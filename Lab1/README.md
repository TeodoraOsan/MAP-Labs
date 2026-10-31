# Arhitectura aplicației

Arhitectura aplicatiei se bazeaza pe sablonul de proiectare **Strategy Pattern**. Acesta este vizibil in relatia dintre interfata `OperatieComplexa` si clasele care o implementeaza (`Adunare`, `Scadere`, `Inmultire`, `Impartire`). Fiecare operatie este apelata prin metoda `executa(a, b)` si astfel evaluatorul nu foloseste structuri decizionale rigide.

## Principii SOLID

Principiile SOLID sunt respectate astfel:

- **Single Responsibility Principle:** Fiecare clasa are un rol unic (`NumarComplex`: stocheaza datele, `LinieComandaParser`: imparte expresia, `Expression`: actioneaza ca model de date, `ExpressionEvaluation`: calculeaza expresia, clasele de operatii contin strict formula matematica aferenta fiecarei operatii, iar `Main` doar porneste executia).
- **Open/Closed Principle:** Putem adauga oricand operatii sau surse de citire noi (prin implementarea interfetelor), fara a modifica clasele deja scrise.
- **Liskov Substitution Principle:** Oricare dintre clasele derivate (`Adunare`, `Scadere`, `Inmultire`, `Impartire`) poate inlocui interfata `OperatieComplexa` fara a altera rezultatul logic.
- **Interface Segregation Principle:** Interfatele (`OperatieParser` si `OperatieComplexa`) contin strict metodele esentiale pentru scopul lor.
- **Dependency Inversion Principle:** Este reflectat prin dependenta `Main ..> OperatieParser`. Clasa `Main` nu depinde de executantul direct (`LinieComandaParser`), ci de o abstractiune comuna (`OperatieParser`).
 
