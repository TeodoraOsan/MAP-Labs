public class NumarComplex {
    private double re;
    private double im;

    public NumarComplex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double get_re() {
        return this.re;
    }

    public double get_im() {
        return this.im;
    }

    @Override
    public String toString() {
        if (im == 0)
            return "" + this.re;
        if (re == 0)
            return this.im + "*i";
        if (im > 0)
            return this.re + "+" + this.im + "*i";
        else
            return this.re + "-" + (-this.im) + "*i";
    }
}
