package metier;

public class CodeGenerator {

    // 🔹 Code unique pour le CLIENT
    public static String generateClientCode() {
        return "CL-" + System.currentTimeMillis();
    }

    // 🔹 (optionnel pour plus tard) Code réparation
    public static String generateReparationCode() {
        return "REP-" + System.currentTimeMillis();
    }
}
