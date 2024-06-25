package xyz.amricko0b.quarkus.jsonrpc;

import jakarta.enterprise.context.ApplicationScoped;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcExceptionMapper;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcMethod;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParam;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParams;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcError;

/** An example of JSON-RPC responsible bean used in tests. */
@ApplicationScoped
public class MathJsonRpcHandlers {

  /** Example of simple params usage. Every param from request mapped on method parameter */
  @JsonRpcMethod(name = "add")
  public int add(@JsonRpcParam(name = "a") int a, @JsonRpcParam(name = "b") int b) {
    return a + b;
  }

  /**
   * Example of params object. The entire "param" JSON-object from request mapped on class instance.
   */
  @JsonRpcMethod(name = "subtract")
  public SubtractResult subtract(@JsonRpcParams SubtractParams params) {
    return new SubtractResult(params.getA() - params.getB());
  }

  /**
   * Example of params object. The entire "param" JSON-object from request mapped on class instance.
   */
  @JsonRpcMethod(name = "divide")
  public float divide(@JsonRpcParam(name = "a") int a, @JsonRpcParam(name = "b") int b) {

    if (b == 0) {
      throw new ArithmeticException("Division by zero");
    }
    return (float) a / b;
  }

  @JsonRpcExceptionMapper
  public JsonRpcError arithmeticException(ArithmeticException ex) {
    return new JsonRpcError(4040, ex.getMessage());
  }
}
