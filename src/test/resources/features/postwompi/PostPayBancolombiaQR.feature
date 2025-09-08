#author: Daniel Sandoval
#language: en

Feature: Create and validate approved Bancolombia QR transaction in Wompi

  @Successful
  Scenario: Create and validate an approved Bancolombia QR transaction
    When the user creates a Bancolombia QR transaction in Wompi
    Then the QR response status should be "PENDING"
    And after checking the transaction by ID, the status should be "APPROVED"
    And the payment method QR should be "BANCOLOMBIA_QR"