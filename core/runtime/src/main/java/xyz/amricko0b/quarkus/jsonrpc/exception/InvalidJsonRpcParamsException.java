package xyz.amricko0b.quarkus.jsonrpc.exception;

import lombok.experimental.StandardException;

/** This exception means that request contains unsupported or invalid params */
@StandardException
public class InvalidJsonRpcParamsException extends RuntimeException {}
