package xyz.amricko0b;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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

  public Pet(UUID id, String name, LocalDate birthDate) {
    this.id = id;
    this.name = name;
    this.birthDate = birthDate;
    this.vaccinated = false;
  }

  public void vaccinate() {
    this.vaccinated = true;
  }

  public void vaccinate(LocalDate vaccinationDate) {
    this.vaccinated = true;
    this.vaccinationDate = vaccinationDate;
  }
}
