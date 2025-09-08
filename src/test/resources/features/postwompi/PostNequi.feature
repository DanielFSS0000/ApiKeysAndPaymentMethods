#author: Daniel Sandoval
#language: en

Feature: Create NEQUI transaction in Wompi

  @Successful
  Scenario: Create a transaction with NEQUI payment method
    When the user creates a NEQUI transaction in Wompi
    Then the response status should be "PENDING"
    And after a few minutes the user validates the "APPROVED" status
    And the payment method should be "NEQUI"