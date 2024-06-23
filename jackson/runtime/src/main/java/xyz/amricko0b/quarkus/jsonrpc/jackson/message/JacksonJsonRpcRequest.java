package xyz.amricko0b.quarkus.jsonrpc.jackson.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcParams;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcRequest;

@Getter
public class JacksonJsonRpcRequest implements JsonRpcRequest {

  @JsonProperty(value = "jsonrpc", required = true)
  private String version;

  @JsonProperty private String id;

  @JsonProperty(required = true)
  private String method;

  @JsonProperty(value = "params")
  private JsonNode paramsNode;

  @JsonIgnore
  public JsonRpcParams getParams() {
    return new JacksonJsonRpcParams(this.paramsNode);
  }
}
