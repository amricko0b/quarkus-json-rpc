package xyz.amricko0b.quarkus.jsonrpc.runtime;

/**
 * A proxy interface that delegates request handling to actual method annotated with @JsonRpcHandler
 */
public interface JsonRpcMethodInvoker {
  /**
   * @param params JSON-RPC request params (deserialized)
   * @return method execution result
   */
  Object invoke(Object... params) throws Exception;

  /**
   * @return JSON-RPC method name
   */
  String getMethodName();
}
