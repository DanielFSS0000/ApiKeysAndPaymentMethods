package co.com.wompi.api.models;

public class NequiTransactionRequest {
    public int amount_in_cents;
    public String currency;
    public String customer_email;
    public PaymentMethod payment_method;
    public String reference;
    public String acceptance_token;
    public String signature;

    public static class PaymentMethod {
        public String type = "NEQUI";
        public String phone_number;
    }
}