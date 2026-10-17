# KlaarKompas — bouwplan functies telefoon en tablet

Status bijgewerkt: 17 oktober 2026. Dit plan gaat uit van de huidige Kotlin/Jetpack Compose-code in `main`, met afzonderlijke Android-productvarianten `phone` en `tablet`. De telefoon- en tabletversie blijven dezelfde lokale gegevensmodellen gebruiken; verschillen zitten in de presentatie en bediening.

## Wat is al aanwezig in de huidige broncode?

- **Dagelijkse taken en afvinken:** de Dagplanner bewaart taken lokaal, ondersteunt datum/tijd, afvinken en optionele Android-herinneringen.
- **Handleidingen met foto's en spraak:** persoonlijke gidsen met stappen en afbeeldingen, spraak-naar-tekst via de Android-herkenningsdienst, voorlezen via Android TTS, zoeken, favorieten en voortgang.
- **Offline:** de ingebouwde gidsbibliotheek en de huidige kernfuncties werken lokaal; de app vraagt op dit moment geen internettoegang aan.
- **Toegankelijkheid:** grotere tekst, hoog contrast, eenvoudige taal en automatisch voorlezen van stappen zijn aanwezig. TalkBack moet op echte apparaten getest blijven worden.
- **Back-up:** handmatig ZIP-bestand met voorkeuren en eigen gidsfoto's, met validatie bij terugzetten. Dit ZIP-bestand is **niet versleuteld**; deel het niet onbeveiligd.
- **Samen-doen:** er is een lokale hulpruimte en basislogica voor stap-voor-stap samenwerken, maar geen online begeleidersaccount.

## Verantwoord geïmplementeerd in deze wijziging

### Mijn gevoel: stemming, dagboek en tekenen
- Nieuwe functie bereikbaar via **Meer functies → Mijn gevoel**.
- Keuze uit zes stemmingen; notitie schrijven; met een vinger tekenen; laatste lijn ongedaan maken of tekening wissen.
- Momenten worden lokaal in DataStore opgeslagen en zijn zonder internet beschikbaar.
- Tekst kan worden voorgelezen met de bestaande Android-voorleesfunctie.
- De dagboekgeschiedenis is begrensd op 100 momenten; notities en tekengegevens hebben lengtebeperkingen.
- Dagboekgegevens worden meegenomen in de bestaande back-up/terugzetfunctie.
- Er is geen netwerkverkeer, account of toestemming voor contacten/camera toegevoegd.

Dit is een eerste bruikbare lokale versie, geen klinische stemmingstest en geen vervanging voor professionele hulp.

## Gefaseerd bouwplan

### Fase 1 — betrouwbare offline basis
1. **Dagelijkse routines en herbruikbare afvinklijsten**
   - Dagplanner aanvullen met sjablonen zoals ochtend, vertrek, thuiskomen en avond.
   - Herhaalregels pas toevoegen nadat tijdzone-, zomer/wintertijd- en meldingsgedrag getest zijn.
   - Acceptatie: taken blijven na herstart bewaard; afvinken is toegankelijk met TalkBack; herinneringen zijn optioneel.
2. **Handleidingen met foto's en spraak**
   - Huidige gidsmaker uitbreiden met spraak-naar-tekst in de titel, samenvatting en elke stap.
   - Per stap foto toevoegen/vervangen/verwijderen; foutmeldingen tonen als een foto niet kan worden gekopieerd.
   - Acceptatie: gids en afbeeldingen overleven app-herstart en back-up/terugzetten.
3. **Stemming, tekenen en dagboek**
   - Huidige eerste versie testen op kleine telefoon- en grote tabletschermen.
   - Daarna bewerken/verwijderen, datum kiezen en exportkeuze met expliciete waarschuwing toevoegen.
   - Acceptatie: codec-tests, datalimieten en back-upcompatibiliteit slagen.
4. **Toegankelijkheid**
   - Semantische labels, focusvolgorde, minimaal aanraakdoel, tekstschaling en contrast controleren.
   - Geen betekenis uitsluitend via kleur of emoji overbrengen.
   - Acceptatie: handmatige TalkBack-test op telefoon en tablet plus lint- en Compose-tests.

