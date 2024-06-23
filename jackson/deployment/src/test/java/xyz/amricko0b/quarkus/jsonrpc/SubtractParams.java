package xyz.amricko0b.quarkus.jsonrpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParam;

@Getter
public class SubtractParams {

  @JsonProperty("a")
  @JsonRpcParam(name = "a")
  private int a;

  @JsonProperty("b")
  @JsonRpcParam(name = "b")
  private int b;
}
