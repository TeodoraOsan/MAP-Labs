public class LinieComandaParser implements OperatieParser{
    @Override
    public Expression parseaza(String[] args) {
        OperatieComplexa operatie = alege_op(args[1]);
        int nrOperanzi = (args.length + 1) / 2;
        NumarComplex[] operanzi = new NumarComplex[nrOperanzi];
        int index = 0;
        for (int i = 0; i < args.length; i += 2) {
            operanzi[index++] = parseaza_nr(args[i]);
        }
        return new Expression(operatie, operanzi);
    }

    private NumarComplex parseaza_nr(String text) {
        if (text == null)
            throw new IllegalArgumentException("Numarul nu poate fi null!");
        text = text.replace(" ", "");
        if (!text.contains("i")) {
            double re = Double.parseDouble(text);
            return new NumarComplex(re, 0.0);
        }
        int semnIdx = -1;
        for(int i = text.length() - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '+' || c == '-') {
                semnIdx = i;
                break;
            }
        }
        if (semnIdx == -1) {
            String parteImag = text.replace("*i", "").replace("i", "");
            double im;
            if (parteImag.equals("+")) {
                im = 1.0;
            } else if (parteImag.equals("-")) {
                im = -1.0;
            } else {
                im = Double.parseDouble(parteImag);
            }
            return new NumarComplex(0.0, im);
        }
        String parteReala = text.substring(0, semnIdx);
        String parteImag = text.substring(semnIdx);
        double re = Double.parseDouble(parteReala);
        parteImag = parteImag.replace("*i", "").replace("i", "");
        double im;
        if (parteImag.equals("+")) {
            im = 1.0;
        } else if (parteImag.equals("-")) {
            im = -1.0;
        } else {
            im = Double.parseDouble(parteImag);
        }
        return new NumarComplex(re, im);
    }

    private OperatieComplexa alege_op(String op) {
        switch (op) {
            case "+":
                return new Adunare();
            case "-":
                return new Scadere();
            case "*":
                return new Inmultire();
            case "/":
                return new Impartire();
            default:
                throw new IllegalArgumentException("Operator necunoscut!");
        }
    }
}
