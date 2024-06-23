package xyz.amricko0b.quarkus.jsonrpc.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import java.io.IOException;
import xyz.amricko0b.quarkus.jsonrpc.exception.InvalidJsonRpcParamsException;
import xyz.amricko0b.quarkus.jsonrpc.exception.JsonRpcRequestParseException;
import xyz.amricko0b.quarkus.jsonrpc.exception.JsonRpcResponseCreationException;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcRequest;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcResponse;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcParamsMeta;
import xyz.amricko0b.quarkus.jsonrpc.serde.JsonRpcSerde;
import xyz.amricko0b.quarkus.jsonrpc.jackson.message.JacksonJsonRpcRequest;

public class JacksonJsonRpcSerde implements JsonRpcSerde {

  @Inject ObjectMapper objectMapper;

  @Override
  public JsonRpcRequest deserializeRequest(String requestString) {
    try {
      return objectMapper.readValue(requestString, JacksonJsonRpcRequest.class);
    } catch (IOException e) {
      throw new JsonRpcRequestParseException(e);
    }
  }

  @Override
  public Object[] prepareParamsForInvoker(JsonRpcRequest request, JsonRpcParamsMeta paramsMeta) {
    try {
      if (paramsMeta.noParamsExpected()) {
        return new Object[0];
      }

      if (paramsMeta.paramsExpectedAsObject()) {
        var paramsObject =
            objectMapper.readValue(
                request.getParams().getParamsAsJsonString(), paramsMeta.getParamsObjectClass());
        return new Object[] {paramsObject};
      }

      var paramMetas = paramsMeta.getParamMetas();
      var handlerParams = new Object[paramMetas.size()];
      var idx = 0;
      for (var paramMeta : paramMetas) {
        var paramValue = request.getParams().getParamByNameAsString(paramMeta.getName());

        // Nullable params are possible
        if (paramValue != null) {
          var deserialized = objectMapper.readValue(paramValue, paramMeta.getParamClass());
          handlerParams[idx] = deserialized;
        } else {

          if (paramMeta.isRequired()) {
            throw new InvalidJsonRpcParamsException(
                "Param \"" + paramMeta.getName() + "\" is required but missing in request");
          }

          handlerParams[idx] = null;
        }

        idx++;
      }

      return handlerParams;
    } catch (JsonProcessingException ex) {
      throw new InvalidJsonRpcParamsException(ex);
    }
  }

  @Override
  public String serializeResposeToString(JsonRpcResponse response) {
    try {
      return objectMapper.writeValueAsString(response);
    } catch (JsonProcessingException ex) {
      throw new JsonRpcResponseCreationException(ex);
    }
  }
}
