public class ExpressionEvaluation {
    public NumarComplex evalueaza(Expression expresie) {
        if (expresie == null) {
            throw new IllegalArgumentException("Expresia nu poate fi null!");
        }
        NumarComplex[] operanzi = expresie.get_operanzi();
        OperatieComplexa operatie = expresie.get_operatie();
        if (operanzi == null || operanzi.length == 0) {
            throw new IllegalArgumentException("Expresia nu contine operanzi!");
        }
        NumarComplex rezultat = operanzi[0];
        for (int i = 1; i < operanzi.length; i++) {
            rezultat = operatie.executa(rezultat, operanzi[i]);
        }
        return rezultat;
    }
}
