# Laboratorul 4 – Programare Funcțională și Stream API

Acest document prezintă refactorizarea proiectului din Laboratorul 3 prin adoptarea paradigmei funcționale în Java (Stream API, interfețe funcționale, expresii lambda, referințe la metode și `Optional<E>`).

---

## 1. Tabel Înainte / După pentru Buclele Înlocuite

| Clasa | Metoda | Înainte (Laboratorul 3) | După (Laboratorul 4) – Pipeline Stream API & Operații |
| :--- | :--- | :--- | :--- |
| `FriendshipService` | `removeAllFriendshipsOfUser` | ```java<br>for (Friendship f : friendshipRepository.findAll())<br>    if (f.getFirstUserId().equals(...) \|\| ...)<br>        toDelete.add(f.getId());<br>for (Tuple<Long, Long> key : toDelete)<br>    friendshipRepository.delete(key);<br>``` | ```java<br>friendshipRepository.findAll().stream()<br>    .filter(f -> f.getFirstUserId().equals(...) \|\| ...)<br>    .map(Friendship::getId)<br>    .collect(Collectors.toList());<br>toDelete.forEach(friendshipRepository::delete);<br>```<br>**Intermediare:** `filter`, `map`<br>**Terminale:** `collect`, `forEach` |
| `CommunityService` | `getFriendsOf` | ```java<br>for (Friendship f : friendshipRepository.findAll())<br>    if (f.getFirstUserId().equals(userId))<br>        friends.add(f.getSecondUserId());<br>    ...<br>``` | ```java<br>friendshipRepository.findAll().stream()<br>    .flatMap(f -> { ... })<br>    .collect(Collectors.toList());<br>```<br>**Intermediare:** `flatMap`<br>**Terminale:** `collect` |
| `CommunityService` | `getMostSociableCommunity` | ```java<br>for (List<Long> community : communities)<br>    for (Long memberId : community) { ... }<br>    if (longestInCommunity > maxLength) { ... }<br>for (Long id : bestCommunity) { ... }<br>``` | ```java<br>communities.stream()<br>    .max(Comparator.comparingInt(this::calculateMaxPathInCommunity)<br>        .thenComparing(List::size))<br>    .map(community -> community.stream()<br>        .map(userRepository::findOne)<br>        .filter(Optional::isPresent).map(Optional::get)<br>        .collect(Collectors.toList()))<br>    .orElseGet(Collections::emptyList);<br>```<br>**Intermediare:** `map`, `filter`<br>**Terminale:** `max`, `collect`, `orElseGet` |
| `CommunityService` | `getCommunities` | ```java<br>for (User user : userRepository.findAll())<br>    if (!visited.contains(user.getId()))<br>        collectCommunity(...);<br>``` | ```java<br>userRepository.findAll().stream()<br>    .map(User::getId)<br>    .filter(visited::add)<br>    .map(id -> { ... collectCommunity(id, visited, c); return c; })<br>    .collect(Collectors.toList());<br>```<br>**Intermediare:** `map`, `filter`<br>**Terminale:** `collect` |
| `InMemoryRepository` | `generateNextId` | ```java<br>for (ID id : entities.keySet())<br>    if (id instanceof Long) {<br>        if (current > maxId) maxId = current;<br>    }<br>``` | ```java<br>entities.keySet().stream()<br>    .filter(Long.class::isInstance)<br>    .mapToLong(id -> (Long) id)<br>    .max()<br>    .orElse(1L);<br>```<br>**Intermediare:** `filter`, `mapToLong`<br>**Terminale:** `max`, `orElse` |
| `FileRepository` | `loadData` | ```java<br>while ((line = reader.readLine()) != null)<br>    String[] tokens = line.split(";");<br>    ...<br>``` | ```java<br>try (Stream<String> linesStream = Files.lines(filePath)) {<br>    List<String> lines = linesStream.collect(Collectors.toList());<br>    IntStream.range(0, lines.size())<br>        .filter(i -> !lines.get(i).trim().isEmpty())<br>        .forEach(i -> { ... });<br>}<br>```<br>**Intermediare:** `filter`<br>**Terminale:** `collect`, `forEach` |
| `FileRepository` | `writeToFile` | ```java<br>for (E entity : allEntities)<br>    writer.write(createEntityAsString(entity));<br>    writer.newLine();<br>``` | ```java<br>List<String> lines = super.findAll().stream()<br>    .map(entityToLine)<br>    .collect(Collectors.toList());<br>Files.write(filePath, lines, ...);<br>```<br>**Intermediare:** `map`<br>**Terminale:** `collect` |
| `ConsoleUI` | `showAllUsers` | ```java<br>for (User u : users)<br>    System.out.println("ID: " + u.getId() + ...);<br>``` | ```java<br>users.stream()<br>    .map(u -> "ID: " + u.getId() + " \| Name: " + ...)<br>    .forEach(System.out::println);<br>```<br>**Intermediare:** `map`<br>**Terminale:** `forEach` |

---

## 2. Interfețe Funcționale Utilizate și Locul Utilizării Lor

* **`Validator<T>` (`@FunctionalInterface`)**:
  * Definită ca interfață funcțională custom. Permite validarea regulilor de business și compunerea dinamică prin metoda `default` `andThen`.
  * **Locație:** `domain.validator.Validator`, `Main.java` (compunere cu regula de lungime minimă ca expresie lambda).
