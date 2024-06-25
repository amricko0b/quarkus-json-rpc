package xyz.amricko0b;

import java.util.UUID;
import lombok.Getter;

@Getter
public class NoSuchPetException extends RuntimeException {

  private final UUID petId;

  public NoSuchPetException(UUID petId) {
    super("No such pet");
    this.petId = petId;
  }
}
