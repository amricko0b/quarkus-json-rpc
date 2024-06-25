package xyz.amricko0b;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcExceptionMapper;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcError;

@ApplicationScoped
public class JsonRpcExceptionMappers {

  @JsonRpcExceptionMapper
  public JsonRpcError mapException(NoSuchPetException e) {
    return new JsonRpcError(4040, e.getMessage(), Map.of("petId", e.getPetId()));
  }
}
