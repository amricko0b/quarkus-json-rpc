package xyz.amricko0b.quarkus.jsonrpc.deployment.factory;

import io.quarkus.gizmo.ClassCreator;
import io.quarkus.gizmo.ClassOutput;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.reflect.Modifier;
import lombok.experimental.UtilityClass;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type.Kind;
import xyz.amricko0b.quarkus.jsonrpc.runtime.JsonRpcMethodInvoker;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcMethodMeta;

/**
 * Utility for invokers generation.
 *
 * <p>Invoker is a synthetic bean, that is used to decouple user-defined @JsonRpcHandler method from
 * extension's internals.
 */
@UtilityClass
public class JsonRpcMethodInvokerFactory {

  /**
   * Generate invokers byte-code to specified class output
   *
   * @param jsonRpcMethod information about JSON-RPC method
   * @param javaHandlerMethod information about Java method that handles JSON-RPC method
   * @param classOutput output for byte-code
   */
  public void generate(
      JsonRpcMethodMeta jsonRpcMethod, MethodInfo javaHandlerMethod, ClassOutput classOutput) {

    var declaringClassName = javaHandlerMethod.declaringClass().name().toString();
    var invokerName =
        declaringClassName + "$" + javaHandlerMethod.name() + "$JsonRpcMethodInvoker$";

    try (var invoker =
        ClassCreator.builder()
            .classOutput(classOutput)
            .className(invokerName)
            .interfaces(JsonRpcMethodInvoker.class)
            .build()) {

      // Field to inject carrier bean instance
      var beanInstance =
          invoker.getFieldCreator("beanInstance", declaringClassName).getFieldDescriptor();

      // Constructor for injection
      var constructor = invoker.getMethodCreator("<init>", void.class, beanInstance.getType());
      constructor.addAnnotation(Inject.class);
      constructor.setModifiers(Modifier.PUBLIC);
      constructor.invokeSpecialMethod(
          MethodDescriptor.ofConstructor(Object.class), constructor.getThis());
      constructor.writeInstanceField(
          beanInstance, constructor.getThis(), constructor.getMethodParam(0));
      constructor.returnValue(null);

      // Method to choose suitable invoker in runtime
      var getMethodName = invoker.getMethodCreator("getMethodName", String.class);
      getMethodName.returnValue(getMethodName.load(jsonRpcMethod.getName()));

      // Invoke method to delegate request handling
      var invoke = invoker.getMethodCreator("invoke", Object.class, Object[].class);
      var invokeParams = invoke.getMethodParam(0);
      var paramsCount = javaHandlerMethod.parametersCount();
      var invokeArgs = new ResultHandle[paramsCount];
      for (var paramIndex = 0; paramIndex < paramsCount; paramIndex++) {
        invokeArgs[paramIndex] = invoke.readArrayValue(invokeParams, paramIndex);
      }
      var invokeResult =
          invoke.invokeVirtualMethod(
              javaHandlerMethod,
              invoke.readInstanceField(beanInstance, invoke.getThis()),
              invokeArgs);

      // Invoker always returns
      if (javaHandlerMethod.returnType().kind() != Kind.VOID) {
        invoke.returnValue(invokeResult);
      } else {
        invoke.returnNull();
      }

      // Make invoker visible as a bean
      invoker.addAnnotation(Singleton.class);
    }
  }
}
