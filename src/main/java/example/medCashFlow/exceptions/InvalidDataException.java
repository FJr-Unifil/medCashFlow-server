package example.medCashFlow.exceptions;

import lombok.Getter;

@Getter
public class InvalidDataException extends RuntimeException {

  private final String entity;

  private final String propertyName;

  private final String value;

  public InvalidDataException(String entity, String propertyName, String value) {
    this.entity = entity;
    this.propertyName = propertyName;
    this.value = value;
  }

  @Override
  public String getMessage() {
    return String.format("%s already exists with {%s: %s}", entity, propertyName, value);
  }

}
