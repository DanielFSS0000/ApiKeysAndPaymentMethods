#author: Daniel Sandoval
#language: en

Feature:Query merchant information with invalid key

  @Negative
  Scenario: Consultation of merchants is not possible due to invalid public key format.
    When the user queries the Wompi merchant with an invalid public key
    Then it validates that the response code is 422
    And the error message should be "Formato inválido"
