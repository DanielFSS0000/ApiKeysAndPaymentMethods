#author: Daniel Sandoval
#language: en

Feature: Query merchant information on Wompi

  @Successful
  Scenario: Successful query of the merchant using the sandbox public key
    When the user queries the Wompi merchant with the public key
    Then it validates that the response is successfull
    And the field data.public_key must be "pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7"