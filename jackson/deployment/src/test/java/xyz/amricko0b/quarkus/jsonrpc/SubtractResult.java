package xyz.amricko0b.quarkus.jsonrpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SubtractResult {

  @JsonProperty("value")
  private int value;

  public SubtractResult(int value) {
    this.value = value;
  }
}
