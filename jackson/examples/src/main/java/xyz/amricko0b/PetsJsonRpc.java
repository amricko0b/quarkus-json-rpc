package xyz.amricko0b;

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

  @JsonRpcMethod(name = "pets.create")
  public UUID createPet(@JsonRpcParam(name = "name", required = true) String name) {
    var id = UUID.randomUUID();
    var pet = new Pet(id, name, LocalDate.now());
    PETS.put(id, pet);
    return id;
  }

  @JsonRpcMethod(name = "pets.vaccinate")
  public Pet vaccinate(
      @JsonRpcParam(name = "id", required = true) UUID id,
      @JsonRpcParam(name = "vaccination_date") LocalDate date) {
    var pet = PETS.get(id);
    if (pet == null) {
      throw new NoSuchPetException(id);
    }

    if (date == null) {
      pet.vaccinate();
    } else {
      pet.vaccinate(date);
    }

    PETS.replace(id, pet);
    return pet;
  }

  @JsonRpcMethod(name = "pets.update")
  public void updatePet(@JsonRpcParams Pet pet) {
    if (!PETS.containsKey(pet.getId())) {
      throw new NoSuchPetException(pet.getId());
    }

    PETS.replace(pet.getId(), pet);
  }

  @JsonRpcMethod(name = "pets.get_all")
  public List<Pet> getAllPets() {
    return PETS.values().stream().toList();
  }
}
