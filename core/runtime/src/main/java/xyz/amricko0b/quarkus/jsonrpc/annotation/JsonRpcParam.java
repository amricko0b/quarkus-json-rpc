package xyz.amricko0b.quarkus.jsonrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** JSON-RPC param to Java Method parameter mapping */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface JsonRpcParam {

  /**
   * @return param name (as in request)
   */
  String name();

  /**
   * @return determines if the param cannot be null
   */
  boolean required() default false;
}
