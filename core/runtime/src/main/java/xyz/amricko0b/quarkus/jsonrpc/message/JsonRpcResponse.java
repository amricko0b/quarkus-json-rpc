package xyz.amricko0b.quarkus.jsonrpc.message;

import lombok.Getter;

/** Generic model for outgoing JSON-RPC response object */
@Getter
public class JsonRpcResponse {

  private final String jsonrpc = "2.0";
  private final String id;
  private Object result;
  private JsonRpcError error;

  public JsonRpcResponse(String id, Object result, JsonRpcError error) {
    this.id = id;
    this.result = result;
    this.error = error;
  }
}
