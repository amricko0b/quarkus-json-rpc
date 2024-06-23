package xyz.amricko0b.quarkus.jsonrpc.jackson.message;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcParams;

@Getter
@RequiredArgsConstructor
public class JacksonJsonRpcParams implements JsonRpcParams {

  private final JsonNode node;

  @Override
  public String getParamsAsJsonString() {
    return node.toString();
  }

  @Override
  public String getParamByNameAsString(String name) {

    var nodeValue = node.get(name);
    return nodeValue != null ? nodeValue.toString() : null;
  }
}