### Fase 2 — veilige hulp en afspraken
1. **Grote hulpknop en vertrouwde contacten**
   - Configureerbare contactpersonen alleen met expliciete toestemming; gebruik eerst Android-kiezer/telefoon-app, niet stilzwijgend bellen of berichten sturen.
   - Toon vóór delen/bellen wie wordt benaderd; houd een offline noodtekst beschikbaar.
   - Geen automatische verzending van locatie of dagboek.
2. **Afspraken en voorbereiding**
   - Afspraken met datum/tijd, plek, wat meenemen en vragen om te stellen.
   - Herinneringen blijven optioneel; geen medische of zorgafspraak automatisch naar derden sturen.
3. **Begeleiders-, ouder- en voogdomgeving**
   - Eerst lokale rolweergave en duidelijke toestemmingen ontwerpen.
   - Begeleiders krijgen niet automatisch toegang tot alle persoonlijke notities/tekeningen.
   - Voor accounts, rollen en toegang op afstand zijn identiteitscontrole, intrekbare toestemming, auditlog, gegevensverwijdering en privacybeoordeling nodig.

### Fase 3 — QR en zoeken op foto
1. **QR-scanner**
   - Alleen starten na bewuste tik en cameratoestemming; QR-code moet naar een KlaarKompas-gids-ID verwijzen of gewone tekst tonen.
   - Onbekende URL's nooit automatisch openen; toon de bestemming eerst.
   - Test ongeldige, beschadigde en kwaadaardige inhoud.
2. **Zoeken op foto**
   - Begin met lokaal kiezen van een bestaande foto en door de gebruiker bevestigde suggesties.
   - Geen foto uploaden naar een server zonder duidelijke toestemming en een gedocumenteerde gegevensstroom.
   - OCR/beeldherkenning pas toevoegen na keuze van een onderhoudbare bibliotheek en tests voor offline werking, prestaties en privacy.

### Fase 4 — beveiligde synchronisatie telefoon ↔ tablet
- Niet doen door alleen een netwerktoestemming toe te voegen. De huidige app heeft geen synchronisatieserver en geen gebruikersauthenticatie.
- Eerst backend en dreigingsmodel ontwerpen: accountbeveiliging, TLS, versleuteling van gevoelige inhoud, sleutelbeheer, minimale dataverzameling, herstel, export/verwijderen, logging zonder dagboekinhoud en conflictafhandeling.
- Gebruik een offline-first architectuur: lokaal blijft de bron voor directe schermweergave; synchronisatie wordt een aparte repository/werktaak met retry, versiebeheer en conflictafhandeling.
- Begin met expliciete, door de gebruiker gestarte overdracht tussen twee apparaten; pas daarna automatische synchronisatie.
- Acceptatie: tests voor verkeerde account, ingetrokken toestemming, offline wijzigingen op beide apparaten, conflict, mislukte upload, accountverwijdering en herstel.

## Telefoon- en tabletontwerp

- **Telefoon:** één hoofdtaak per scherm, grote duidelijke knoppen, scrollbare invoer en onderste navigatie.
- **Tablet:** brede layout met zij-navigatie, ruimere tekenruimte en twee kolommen waar het helpt; invoervelden en knoppen niet onnodig uitrekken.
- Beide varianten gebruiken dezelfde opslagformaten en versieerbare codecs, zodat back-ups tussen form factors uitwisselbaar blijven.
- Elke fase bouwt en test minimaal `testPhoneDebugUnitTest`, `testTabletDebugUnitTest`, `lintPhoneDebug` en `lintTabletDebug`, plus handmatige apparaatcontroles.

## Harde releasevoorwaarden

- Geen nieuwe functie wordt als klaar gemeld op basis van alleen een groene build.
- Test bestaande gidsen, foto's, favorieten, voortgang, planner, herinneringen en back-up/terugzetten na elke gegevensschemawijziging.
- De huidige ZIP-back-up is niet versleuteld; behandel hem als een bestand met persoonlijke informatie.
- Geen begeleidersportaal of cloudsync publiceren voordat server, authenticatie, toestemming, gegevensstromen, privacyverklaring en beveiliging aantoonbaar gereed zijn.