* **`Predicate<T>`**:
  * Folosită pentru filtrarea dinamică a colecțiilor fără metode hardcodate în Repository.
  * **Locație:**
    * `repository.Repository.findAll(Predicate<E> filter)`
    * `domain.UserCriteria` (metode statice ce întorc `Predicate<User>`)
    * `domain.FriendshipCriteria` (metode statice ce întorc `Predicate<Friendship>`)
    * `service.UserService.filterUsers(Predicate<User> filter)`
* **`Function<T, R>`**:
  * În `repository.RepositoryFactory`: map-urile `userRepoProviders` și `friendshipRepoProviders` stochează `Function<Validator<E>, Repository<ID, E>>`, asigurând dispatching funcțional *lazy* fără instrucțiuni `switch`.
  * În `repository.FileRepository`: constructorul primește `Function<String, E>` (deserializare linie $\rightarrow$ entitate) și `Function<E, String>` (serializare entitate $\rightarrow$ linie).
* **`Consumer<T>`**:
  * Utilizat în operațiile terminale `.forEach()` pentru efecte finale fără mutații colaterale ascunse:
    * `toDelete.forEach(friendshipRepository::delete)` în `FriendshipService`.
    * `.forEach(System.out::println)` în `ConsoleUI`.
* **`Comparator<T>`**:
  * Construit fluent prin fabrici funcționale (`Comparator.comparingInt(...).thenComparing(...)`) în `CommunityService.getMostSociableCommunity()`.

---

## 3. Cele Patru Tipuri de Referințe la Metode

1. **Metodă statică (`ClassName::staticMethod`):**
   * `Long.class::isInstance` (în `InMemoryRepository.generateNextId`).
2. **Metodă de instanță a unui obiect oarecare / arbitrar (`ClassName::instanceMethod`):**
   * `Friendship::getId` (în `FriendshipService.removeAllFriendshipsOfUser`).
   * `User::getId` (în `CommunityService.getCommunities`).
   * `List::size` (în comparatorul din `CommunityService.getMostSociableCommunity`).
   * `Optional::isPresent` și `Optional::get` (în `CommunityService.getMostSociableCommunity`).
3. **Metodă de instanță a unui obiect anume / existent (`instance::method`):**
   * `System.out::println` (în `ConsoleUI.showAllUsers`, `showAllFriendships`, `uiShowMostSociableCommunity`).
   * `friendshipRepository::delete` (în `FriendshipService.removeAllFriendshipsOfUser`).
   * `this::calculateMaxPathInCommunity` (în `CommunityService.getMostSociableCommunity`).
   * `this::extractUser`, `this::createUserLine`, `this::extractFriendship`, `this::createFriendshipLine` (în `RepositoryFactory.initProviders`).
4. **Constructor (`ClassName::new`):**
   * `InMemoryRepository::new` (în `RepositoryFactory.initProviders`, ca referință la constructor pentru tipul `MEMORY`).
   * `ArrayList::new` (în `CommunityService`).

---

## 4. Decizii Tehnice

### Compoziție (`FileRepository<ID, E>`) vs. Moștenire (`AbstractFileRepository`)
S-a adoptat varianta **compoziției**: crearea unei singure clase generice `FileRepository<ID, E>` care primește prin constructor două funcții de transformare:
```java
Function<String, E> lineToEntity
Function<E, String> entityToLine
```

**Justificare:**
1. *Composition over inheritance*: Se elimină clasele derivate scheletice (`UserFileRepository`, `FriendshipFileRepository`) care existau exclusiv pentru a suprascrie două metode.
2. *Responsabilitate unică*: Logica de acces la disc, citire cu `Files.lines` și scriere atomică cu `Files.write` se află într-un singur loc generic, reutilizabil pentru orice entitate viitoare.
3. *Decuplare*: Logica de parsare/serializare este decuplată de mecanismul de I/O, fiind transmisă curat prin expresii lambda sau referințe la metode din `RepositoryFactory`.

### Utilizare `Optional<E>` în Repository
S-a decis trecerea metodelor `findOne(ID id)` și `delete(ID id)` din interfața `Repository` la tipul de retur `Optional<E>`, consecvent în toate implementările (`InMemoryRepository`, `FileRepository`, `UserDbRepository`, `FriendshipDbRepository`).

**Justificare:**
1. *Type Safety și eliminarea erorilor*: Forțează apelantul la nivel de compilator să ia în considerare posibilitatea lipsei entității, prevenind apariția excepțiilor `NullPointerException`.
2. *Aliniere cu standardele moderne Java și arhitectura din curs*: Oferă un design similar cu Spring Data `CrudRepository`.
3. *Expresivitate în Service*: Permite utilizarea directă și elegantă a metodelor `orElseThrow` (pentru entități inexistente la ștergere), `isPresent`/`isEmpty` (la validarea existenței utilizatorilor înainte de legarea prieteniei) și combinarea în pipeline-uri de stream.

---

## 5. Bucle Păstrate și Justificare

* Singura buclă clasică păstrată în tot proiectul este bucla `while (true)` din metoda `ConsoleUI.run()`.
  * **Justificare:** Această buclă controlează ciclul interactiv de viață al aplicației consolă (*REPL – Read, Eval, Print Loop*), așteptând comenzi la `System.in` până când utilizatorul introduce tasta `"0"` (*Exit*). Această iterație nu reprezintă o parcurgere de colecție sau procesare de date, ci fluxul principal al interfeței cu utilizatorul.
* Toate celelalte bucle (`for`, `while`, iterații manuale) din pachetele `domain`, `service`, `repository` și metodele de afișare din `ui` au fost eliminate complet și înlocuite cu operații native Stream API.
