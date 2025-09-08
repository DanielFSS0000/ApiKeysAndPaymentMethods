#author: Daniel Sandoval
#language: en

Feature: Create transaction with Puntos Colombia (PCOL) in Wompi

  @Negative
  Scenario: Fail to create a PCOL transaction when payment identity is not configured
    When the user creates a PCOL transaction in Wompi with APPROVED_ONLY_POINTS status
    Then the response error type should be "NOT_FOUND_ERROR"
    And the error reason should be "No hay una identidad de pago para PCOL configurada para este comercio"