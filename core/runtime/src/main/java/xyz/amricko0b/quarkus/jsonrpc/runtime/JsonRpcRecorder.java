package xyz.amricko0b.quarkus.jsonrpc.runtime;

import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;
import java.util.List;
import xyz.amricko0b.quarkus.jsonrpc.JsonRpcMediator;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcMethodMeta;

@Recorder
public class JsonRpcRecorder {

  public BeanContainerListener setMethodMetas(List<JsonRpcMethodMeta> methodInfos) {
    return beanContainer -> {
      var mediator = beanContainer.beanInstance(JsonRpcMediator.class);
      mediator.setMethodMetas(methodInfos);
    };
  }
}
