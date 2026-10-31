# Funcționarea Factory-ului și Tranziția la Baza de Date

* Clasa `RepositoryFactory` (Simple Factory) citește fișierul de configurare `"src/main/resources/config.properties"` la instanțiere.
* În funcție de cheia `"repository.type"` (`MEMORY`, `FILE`, `DATABASE`), metodele `createUserRepository()` și `createFriendshipRepository()` instanțiază și returnează implementările corespunzătoare (ca tip abstract `Repository<ID, E>`).
* Service-ul, UI-ul și Main depind exclusiv de interfața `Repository`.

### Trecerea la persistența pe baza de date la Laboratorul 4:
1. Se implementează operațiile JDBC în metodele claselor schelet deja existente (`UserDbRepository` și `FriendshipDbRepository`).
2. În `config.properties` se modifică o singură linie: `repository.type=DATABASE`.
3. Clasele Service, UI și Main rămân nemodificate.

---

# Tratarea Excepțiilor: Fișier Lipsă și Linii Corupte

* **Fișier lipsă la pornire:**  
  În `AbstractFileRepository.loadData()`, dacă fișierul nu există pe disc, aplicația pornește normal cu un repository gol; fișierul se creează automat pe disc la primul apel de `save()`.

* **Linie coruptă sau invalidă:**  
  În bucla de citire din `loadData()`, fiecare linie este parsată într-un bloc `try-catch` intern.  
  Linia coruptă este raportată în consolă (număr linie + motiv) și ignorată, continuându-se încărcarea restului de date valide.  
  Aplicația rămâne robustă și utilizabilă chiar dacă fișierul a suferit coruperi accidentale parțiale, fără a bloca pornirea întregului sistem.

---

# Simple Factory vs. Abstract Factory din Perspectiva OCP

* **Simple Factory (implementat în proiect):**  
  Folosește o singură clasă cu o instrucțiune `switch` clasică.
  * **Avantaj:** simplitate, ușor de înțeles, număr redus de clase în proiect.
  * **Dezavantaj:** încalcă OCP (*Open-Closed Principle*), deoarece adăugarea unui nou mod de persistență forțează modificarea codului sursă al fabricii existente prin adăugarea unui nou `case`.

* **Abstract Factory:**  
  Definește o interfață `RepositoryFactory` cu fabrici concrete derivate (`MemoryRepositoryFactory`, `FileRepositoryFactory`, `DbRepositoryFactory`).
  * **Avantaj:** respectă strict OCP, extinderea realizându-se doar prin clase noi, fără modificarea codului existent.
  * **Dezavantaj:** crește complexitatea arhitecturală și numărul de fișiere/clase.

---

# Localizarea Validării (Repository vs. Service)

* În proiect, validarea entităților este plasată în **REPOSITORY** (în metoda `save`, prin folosirea unui `Validator`).

### Argumentare:
1. **Integritatea persistenței:** repository-ul acționează ca o barieră strictă; nicio entitate invalidă nu poate ajunge pe disc sau în memorie, indiferent de unde sau de cine este apelat repository-ul.
2. **Rolul Service-ului:** În Service rămân regulile complexe de business și de coordonare a stării (ex: verificarea existenței ambilor utilizatori înainte de crearea unei prietenii, ștergerea în cascadă a prieteniilor la eliminarea unui user).

---

# Principii SOLID și Design Patterns în Proiect

## Principii SOLID

* **SRP (Single Responsibility Principle):**
  * `AbstractFileRepository` gestionează exclusiv fluxurile I/O și persistența.
  * Subclasele `UserFileRepository` și `FriendshipFileRepository` răspund doar de parsarea/formatarea string-entitate.
  * Serviciile sunt decuplate pe domenii (`UserService`, `FriendshipService`, `CommunityService`).

* **OCP (Open-Closed Principle):**
  * `AbstractFileRepository` este închis modificării fluxului de citire/scriere, dar deschis extinderii prin moștenire (metodele abstracte `extractEntity` și `createEntityAsString`).

* **LSP (Liskov Substitution Principle):**
  * `InMemoryRepository`, `UserFileRepository` și scheletele `UserDbRepository` pot substitui interfața `Repository<Long, User>` în servicii fără a altera corectitudinea programului.

* **DIP (Dependency Inversion Principle):**
  * Nivelurile înalte (Service, UI) depind exclusiv de abstracțiunea `Repository<ID, E>`, nu de clasele concrete; dependențele sunt injectate prin constructori.

## Design Patterns

* **Factory (Simple Factory):** `RepositoryFactory` centralizează crearea instanțelor.
* **Template Method:** `AbstractFileRepository` definește scheletul algoritmului de încărcare/salvare (`loadData`, `writeToFile`), delegând pașii specifici către `extractEntity()` și `createEntityAsString()` din subclase.
* **Strategy:** interfața `Validator<T>` permite schimbarea strategiilor de validare pentru fiecare tip de entitate în parte.
* **Decorator:** folosit la fluxurile de I/O standard Java (`BufferedReader` peste `FileReader`; `BufferedWriter` peste `FileWriter`).
