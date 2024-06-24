# JSON-RPC Support For Quarkus

This extension provides [JSON-RPC 2.0](https://www.jsonrpc.org/specification) support for Quarkus Framework.

**Please note: this extension is at early development stage. Though I develop it actively, I won't say it is ready-to-use in Production environments. 
There is still a lot of work to do for now.**

**Don't hesitate to reach out and share your issues if there are some.**

## Roadmap

- Server-side support based on Jackson ✅
- Exception mappers
- Live Reload
- Open-RPC generation
- Server-side support based on Jsonb
- Client side

## Server-side Quick Start

1. Start with adding the extension as a dependency
```xml
<dependency>
  <groupId>xyz.amricko0b</groupId>
  <artifactId>quarkus-json-rpc-jackson</artifactId>
  <version>0.0.1</version>
</dependency>
```

2. Define your request handlers
```java

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcMethod;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParam;
import xyz.amricko0b.quarkus.jsonrpc.annotation.JsonRpcParams;

@ApplicationScoped
public class PetsJsonRpc {

  private static final ConcurrentHashMap<UUID, Pet> PETS = new ConcurrentHashMap<>();

  @JsonRpcMethod(name = "pets.create") // Method params may be mapped to Java-method parameters...
  public UUID createPet(@JsonRpcParam(name = "name", required = true) String name) {
    var id = UUID.randomUUID();
    var pet = new Pet(id, name, LocalDate.now());
    PETS.put(id, pet);
    return id;
  }

  @JsonRpcMethod(name = "pets.update") // ... as well as to Pojo
  public void updatePet(@JsonRpcParams Pet pet) {
    if (!PETS.containsKey(pet.getId())) {
      throw new NoSuchPetException("No such pet: " + pet.getId());
    }

    PETS.replace(pet.getId(), pet);
  }

  @JsonRpcMethod(name = "pets.get_all")
  public List<Pet> getAllPets() {
    return PETS.values().stream().toList();
  }
}

```

3. Now you can serve incoming requests from any transport. For instance:
```java
@Path("/api/v1/jsonrpc")
public class JsonRpcResource {

  @Inject JsonRpcMediator jsonRpc;

  @POST
  public String post(String body) {
    return jsonRpc.serve(body);
  }
}
```

**More [examples](jackson/examples) for Jackson**