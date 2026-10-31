public class Impartire implements OperatieComplexa{
    @Override
    public NumarComplex executa(NumarComplex a, NumarComplex b) {
        double numitor = b.get_re() * b.get_re() + b.get_im() * b.get_im();
        double real = (a.get_re() * b.get_re() + a.get_im() * b.get_im()) / numitor;
        double imag = (a.get_im() * b.get_re() - a.get_re() * b.get_im()) / numitor;
        return new NumarComplex(real, imag);
    }
}
