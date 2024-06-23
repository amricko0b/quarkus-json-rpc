package xyz.amricko0b.quarkus.jsonrpc.serde;

import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcRequest;
import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcResponse;
import xyz.amricko0b.quarkus.jsonrpc.meta.JsonRpcParamsMeta;

/**
 * Request and response serialization protocol. Must be implemented for any supported serialization
 * library.
 */
public interface JsonRpcSerde {

  /**
   * @param requestString raw request from the transport
   * @return request model deserialized from JSON-string
   */
  JsonRpcRequest deserializeRequest(String requestString);

  /**
   * @param request request to be processed
   * @param paramsMeta method params metadata.Exists if request is supported.
   * @return array of parameters to be fed to invoker
   */
  Object[] prepareParamsForInvoker(JsonRpcRequest request, JsonRpcParamsMeta paramsMeta);

  /**
   * @param response response object to be sent
   * @return raw response
   */
  String serializeResposeToString(JsonRpcResponse response);
}
