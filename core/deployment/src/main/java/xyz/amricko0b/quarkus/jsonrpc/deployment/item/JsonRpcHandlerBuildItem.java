package xyz.amricko0b.quarkus.jsonrpc.deployment.item;

import io.quarkus.builder.item.MultiBuildItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jboss.jandex.MethodInfo;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcMethodMeta;

/** Helps to register and process JSON-RPC methods supported and handled by the application. */
@Getter
@RequiredArgsConstructor
public final class JsonRpcHandlerBuildItem extends MultiBuildItem {

  /** Info of java handler-method */
  private final MethodInfo beanMethod;

  /** Handled JSON-RPC method info */
  private final JsonRpcMethodMeta jsonRpcMethod;
}
