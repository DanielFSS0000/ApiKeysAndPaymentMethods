package co.com.wompi.api.utils;

public class ReferenceGenerator {

    // Genera una referencia random usando solo un número aleatorio de 6 dígitos
    public static String randomReference() {
        int randomNum = (int) (Math.random() * 1000000);
        return "reference" + randomNum;
    }
}