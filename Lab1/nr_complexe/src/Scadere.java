public class Scadere implements OperatieComplexa{
    @Override
    public NumarComplex executa(NumarComplex a, NumarComplex b) {
        double real = a.get_re() - b.get_re();
        double imag = a.get_im() - b.get_im();
        return new NumarComplex(real, imag);
    }
}
