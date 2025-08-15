#author: Daniel Sandoval
#language: en

Feature: Create PSE transaction in Wompi

  @Successful
  Scenario: Create a PSE transaction Pending path using bank code "1"
    When the user creates a PSE transaction in Wompi with approving bank
    Then the PSE response status should be "PENDING"
    Then the user waits a few minutes to validate the "DECLINED" status
    And the payment method used is "PSE"