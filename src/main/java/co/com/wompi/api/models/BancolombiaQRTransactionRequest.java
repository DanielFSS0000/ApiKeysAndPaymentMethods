package co.com.wompi.api.models;

public class BancolombiaQRTransactionRequest {
    public int amount_in_cents;
    public String currency;
    public String customer_email;
    public PaymentMethod payment_method;
    public String reference;
    public String acceptance_token;
    public String signature;

    public static class PaymentMethod {
        public String type = "BANCOLOMBIA_QR";
        public String payment_description;
        public String sandbox_status;
    }
}
