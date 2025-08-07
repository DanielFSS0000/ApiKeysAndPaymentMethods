package co.com.wompi.api.utils;

public class ReferenceGenerator {

    // Genera una referencia random usando solo un número aleatorio de 6 dígitos
    public static String randomNequiReference() {
        int randomNum = (int) (Math.random() * 1000000); // entre 0 y 999999
        return "pedido-nequi-" + randomNum;
    }
}
