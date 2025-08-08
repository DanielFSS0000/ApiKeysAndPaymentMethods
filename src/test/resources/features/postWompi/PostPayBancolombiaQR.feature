#author: Daniel Sandoval
#language: en

Feature: Create transaction with Bancolombia QR in Wompi

  @Successful
  Scenario: Create an approved transaction using Bancolombia QR method
    When the user creates a Bancolombia QR transaction in Wompi with PENDING status
    Then Then the QR response status should be "PENDING"
    And the payment method QR should be "BANCOLOMBIA_QR"
