package com.petservice.main.common.exception;

public class PaymentRequiredException extends RuntimeException {
  public PaymentRequiredException(String message) {
    super(message);
  }
}
