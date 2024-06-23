package xyz.amricko0b.quarkus.jsonrpc.meta;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON-RPC method params metadata.
 *
 * <p>Mapping params to Java-object is supported as well as simple params (each param mapped on
 * suitable java-method parameter)
 */
@Getter
@Setter
@NoArgsConstructor
public final class JsonRpcParamsMeta {

  /** To store simple params. Makes paramsObjectClass useless */
  private List<JsonRpcParamMeta> paramMetas;

  /** To store complex params-object class. Makes paramMetas useless */
  private Class<?> paramsObjectClass;

  /** Create params-object meta */
  public JsonRpcParamsMeta(Class<?> paramsObjectClass) {
    this.paramsObjectClass = paramsObjectClass;
  }

  /** Create simple params meta */
  public JsonRpcParamsMeta(List<JsonRpcParamMeta> paramsArray) {
    this.paramMetas = paramsArray;
  }

  /**
   * @return does method expect parameters to be called?
   */
  public boolean noParamsExpected() {
    return (paramMetas == null || paramMetas.isEmpty()) && paramsObjectClass == null;
  }

  /**
   * @return does method support params object mapping?
   */
  public boolean paramsExpectedAsObject() {
    return paramsObjectClass != null && paramMetas == null;
  }
}
