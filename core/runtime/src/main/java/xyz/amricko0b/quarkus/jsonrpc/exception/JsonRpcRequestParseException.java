package xyz.amricko0b.quarkus.jsonrpc.exception;

import lombok.experimental.StandardException;

/** This exception means request does not contain valid JSON-RPC request object */
@StandardException
public class JsonRpcRequestParseException extends RuntimeException {}
