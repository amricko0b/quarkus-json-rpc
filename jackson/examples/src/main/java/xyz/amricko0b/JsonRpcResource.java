package xyz.amricko0b;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import xyz.amricko0b.quarkus.jsonrpc.JsonRpcMediator;

@Path("/api/v1/jsonrpc")
public class JsonRpcResource {

  @Inject JsonRpcMediator jsonRpc;

  @POST
  public String post(String body) {
    return jsonRpc.serve(body);
  }
}
