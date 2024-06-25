package xyz.amricko0b.quarkus.jsonrpc.runtime;

import xyz.amricko0b.quarkus.jsonrpc.message.JsonRpcError;

public interface JsonRpcExceptionMapperInvoker {

  JsonRpcError invoke(Throwable exception);

  Class<?> getSupportedClass();
}
