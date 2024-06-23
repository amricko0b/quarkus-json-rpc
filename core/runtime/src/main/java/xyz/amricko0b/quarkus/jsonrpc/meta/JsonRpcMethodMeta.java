package xyz.amricko0b.quarkus.jsonrpc.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Supported JSON-RPC method metadata. */
@Getter
@RequiredArgsConstructor
public final class JsonRpcMethodMeta {

  /** JSON-RPC method name. May differ from the name of Java-method. */
  private final String name;

  /** Params metadata */
  private final JsonRpcParamsMeta params;
}
