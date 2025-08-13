# loan-frontend-adapter @🍎🌳

## Das Wichtigste in Kürze
1. Das im [pom.xml](pom.xml) konfigurierte `openapi-generator-maven-plugin` generiert zur Build Time aus der [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml) das `api-interface`, das entsprechende `model` und das `swagger-ui`
2. Der [LoanRestController](src/main/java/com/appletree/lfa/api/LoanRestController.java) implementiert das generierte `api-interface`
3. Damit der [LoanRestController](src/main/java/com/appletree/lfa/api/LoanRestController.java) Abfragen mit Daten beantworten kann, werden sämtliche [Kernbankensystem-Json-Dateien](src/main/resources/data) _unverändert_ mit dem [ResourceDataLoader](src/main/java/com/appletree/lfa/data/access/ResourceDataLoader.java) in die Applikation geladen und als Grundlage für die [Repository-Klassen](src/main/java/com/appletree/lfa/data/access/repo) verwendet
4. Im [LoanRestControllerTest](src/test/java/com/appletree/lfa/api/LoanRestControllerTest.java) werden mit dem _gleichen_ [ResourceDataLoader](src/main/java/com/appletree/lfa/data/access/ResourceDataLoader.java) massgeschneiderte [Daten spezifisch für die Tests](src/test/resources/data) eingelesen, wodurch der Arrange-Teil in den Tests entfällt bzw. der Hauptteil von der `@BeforeAll`-Methode übernommen wird
5. Die `package`-Struktur sieht wie folgt aus:
    - `com.appletree.lfa`: `lfa` steht für `loan-frontend-adapter`
        - [api](src/main/java/com/appletree/lfa/api): API-Schicht (REST Controller, generierte API-Klassen, etc.)
        - [business](src/main/java/com/appletree/lfa/business): Logikschicht (hauptsächlich die Konvertierung der `Loan`-Objekte)
        - [data](src/main/java/com/appletree/lfa/data): Persistenzschicht
            - [access](src/main/java/com/appletree/lfa/data/access): Enthält alles zum Datenzugriff
            - [model](src/main/java/com/appletree/lfa/data/model): Enthält die nackten Datenmodelle
        - [util](src/main/java/com/appletree/lfa/util): Statische, nicht instanziierbare Klassen zur allgemeinen Verwendung
