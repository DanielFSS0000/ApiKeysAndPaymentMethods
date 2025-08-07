Feature: Create NEQUI transaction in Wompi

  @Successful
  Scenario: Create a transaction in PENDING status with NEQUI method and valid amount
    When the user creates a NEQUI transaction in Wompi
    Then the response status should be PENDING
    And the payment method should be NEQUI
