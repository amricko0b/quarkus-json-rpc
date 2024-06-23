package xyz.amricko0b.quarkus.jsonrpc.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** JSON-RPC error object */
@Getter
@AllArgsConstructor
public class JsonRpcError {

  public static final JsonRpcError PARSE_ERROR = new JsonRpcError(-32700, "Parse error");
  public static final JsonRpcError METHOD_NOT_FOUND = new JsonRpcError(-32601, "Method not found");
  public static final JsonRpcError INVALID_PARAMS = new JsonRpcError(-32602, "Invalid params");
  public static final JsonRpcError INTERNAL_ERROR = new JsonRpcError(-32603, "Internal error");

  private final int code;
  private final String message;

  /**
   * Error details. Assumed to be application specific, so the only option is to leave this as
   * Object
   */
  private Object data;

  /** For reserved error objects data is optional */
  public JsonRpcError(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
