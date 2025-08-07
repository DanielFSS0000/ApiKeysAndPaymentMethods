#language: en
@Successful
Feature: Create transaction with Bancolombia QR in Wompi

  Scenario: Create an approved transaction using Bancolombia QR method
    When the user creates a Bancolombia QR transaction in Wompi with PENDING status
    Then the response status should be "PENDING"
    And the payment method should be "BANCOLOMBIA_QR"
