package xyz.amricko0b.quarkus.jsonrpc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import xyz.amricko0b.quarkus.jsonrpc.JsonRpcMediator;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParam;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcError;

public class ExtensionTests {

  @RegisterExtension
  static final QuarkusUnitTest config =
      new QuarkusUnitTest()
          .withApplicationRoot(
              jar ->
                  jar.addClass(MathJsonRpcHandlers.class)
                      .addClass(SubtractParams.class)
                      .addClass(SubtractResult.class)
                      .addClass(JsonRpcParam.class));

  @Inject ObjectMapper objectMapper;
  @Inject JsonRpcMediator jsonRpc;

  @Test
  @SneakyThrows
  public void testAdd() {

    var req =
        """
              {
                "jsonrpc": "2.0",
                "id": "904f5c43-4c53-4092-b809-3819e2d95f16",
                "method": "add",
                "params": {
                  "a": 5,
                  "b": 4
                }
              }""";

    var resp = jsonRpc.serve(req);

    var respTree = objectMapper.readTree(resp);
    assertEquals("904f5c43-4c53-4092-b809-3819e2d95f16", respTree.get("id").asText());
    assertEquals(9, respTree.get("result").asInt());
  }

  @Test
  @SneakyThrows
  public void testSubtract() {

    var req =
        """
              {
                "jsonrpc": "2.0",
                "id": "904f5c43-4c53-4092-b809-3819e2d95f16",
                "method": "subtract",
                "params": {
                  "a": 5,
                  "b": 4
                }
              }""";

    var resp = jsonRpc.serve(req);
    var respTree = objectMapper.readTree(resp);
    assertEquals("904f5c43-4c53-4092-b809-3819e2d95f16", respTree.get("id").asText());
    assertEquals(1, respTree.get("result").get("value").asInt());
  }

  @Test
  @SneakyThrows
  public void testDivide() {

    var req =
        """
              {
                "jsonrpc": "2.0",
                "id": "904f5c43-4c53-4092-b809-3819e2d95f16",
                "method": "divide",
                "params": {
                  "a": 5,
                  "b": 0
                }
              }""";

    var resp = jsonRpc.serve(req);
    var respTree = objectMapper.readTree(resp);
    assertEquals("904f5c43-4c53-4092-b809-3819e2d95f16", respTree.get("id").asText());
    assertEquals(JsonRpcError.INTERNAL_ERROR.getCode(), respTree.get("error").get("code").asInt());
    assertEquals(
        JsonRpcError.INTERNAL_ERROR.getMessage(), respTree.get("error").get("message").asText());
  }
}
