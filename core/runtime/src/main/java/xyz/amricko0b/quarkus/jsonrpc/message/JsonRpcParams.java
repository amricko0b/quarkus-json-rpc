package xyz.amricko0b.quarkus.jsonrpc.message;

public interface JsonRpcParams {

  String getParamsAsJsonString();

  String getParamByNameAsString(String name);
}
