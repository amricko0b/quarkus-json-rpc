# JSON-RPC Support For Quarkus

This extension provides [JSON-RPC 2.0](https://www.jsonrpc.org/specification) support for Quarkus
Framework.

**Please note: this extension is at early development stage. Though I develop it actively, I won't
say it is ready-to-use in Production environments.
There is still a lot of work to do for now.**

**Don't hesitate to reach out and share your issues if there are some.**

## Roadmap

- Server-side support based on Jackson ✅
- Exception mappers ✅
- Live Reload
- Open-RPC generation
- Server-side support based on Jsonb
- Client side
- Reactive Support

## Usage

### Installation

```xml

<dependency>
  <groupId>xyz.amricko0b</groupId>
  <artifactId>quarkus-json-rpc-jackson</artifactId>
  <version>0.0.2</version>
</dependency>
```

Jsonb support coming soon...

### Server side methods

Export your application login using `@JsonRpcMethod`. Define method params via `@JsonRpcParam`

```java

@ApplicationScoped
public class PetService {

  @JsonRpcMethod(name = "pets.create")
  public UUID createPet(@JsonRpcParam(name = "name", required = true) String name) {
    // Your application logic comes here
    return UUID.randomUUID();
  }
}
```

Method's return value will be JSON-RPC `result`.
It is ok for your handler to be `void`. Extension will map `void` to empty JSON-object.

#### What about types?

Everything Jackson can handle is *supposed* to work fine out of the box.

Extension uses common `ObjectMapper` provided by `quarkus-jackson` module, so all predefined
settings will be applied as well as your customizations from `ObjectMapperCustomizer`.

### Params DTOs

Consider this DTO (accessors and constructors omitted for brevity):

```java
public class Pet {

  @JsonProperty(value = "id")
  private UUID id;

  @JsonProperty(value = "pet_name")
  private String name;

  @JsonProperty(value = "birth_date")
  private LocalDate birthDate;

  @JsonProperty(value = "vaccinated")
  private boolean vaccinated;

  @JsonProperty(value = "vaccination_date")
  private LocalDate vaccinationDate;
}
```

Extension allows you to map params to DTO. Use `@JsonRpcParams` (note plural form).

```java

@JsonRpcMethod(name = "pets.update") // ... as well as to Pojo
public void updatePet(@JsonRpcParams Pet pet) {
  // Logic comes here
}
```

Required params won't work this way though. I believe it is possible to use Jakarta Validation, but
I have not test it properly (yet).

## Exception handling

By default, all exceptions that are thrown during handling map to
reserved `-32603 "Internal error"`. Other predefined exceptions are also utilized by extension.

You can create customer mapping if you need to:

```java

@ApplicationScoped
public class JsonRpcExceptionMappers {

  @JsonRpcExceptionMapper
  public JsonRpcError mapException(NoSuchPetException e) {
    return new JsonRpcError(4040, e.getMessage(), Map.of("petId", e.getPetId()));
  }
}
```

Sadly only global mappings are supported for now.

## Transport

JSON-RPC is a transport-agnostic protocol hence you can exchange requests/responses/notifications
via any text-based protocol (HTTP/TCP/AMQP). To make things simple (and to indulge my laziness) this
extension provides no transport as well.

To serve your requests, you need to inject `JsonRpcMediator`. For instance, if you want to export
via HTTP:

```java

@Path("/api/v1/jsonrpc")
public class JsonRpcResource {

  @Inject
  JsonRpcMediator jsonRpc;

  @POST
  public String post(String request) {
    return jsonRpc.serve(request);
  }
}
```

The same seems to be legit for Smallrye Messaging (Kafka, RabbitMQ etc.)

Please note that when using HTTP your application is expected to answer with `200 OK` unless
transport failed.

**More [examples](jackson/examples) for Jackson**