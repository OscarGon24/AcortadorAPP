package com.example.login4;

import java.util.Calendar;

public class ValidarTarjeta {
        public static boolean ValidarNumero(String numeroTarjeta){
            // Verificar que el número de tarjeta no esté vacío
            if (numeroTarjeta == null || numeroTarjeta.isEmpty()){
                return false;
            }
            // Limpiar el número de tarjeta
            String limpiarNumero = numeroTarjeta.replaceAll("[^0-9]", "");
            // Verificar que solo contenga dígitos y tenga longitud válida
            if (!limpiarNumero.matches("\\d+") || limpiarNumero.length() < 13 || limpiarNumero.length() > 19){
                return false;
            }
            // Aplicar algoritmo de Luhn
            int suma = 0;
            boolean alternate = false;

            for (int i = limpiarNumero.length() - 1; i >= 0; i--) {
                int digit = Integer.parseInt(limpiarNumero.substring(i, i + 1));
                if (alternate) {
                    digit *= 2;
                    if (digit > 9) {
                        digit = (digit % 10) + 1;
                    }
                }
                suma += digit;
                alternate = !alternate;
            }
            return (suma % 10 == 0);
        }

    public static String getTipoTarjeta(String numeroTarjeta) {

        String limpiarTarjeta = numeroTarjeta.replaceAll("[^0-9]", "");

        if (limpiarTarjeta.matches("^4[0-9]{12}(?:[0-9]{3})?$")) {
            return "Visa";
        } else if (limpiarTarjeta.matches("^5[1-5][0-9]{14}$")) {
            return "MasterCard";
        } else if (limpiarTarjeta.matches("^3[47][0-9]{13}$")) {
            return "American Express";
        } else if (limpiarTarjeta.matches("^6(?:011|5[0-9]{2})[0-9]{12}$")) {
            return "Discover";
        } else {
            return "Desconocido";
        }
    }

    // Valida la fecha de expiración (MM/YY)
    public static boolean ValidarFechaExpiracion(String fechaExpiracion) {
            if (fechaExpiracion == null || !fechaExpiracion.matches("^(0[1-9]|1[0-2])/?([0-9]{2})$")) {
                return false;
            }

            String[] parts = fechaExpiracion.split("/");
            int mes = Integer.parseInt(parts[0]);
            int anio = Integer.parseInt(parts[1]);

            // Obtener año y mes actual
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR) % 100;
            int currentMonth = calendar.get(Calendar.MONTH) + 1;

            if (anio < currentYear || (anio == currentYear && mes < currentMonth)) {
                return false;
            }
            return true;
    }

    // Valida el CVV
    public static boolean ValidarCVV(String numeroTarjeta, String cvv) {
        String cardType = getTipoTarjeta(numeroTarjeta);
        int requiredLength = (cardType.equals("American Express")) ? 4 : 3;
        return cvv.matches("^\\d{" + requiredLength + "}$");
    }
}