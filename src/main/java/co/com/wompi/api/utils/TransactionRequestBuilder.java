package co.com.wompi.api.utils;

import co.com.wompi.api.models.BancolombiaQRTransactionRequest;
import co.com.wompi.api.models.NequiTransactionRequest;
import co.com.wompi.api.models.PcolTransactionRequest;
import co.com.wompi.api.models.PseTransactionRequest;

public final class TransactionRequestBuilder {
    private TransactionRequestBuilder() {
    }

    //******** BANCOLOMBIA QR **********
    public static BancolombiaQRTransactionRequest bancolombiaQr(String acceptanceToken, String email,
                                                                String paymentDescription, String sandboxStatus,
                                                                int amountInCents, String currency) {
        BancolombiaQRTransactionRequest req = new BancolombiaQRTransactionRequest();
        req.amount_in_cents = amountInCents;
        req.currency = currency;
        req.customer_email = email;
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = acceptanceToken;
        req.payment_method = new BancolombiaQRTransactionRequest.PaymentMethod();
        req.payment_method.payment_description = paymentDescription;
        req.payment_method.sandbox_status = sandboxStatus;
        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference, req.amount_in_cents, req.currency, Constants.INTEGRITY_KEY_WOMPI
        );
        return req;
    }

    //******** NEQUI **********
    public static NequiTransactionRequest nequi(String acceptanceToken, String email, String phone,
                                                int amountInCents, String currency) {
        NequiTransactionRequest req = new NequiTransactionRequest();
        req.amount_in_cents = amountInCents;
        req.currency = currency;
        req.customer_email = email;
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = acceptanceToken;
        req.payment_method = new NequiTransactionRequest.PaymentMethod();
        req.payment_method.phone_number = phone;

        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference, req.amount_in_cents, req.currency, Constants.INTEGRITY_KEY_WOMPI
        );
        return req;
    }

    //******** PCOL **********
    public static PcolTransactionRequest pcol(String acceptanceToken, String email, String sandboxStatus,
                                              int amountInCents, String currency) {
        PcolTransactionRequest req = new PcolTransactionRequest();
        req.amount_in_cents = amountInCents;
        req.currency = currency;
        req.customer_email = email;
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = acceptanceToken;
        req.payment_method = new PcolTransactionRequest.PaymentMethod();
        req.payment_method.sandbox_status = sandboxStatus;

        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference, req.amount_in_cents, req.currency, Constants.INTEGRITY_KEY_WOMPI
        );
        return req;
    }
    //******** PSE **********
    public static PseTransactionRequest pse(String acceptanceToken, String email, int amountInCents, String currency,
                                            int userType, String legalIdType, String legalId,
                                            String finInstitutionCode, String paymentDescription) {
        PseTransactionRequest req = new PseTransactionRequest();
        req.amount_in_cents = amountInCents;
        req.currency = currency;
        req.customer_email = email;
        req.reference = ReferenceGenerator.randomNequiReference();
        req.acceptance_token = acceptanceToken;

        req.payment_method = new PseTransactionRequest.PaymentMethod();
        req.payment_method.type = "PSE";
        req.payment_method.user_type = userType;
        req.payment_method.user_legal_id_type = legalIdType;
        req.payment_method.user_legal_id = legalId;
        req.payment_method.financial_institution_code = finInstitutionCode; // "1" approve, "2" decline (sandbox)
        req.payment_method.payment_description = paymentDescription;

        req.signature = SignatureUtils.calcularFirmaIntegridad(
                req.reference, req.amount_in_cents, req.currency, Constants.INTEGRITY_KEY_WOMPI
        );
        return req;
    }
}
