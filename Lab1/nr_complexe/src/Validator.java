public class Validator {
    public void valideaza(String[] args) {
        if (args == null || args.length < 3 || args.length % 2 == 0)
            throw new IllegalArgumentException("Expresie invalida!");
        String primOp = args[1];
        if (!"+-*/".contains(primOp) || primOp.length() != 1) {
            throw new IllegalArgumentException("Expresie invalida!");
        }
        for (int i = 3; i < args.length; i += 2) {
            if (!args[i].equals(primOp)) {
                throw new IllegalArgumentException("Expresie invalida!");
            }
        }
    }
}
