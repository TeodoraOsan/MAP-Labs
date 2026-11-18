# Laborator 5 – Rețea de Socializare (Persistență & Baze de Date)

Aplicație de tip consolă dezvoltată în Java (Gradle) pentru gestionarea unei rețele de socializare (utilizatori, relații de prietenie și comunități), extinsă cu suport pentru persistență în memorie, fișiere text și bază de date relațională (PostgreSQL).

---

## Arhitectură și Funcționalități

Proiectul urmează o arhitectură stratificată (**Layered Architecture**):
* **`domain`**: Entități (`User`, `Friendship`, `Tuple`), criterii de filtrare, validatori (`UserValidator`, `FriendshipValidator`) și excepții personalizate.
* **`repository`**: Abstracții și implementări pentru stocarea datelor (`InMemoryRepository`, `FileRepository`, `UserDbRepository`, `FriendshipDbRepository`), instanțiate dinamic prin `RepositoryFactory`.
* **`service`**: Logica de business (`UserService`, `FriendshipService`, `CommunityService` – determinarea componentelor conexe și a celei mai sociabile comunități).
* **`ui`**: Interfața utilizator în linie de comandă (`ConsoleUI`).

---

## Justificare Cerința B.1 – Ștergerea Prieteniilor

Pentru gestionarea prieteniilor în momentul ștergerii unui utilizator, am ales abordarea la nivelul bazei de date folosind constrângerea **`ON DELETE CASCADE`** pe cheile externe (`id1` și `id2`) din tabelul `friendships`.

### Argumente tehnice:

1. **Integritate și Siguranță (ACID):** Baza de date garantează integritatea referențială în mod automat și atomic. Dacă ștergerea s-ar face manual din `FriendshipService` (Java), ar exista riscul ca aplicația să se oprească fix după ștergerea utilizatorului, dar înainte de curățarea prieteniilor, lăsând în urmă *orphan records* (înregistrări orfane invalide).
2. **Performanță:** Delegând această sarcină către PostgreSQL, se reduce numărul de interogări (*query*-uri) transmise prin rețea între aplicația Java și serverul de baze de date. O singură comandă `DELETE` pe tabelul `users` este suficientă.
3. **Cod mai curat (Separation of Concerns):** Repository-ul și Service-ul rămân mai simple, mai lizibile și mai ușor de întreținut, delegând gestionarea integrității relaționale către SGBD.

---

## Detalii de Configurare – Bază de Date & Persistență

* **Generare ID-uri:** Tabelele din PostgreSQL folosesc coloane de tip `GENERATED ALWAYS AS IDENTITY` (sau `SERIAL`/`IDENTITY`) pentru generarea automată și sigură a identificatorilor unici.
* **Comutare moduri de persistență:** Trecerea între cele 3 moduri suportate (`MEMORY`, `FILE`, `DATABASE`) se face exclusiv prin modificarea proprietăților din fișierul `src/main/resources/config.properties` (fără recompilarea codului).

### Structură SQL orientativă (PostgreSQL)

```sql
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL
);

CREATE TABLE friendships (
    id1 BIGINT NOT NULL,
    id2 BIGINT NOT NULL,
    friends_from TIMESTAMP NOT NULL,
    PRIMARY KEY (id1, id2),
    CONSTRAINT fk_user1 FOREIGN KEY (id1) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user2 FOREIGN KEY (id2) REFERENCES users(id) ON DELETE CASCADE
);
