package xyz.amricko0b.quarkus.jsonrpc.exception;

import lombok.experimental.StandardException;

/** This exception means that serde was unable to serialize response object */
@StandardException
public class JsonRpcResponseCreationException extends RuntimeException {}
