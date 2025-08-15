package co.com.wompi.api.models;

public class PseTransactionRequest {
    public int amount_in_cents;
    public String currency;
    public String customer_email;
    public PaymentMethod payment_method;
    public String reference;
    public String acceptance_token;
    public String signature;

    public static class PaymentMethod {
        public String type = "PSE";
        public int user_type;
        public String user_legal_id_type;
        public String user_legal_id;
        public String financial_institution_code;
        public String payment_description;
    }
}