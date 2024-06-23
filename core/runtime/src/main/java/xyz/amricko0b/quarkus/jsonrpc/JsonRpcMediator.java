package xyz.amricko0b.quarkus.jsonrpc;

import java.util.Map;
import lombok.Setter;
import xyz.amricko0b.quarkus.jsonrpc.exception.InvalidJsonRpcParamsException;
import xyz.amricko0b.quarkus.jsonrpc.exception.JsonRpcRequestParseException;
import xyz.amricko0b.quarkus.jsonrpc.exception.JsonRpcResponseCreationException;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcError;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcResponse;
import xyz.amricko0b.quarkus.jsonrpc.runtime.JsonRpcMethodInvoker;
import io.quarkus.arc.All;
import jakarta.inject.Inject;
import java.util.List;
import lombok.extern.jbosslog.JBossLog;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcMethodMeta;
import xyz.amricko0b.quarkus.jsonrpc.serde.JsonRpcSerde;

/**
 * The First-Class API of the extension.
 *
 * <p>Extensions's user may inject this bean to serve requests.
 *
 * <p>While JSON-RPC is a transport-agnostic protocol, this extension does not provide any
 * transport. It is up to extenstion's user to choose between HTTP or WS or whatever...
 */
@JBossLog
public class JsonRpcMediator {

  /** Invokers to delegate method handling */
  @Inject @All List<JsonRpcMethodInvoker> invokers;

  /** Serialization protocol */
  @Inject JsonRpcSerde serde;

  /** Supported methods metadata */
  @Setter List<JsonRpcMethodMeta> methodMetas;

  /**
   * Serve JSON-string request from transport
   *
   * @param requestString request as JSON-string
   * @return response as JSON-string
   */
  public String serve(String requestString) {

    String requestId = null;
    try {
      var request = serde.deserializeRequest(requestString);
      requestId = request.getId();

      var methodMeta =
          methodMetas.stream().filter(in -> in.getName().equals(request.getMethod())).findFirst();
      if (methodMeta.isEmpty()) {
        return serde.serializeResposeToString(
            new JsonRpcResponse(requestId, null, JsonRpcError.METHOD_NOT_FOUND));
      }

      var invokerParams = serde.prepareParamsForInvoker(request, methodMeta.get().getParams());

      var invoker =
          invokers.stream()
              .filter(inv -> inv.getMethodName().equals(request.getMethod()))
              .findFirst();
      if (invoker.isEmpty()) {
        return serde.serializeResposeToString(
            new JsonRpcResponse(requestId, null, JsonRpcError.METHOD_NOT_FOUND));
      }

      try {
        var result = invoker.get().invoke(invokerParams);

        if (result != null) {
          return serde.serializeResposeToString(new JsonRpcResponse(requestId, result, null));
        } else {
          // Result must never be null for successful requests, though empty objects are supported.
          // Using empty map as result will infer empty JSON-object.
          return serde.serializeResposeToString(new JsonRpcResponse(requestId, Map.of(), null));
        }

      } catch (Exception ex) {
        log.error("Error during JSON-RPC request handling", ex);
        return serde.serializeResposeToString(
            new JsonRpcResponse(requestId, null, JsonRpcError.INTERNAL_ERROR));
      }

    } catch (JsonRpcRequestParseException ex) {
      log.error("Unable to deserialize JSON-RPC request", ex);
      return serde.serializeResposeToString(
          new JsonRpcResponse(null, null, JsonRpcError.PARSE_ERROR));
    } catch (InvalidJsonRpcParamsException ex) {
      log.error("Invalid JSON-RPC request params", ex);
      return serde.serializeResposeToString(
          new JsonRpcResponse(requestId, null, JsonRpcError.INVALID_PARAMS));
    } catch (JsonRpcResponseCreationException ex) {
      log.error("Unable to create response object", ex);
      return serde.serializeResposeToString(
          new JsonRpcResponse(requestId, null, JsonRpcError.INTERNAL_ERROR));
    }
  }
}
