#author: Daniel Sandoval
#language: en
@Negative
  Feature:Query merchant information with invalid key
Scenario: Failed query of the merchant using an invalid public key
When the user queries the Wompi merchant with an invalid public key
Then it validates that the response code is 422
And the error message should be "Formato inválido"
