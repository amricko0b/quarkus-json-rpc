package xyz.amricko0b.quarkus.jsonrpc.deployment.factory;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.MethodParameterInfo;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParam;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParams;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcMethodMeta;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcParamMeta;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcParamsMeta;

/** Utility class for JSON-RPC metadata creation. */
@UtilityClass
public class JsonRpcMethodInfoFactory {

  /** Used for simple params */
  private static final DotName JSON_RPC_PARAM = DotName.createSimple(JsonRpcParam.class);

  /** Used to map all params on java object */
  private static final DotName JSON_RPC_PARAMS = DotName.createSimple(JsonRpcParams.class);

  private static final String PARAM_NAME_PROPERTY = "name";
  private static final String PARAM_REQUIRED_PROPERTY = "required";

  /**
   * @param javaMethod java bean method responsible for request handling
   * @param jsonRpcHandlerAnnotation metadata annotation
   * @param index Jandex class index
   * @return supported JSON-RPC method metadata based on handler method and annotations
   */
  public JsonRpcMethodMeta create(
      MethodInfo javaMethod, AnnotationInstance jsonRpcHandlerAnnotation, IndexView index) {

    var methodName = jsonRpcHandlerAnnotation.value("name").asString();
    return new JsonRpcMethodMeta(methodName, createParamsInfo(javaMethod, index));
  }

  /**
   * @param javaMethod java bean method responsible for request handling
   * @return supported JSON-RPC method's params metadata based on handler method signature and
   *     annotations
   */
  public JsonRpcParamsMeta createParamsInfo(MethodInfo javaMethod, IndexView index) {

    // Param-less methods are possible
    if (javaMethod.parametersCount() == 0) {
      return new JsonRpcParamsMeta();
    }

    if (javaMethod.parametersCount() == 1) {

      // Single parameter can be either complex params-object...
      var onlyParameter = javaMethod.parameters().get(0);
      var paramsAnnotation = onlyParameter.annotation(JSON_RPC_PARAMS);
      if (paramsAnnotation != null) {
        return new JsonRpcParamsMeta(loadClass(onlyParameter));
      }

      // ... or simple param
      var paramAnnotation = onlyParameter.annotation(JSON_RPC_PARAM);
      if (paramAnnotation != null) {
        return new JsonRpcParamsMeta(
            List.of(
                new JsonRpcParamMeta(
                    paramAnnotation.value(PARAM_NAME_PROPERTY).asString(),
                    loadClass(onlyParameter),
                    paramAnnotation.valueWithDefault(index, PARAM_REQUIRED_PROPERTY).asBoolean())));
      } else {
        throw new IllegalStateException(
            "Handler method params must be annotated with @JsonRpcParam or @JsonRpcParams");
      }
    }

    List<JsonRpcParamMeta> paramList = new ArrayList<>();
    for (var param : javaMethod.parameters()) {
      var paramAnnotation = param.annotation(JSON_RPC_PARAM);
      if (paramAnnotation != null) {
        paramList.add(
            new JsonRpcParamMeta(
                paramAnnotation.value(PARAM_NAME_PROPERTY).asString(),
                loadClass(param),
                paramAnnotation.valueWithDefault(index, PARAM_REQUIRED_PROPERTY).asBoolean()));
      }
    }

    return new JsonRpcParamsMeta(paramList);
  }

  /**
   * @param parameterInfo method parameter
   * @return class signature
   */
  private Class<?> loadClass(MethodParameterInfo parameterInfo) {

    var className = parameterInfo.type().name().toString();

    switch (className) {
      case "boolean":
        return boolean.class;
      case "byte":
        return byte.class;
      case "short":
        return short.class;
      case "int":
        return int.class;
      case "long":
        return long.class;
      case "float":
        return float.class;
      case "double":
        return double.class;
      case "char":
        return char.class;
      case "void":
        return void.class;
    }

    try {
      return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
