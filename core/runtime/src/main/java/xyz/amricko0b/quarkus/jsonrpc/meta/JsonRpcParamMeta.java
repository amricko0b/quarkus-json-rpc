package xyz.amricko0b.quarkus.jsonrpc.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Simple param metadata */
@Getter
@RequiredArgsConstructor
public final class JsonRpcParamMeta {

  /** Param name */
  private final String name;

  /** Param class */
  private final Class<?> paramClass;

  /** Param cannot be null? */
  private final boolean required;
}