6. Das Projekt enthält alles, um erfolgreich Anfragen zu Kreditdaten von Kunden zu beantworten
    - Vorgehen
        - `git clone` https://github.com/muellR/loan-frontend-adapter.git
        - `mvn clean install`
        - [LoanFrontendAdapterApplication](src/main/java/com/appletree/lfa/LoanFrontendAdapterApplication.java) starten
        - Alle gültigen `userId`s anfragen mit [http://localhost:8080/service/v1/userIds](http://localhost:8080/service/v1/userIds) oder via [swagger-ui](http://localhost:8080/swagger-ui/index.html)
        - `Loan`-Daten für eine spezifische `userId` anfragen mit [http://localhost:8080/service/v1/loansByUser/{userId}](http://localhost:8080/service/v1/loansByUser/11110001) oder via [swagger-ui](http://localhost:8080/swagger-ui/index.html)

## Bemerkungen
### Mappings
- Es war eine Herausforderung, mit den gegebenen Informationen die Kernbankensystem-Objekte korrekt in die `Loan`-Objekte zu übersetzen. Mit Yves (Business Analyst) müssen die Akzeptanzkriterien Feld für Feld besprochen und Testfälle definiert werden.
    - Woher kommen die `id`s für die `Loan`-Objekte? `UUID`s zu generieren im [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) ist nicht zulässig, sonst würden sich die `id`s im Frontend immer wieder ändern. Der [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) vergibt _stabile_ und _eindeutige_ `id`s. Der Parent-`Loan` hat die gleiche `id`, wie das zugrunde liegende `FinancingObject`. Die `id`s der Child-`Loan`s setzen sich zusammen aus der `id` des `FinancingObject`s und der `id` des `Product`s. Das ergibt Sinn, weil das `FinancingObject` die Existenzberechtigung für ein Parent-`Loan` und ein `Product` die Grundlage für ein Child-Loan ist.
    - `paymentFrequency` und `interestPaymentFrequency` werden gemäss dem [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml)-Beispiel als `"Monthly"` oder `"Quarterly"` ans Frontend übermittelt. Es müssen aber noch andere `Frequency`s gemappt werden (siehe [FrequencyUtil](src/main/java/com/appletree/lfa/util/FrequencyUtil.java)). Sollte jetzt zum Beispiel die Ausprägung `1` als `"Annually"` oder als `"Yearly"` gemapped werden?
    - Gehört die `contractNumber` nur auf den Parent-`Loan` oder genauso auf die Child-`Loan`s?
    - Wie wird der `currencyCode` in einem Parent-`Loan` abgebildet, wenn in den zugehörigen Child-`Loan`s verschiedene Währungen vorkommen? Und wie werden dann die Beträge im Parent-`Loan` aufsummiert?
    - ...

### User Stories
- Die User Stories sind nicht alle restlos klar.
    - Die User Story **S6: Identify when the next interest payment is due** verlangt, dass im Feld `interestDue` der Child-`Loan`s ein Datum gesetzt wird. Die [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml) beschreibt das Feld `interestDue` wie folgt:
      ``` yaml
      interestDue:
        type: string
        description: Interest amount which is due
      ```
      Es handelt sich also um ein Betragsfeld und nicht um ein Datumsfeld. Auch das Beispiel in der [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml) zeigt, dass es sich um ein Betragsfeld handelt (`interestDue: "88.30000"`, sprich ein Gleitkommazahl-`String` mit 5 Nachkommastellen). Wegen dieser Diskrepanz wurde die User Story nicht umgesetzt. Zuerst muss mit Yves (Business Analyst) geklärt werden, was hier zu tun ist.
    - Die User Story **S5: Calculate next amortisation payment amount for lending** geht nur solange gut, wie pro `Limit` _nur eine_ `LimitRealSecurity` konfiguriert ist. Sind _mehr_ `LimitRealSecurity`s konfiguriert, werden im [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) _mehrere_ `LoanCollateralInner`s mit dem _gleichen_ `amortisationPaymentAmount` produziert. Ist das erwünscht oder kann der Fall kategorisch ausgeschlossen werden? In den ausgehändigten [Kernbankensystem-Json-Dateien](src/main/resources/data) kommt der Fall zumindest nicht vor.
    - Die User Story **S3: Find lending date range based on product date ranges** spezifiziert beispielsweise ein Resultat von 01.11.2020. Gemäss der [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml) soll allerdings ein `String` mit dem `UTC`-Date-Time-Format geliefert werden (inkl. Millisekunden). Das wäre für den 01.11.2020 beispielsweise `"2020-10-31T23:00:00.000Z"`, da in der Schweiz `CET` (Central European Time) und in den Wintermonaten 1 Stunde Zeitverschiebung gegenüber `UTC` (Coordinated Universal Time) gilt. Entsprechend ist der `ObjectMapper` für die zusätzliche Anzeige der Millisekunden konfiguriert (siehe [LoanFrontendAdapterConfig](src/main/java/com/appletree/lfa/LoanFrontendAdapterConfig.java)). Die User Story sollte das Datumsformat analog dem [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml)-Beispiel als `UTC`-`String` spezifizieren. Mit Yves (Business Analyst) sollte geklärt werden, dass sich die Akzeptanzkriterien (und entsprechend auch die Tests) künftig bestmöglich auf die Zieldomäne beziehen sollten.
    - ...

### Implementierung
- [http://localhost:8080/service/v1/loansByUser/{userId}](http://localhost:8080/service/v1/loansByUser/11110001) gibt alle zu einer `userId` gehörenden `Loan`s zurück. Dabei werden im [UserService](src/main/java/com/appletree/lfa/business/UserService.java) mit _nur einem_ Repository-Aufruf `FinancingObject`-übergreifend alle zugehörigen `Limit`s und in _nur einer_ weiteren Anfrage alle referenzierten `Product`s aus der Datenbank geladen. So wird die Anzahl Datenbank-Abfragen _minimiert_. Wäre eine echte Datenbank angehängt, könnte man sich überlegen, die Objekte über einen `JOIN` bereits auf der Datenbank zusammenzufassen und in einer einzigen Abfrage zu laden.
- Die Applikation differenziert nicht, ob die angefragte `userId` nicht existiert oder ob der entsprechende User _keine_ `FinancingObject`s besitzt – in beiden Fällen wird eine leere `Loan`-Liste zurückgegeben.
- Die [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml) sieht einen `httpStatus=401` vor. Zu diesem `httpStatus` wird es in der Applikation nie kommen, da keine Security konfiguriert ist. Der `@RestController` ist noch durch angemessene Security vor unbefugten Zugriffen zu schützen.
- An verschiedenen Stellen im Code wird die angefragte `userId` in die `log`s geschrieben. Ob das zulässig ist, muss geklärt werden (Datenschutz).
- Der [UserService](src/main/java/com/appletree/lfa/business/UserService.java) und der [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) behandeln die `userId` als `Long`. Der [LoanRestController](src/main/java/com/appletree/lfa/api/LoanRestController.java) hingegen muss gemäss der [openapi-spec](src/main/resources/spec/20231210_OutboundIntegrationAPI_LoansService.yaml) die `userId` als `String` behandeln. Schade – es wäre vorteilhaft gewesen, die `userId` als `Long` entgegenzunehmen (dann wäre das `Long.parseLong(...)` hinfällig).
- Die `convertLoans`-Methode im [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) arbeitet mit den Typen `Map<Long, Limit> ...` und `Map<Long, Product> ...` als Parameter. Die `id` ist dabei jeweils im `Key` und im `Value` der `Map` redundant enthalten. Dafür ist die `Map` die richtige Datenstruktur für die random access lookups, die wenige Code-Zeilen später folgen.
- Der [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) kann auch `FinancingObject`s konvertieren, die keine `Product`s referenzieren (es resultiert ein Parent-`Loan` ohne Child-`Loan`s). Genauso kann der [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) `Limit`s konvertieren, die _keine_ oder _mehrere_ `LimitRealSecurity`s referenzieren (es resultieren so viele `LoanCollateralInner`s, wie es `LimitRealSecurity`s gibt). Für die Konvertierung von `FinancingObject`s ohne `Limit`-Referenz ist der [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) nicht ausgelegt (es werden aktuell allgemein wenig `null`-Prüfungen gemacht).
- Der [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) ist fähig, in einem Aufruf über mehrere User hinweg `Loan`s zu konvertieren. Der _gleiche_ [LoanConverter](src/main/java/com/appletree/lfa/business/convert/LoanConverter.java) kann entsprechend für ein neues `@GetMapping` verwendet werden, das beispielsweise für Kundenberater die Kreditdaten mehrerer Kunden gleichzeitig laden soll (zum Beispiel alle Kunden die im Zahlungsverzug sind).
- Das Objekt [LimitRealSecurity](src/main/java/com/appletree/lfa/data/model/limit/LimitRealSecurity.java) enthält das Feld `type`, welches im Kernbankensystem als `String` modelliert ist. In dieser Applikation wird das `enum` [LimitRealSecurityType](src/main/java/com/appletree/lfa/data/model/limit/LimitRealSecurityType.java) eingeführt, weil es sich um eine _endliche_ Aufzählung von möglichen Werten handelt.
- In der Klasse [FinancingObject](src/main/java/com/appletree/lfa/data/model/financingobject/FinancingObject.java) werden die Backend-Felder `limit` in `limitId` und `products` in `productIds` umbenannt. Die bestehende Bezeichnung `limit` ist gefährlich, weil jemand auf die Idee kommen könnte, dass es sich um eine Limite handelt, und nicht um eine `id`.
- Die `null`-Prüfungen sind auf ein Minimum beschränkt. Die Applikation könnte `null`-Werte deutlich defensiver behandeln.
- Das `Exception`-Handling ist ebenfalls auf ein Minimum reduziert. Es gibt eigentlich nur einen `catch`-Block, der alle möglichen Fehler abfängt und mit einem `httpStatus=500` antwortet.
- Die Tests sind ebenfalls auf ein Minimum beschränkt, da wäre deutlich mehr möglich.
- Im [LoanRestControllerTest](src/test/java/com/appletree/lfa/api/LoanRestControllerTest.java) werden die User Stories getestet. Es handelt sich allerdings eher um Integration-Tests als um Unit-Tests (alternativ wäre mit `@WebMvcTest` ein Slice-Test möglich gewesen).
- Den Testfall zur Story **S4: Identify if any part of a lending is overdue** im [LoanRestControllerTest](src/test/java/com/appletree/lfa/api/LoanRestControllerTest.java) hätte man mit einem `@ParameterizedTest` lösen können.
  ``` java
    @ParameterizedTest
    @CsvSource({
            "false, false, false",
            "true, false, true",
            "false, true, true",
            "true, true, true"
    })
    public void test_method(boolean child1Overdue, boolean child2Overdue, boolean expectedParentOverdue) {}
    ```
  Damit wären die beiden weiteren möglichen Testfälle quasi geschenkt und _ein einziger_ Test hätte ausgereicht. Allerdings passt der momentane Aufbau der `@Test`-Methoden nicht ideal dafür (kein `@Test`-spezifischer Arrange-Teil, in dem die Testdaten aufgebaut werden).
- Das `openapi-generator-maven-plugin` enthält die transitive Vulnerabilität [CVE-2025-48924](https://nvd.nist.gov/vuln/detail/CVE-2025-48924). Es wird empfohlen auf die Version `3.18.0` von [commons-lang3](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3) upzugraden. Im [pom.xml](pom.xml) wurde deshalb die folgende Konfiguration vorgenommen:
  ``` xml
    <properties>
        <commons-lang3.version>3.18.0</commons-lang3.version>
    </properties>
  ```
  Damit ist die Applikation von der Vulnerabilität befreit.
- Mit `module`s anstelle von `package`s könnte man die Schichten [data](src/main/java/com/appletree/lfa/data), [business](src/main/java/com/appletree/lfa/business) und [api](src/main/java/com/appletree/lfa/api) noch stärker kapseln. Das würde zu einem hierarchischen [pom.xml](pom.xml)-Aufbau führen, bei dem jedes `module` nur die für sich relevanten `dependencies` im [pom.xml](pom.xml) enthalten würde. So kann beispielsweise ein Zugriff von [data](src/main/java/com/appletree/lfa/data) nach [business](src/main/java/com/appletree/lfa/business) oder [api](src/main/java/com/appletree/lfa/api) unterbunden werden.

### Schlusswort
Vielen Dank, dass ich diese spannende Aufgabe lösen durfte – es war mir eine Freude 😍


