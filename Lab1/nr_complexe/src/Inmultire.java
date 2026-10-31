public class Inmultire implements OperatieComplexa{
    @Override
    public NumarComplex executa(NumarComplex a, NumarComplex b) {
        double real = a.get_re() * b.get_re() - a.get_im() * b.get_im();
        double imag = a.get_re() * b.get_im() + a.get_im() * b.get_re();
        return new NumarComplex(real, imag);
    }
}
