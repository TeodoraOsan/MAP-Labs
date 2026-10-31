public class Expression {
    private OperatieComplexa operatie;
    private NumarComplex[] operanzi;

    public Expression(OperatieComplexa operatie, NumarComplex[] operanzi) {
        this.operatie = operatie;
        this.operanzi = operanzi;
    }

    public OperatieComplexa get_operatie() {
        return this.operatie;
    }

    public NumarComplex[] get_operanzi() {
        return this.operanzi;
    }
}
