package xyz.amricko0b.quarkus.jsonrpc.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import jakarta.inject.Singleton;
import java.util.List;
import org.jboss.jandex.DotName;
import xyz.amricko0b.quarkus.jsonrpc.JsonRpcMediator;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcExceptionMapper;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcMethod;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParam;
import xyz.amricko0b.quarkus.jsonrpc.deployment.factory.JsonRpcMethodInfoFactory;
import xyz.amricko0b.quarkus.jsonrpc.deployment.factory.InvokerFactory;
import xyz.amricko0b.quarkus.jsonrpc.deployment.item.JsonRpcExceptionMapperBuildItem;
import xyz.amricko0b.quarkus.jsonrpc.deployment.item.JsonRpcHandlerBuildItem;
import xyz.amricko0b.quarkus.jsonrpc.runtime.JsonRpcRecorder;

/** Common build steps those do not depend on serialization specific things */
public class JsonRpcCoreProcessor {

  /** Annotation to mark Java-methods which encapsulate login to handle JSON-RPC requests */
  public static final DotName JSON_RPC_HANDLER = DotName.createSimple(JsonRpcMethod.class);

  public static final DotName JSON_RPC_EXCEPTION_MAPPER =
      DotName.createSimple(JsonRpcExceptionMapper.class);

  /**
   * Explicitly add some classes to jandex index cause some of them are not visible (I believe it
   * happens because of transitive dependency)
   */
  @BuildStep
  AdditionalIndexedClassesBuildItem addAnnotationsToIndex() {
    return new AdditionalIndexedClassesBuildItem(
        DotName.createSimple(JsonRpcParam.class).toString());
  }

  /**
   * Find all methods marked with @JsonRpcHandler. Each handler will form a build item for further
   * processing.
   */
  @BuildStep
  void scanJsonRpcHandlers(
      CombinedIndexBuildItem index, BuildProducer<JsonRpcHandlerBuildItem> jsonRpcHandlers) {

    var jandex = index.getIndex();

    for (var jsonRpcHandlerAnnotation : jandex.getAnnotations(JSON_RPC_HANDLER)) {
      var handlerMethod = jsonRpcHandlerAnnotation.target().asMethod();
      var supportedMethod =
          JsonRpcMethodInfoFactory.create(handlerMethod, jsonRpcHandlerAnnotation, jandex);
      jsonRpcHandlers.produce(new JsonRpcHandlerBuildItem(handlerMethod, supportedMethod));
    }
  }

  /**
   * Find all methods marked with @JsonRpcExceptionMapping. Each mapper will form a build item for
   * further processing.
   */
  @BuildStep
  void scanJsonRpcExceptionMappers(
      CombinedIndexBuildItem index,
      BuildProducer<JsonRpcExceptionMapperBuildItem> jsonRpcExceptionMappers) {

    var jandex = index.getIndex();

    for (var mapperAnnotation : jandex.getAnnotations(JSON_RPC_EXCEPTION_MAPPER)) {
      var javaMethod = mapperAnnotation.target().asMethod();
      var exceptionType = javaMethod.parameterType(0);
      jsonRpcExceptionMappers.produce(
          new JsonRpcExceptionMapperBuildItem(exceptionType, javaMethod));
    }
  }

  /** Generate synthetic invokers to delegate request handling */
  @BuildStep
  void generateInvokers(
      List<JsonRpcHandlerBuildItem> jsonRpcHandlers,
      BuildProducer<GeneratedBeanBuildItem> generatedBeans) {

    var classOutput = new GeneratedBeanGizmoAdaptor(generatedBeans);

    for (var jsonRpcHandler : jsonRpcHandlers) {
      InvokerFactory.generateForMethodHandler(
          jsonRpcHandler.getJsonRpcMethod(), jsonRpcHandler.getBeanMethod(), classOutput);
    }
  }

  /** Generate synthetic invokers to delegate exception mapping */
  @BuildStep
  void generateExceptionMapperInvokers(
      List<JsonRpcExceptionMapperBuildItem> jsonRpcExceptionMappers,
      BuildProducer<GeneratedBeanBuildItem> generatedBeans) {

    var classOutput = new GeneratedBeanGizmoAdaptor(generatedBeans);

    for (var mapper : jsonRpcExceptionMappers) {
      InvokerFactory.generateForExceptionMapper(
          mapper.getExceptionType(), mapper.getMapperMethod(), classOutput);
    }
  }

  /** JSON-RPC method's metadata must be available in runtime */
  @BuildStep
  @Record(ExecutionTime.STATIC_INIT)
  BeanContainerListenerBuildItem populateMethodInfosToMediator(
      List<JsonRpcHandlerBuildItem> jsonRpcHandlers, JsonRpcRecorder recorder) {
    return new BeanContainerListenerBuildItem(
        recorder.setMethodMetas(
            jsonRpcHandlers.stream().map(JsonRpcHandlerBuildItem::getJsonRpcMethod).toList()));
  }

  @BuildStep
  AdditionalBeanBuildItem registerMediator() {
    return AdditionalBeanBuildItem.builder()
        .addBeanClasses(JsonRpcMediator.class)
        .setDefaultScope(DotName.createSimple(Singleton.class))
        .build();
  }
}
