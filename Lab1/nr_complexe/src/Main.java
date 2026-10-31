public class Main {
    public static void main(String[] args) {
        try {
            Validator validator = new Validator();
            validator.valideaza(args);
            LinieComandaParser parser = new LinieComandaParser();
            ExpressionEvaluation evaluator = new ExpressionEvaluation();
            System.out.println(evaluator.evalueaza(parser.parseaza(args)));
        }
        catch (IllegalArgumentException e){
            System.out.println("Eroare de validare: " + e.getMessage());
        }
    }
}