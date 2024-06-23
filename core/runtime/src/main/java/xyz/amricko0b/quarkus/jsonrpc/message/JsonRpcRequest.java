package xyz.amricko0b.quarkus.jsonrpc.message;

/** Generic model for incoming JSON-RPC requests */
public interface JsonRpcRequest {

  /**
   * @return version assumed for request
   */
  String getVersion();

  /**
   * @return request-response correlation ID
   */
  String getId();

  /**
   * @return name of the method to be called
   */
  String getMethod();

  /**
   * @return generic params structure
   */
  JsonRpcParams getParams();
}
