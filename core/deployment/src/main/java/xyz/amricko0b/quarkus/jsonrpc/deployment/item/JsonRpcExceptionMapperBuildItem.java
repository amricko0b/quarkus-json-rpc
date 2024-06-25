package xyz.amricko0b.quarkus.jsonrpc.deployment.item;

import io.quarkus.builder.item.MultiBuildItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

/** Helps to register and process methods for mapping Java exceptions to JSON-RPC error objects */
@Getter
@RequiredArgsConstructor
public final class JsonRpcExceptionMapperBuildItem extends MultiBuildItem {

  /** Mapped exception type info */
  private final Type exceptionType;

  /** Mapping method info */
  private final MethodInfo mapperMethod;
}
