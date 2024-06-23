package xyz.amricko0b.quarkus.jsonrpc.annotation;

/** Annotation to mark bean methods those are in charge to handle JSON-RPC method invocations. */
public @interface JsonRpcMethod {

  /**
   * @return name of handled JSON-RPC method
   */
  String name();
}
