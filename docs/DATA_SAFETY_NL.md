# Google Play Data safety — voorbereidingsnotitie voor versie 0.3.0

**Ontwikkelaarscontact:** DRAVEINX — draveinx@gmail.com

**Belangrijk:** dit document is een controlehulp, geen definitieve Data safety-verklaring. De ontwikkelaar moet de werkelijke uitvoering op de doelapparaten en de gegevenspraktijken van de gebruikte externe diensten beoordelen voordat het formulier in Play Console wordt ingediend. Vul het formulier afzonderlijk in voor de telefoon- en tablet-app, ook al delen ze dezelfde broncode.

Officiële informatie:
- [Data safety in Play Console](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Google Play User Data-beleid](https://support.google.com/googleplay/android-developer/answer/10144311)

## Gedrag dat uit de app-code blijkt

De versie 0.3.0 heeft geen eigen account, eigen cloudserver, automatische synchronisatie, advertentie-SDK of analysetracker. De app vraagt zelf geen algemene INTERNET-permissie aan. Standaard worden de volgende gegevens lokaal bewaard:

- favorieten, voortgang en recent geopende gidsen;
- persoonlijke handleidingen, titel, stappen, tips en waarschuwingen;
- door de gebruiker geselecteerde gidsfoto's;
- taaknamen, datums, tijden, voltooiingsstatus en herinneringsinstellingen;
- toegankelijkheids- en voorleesvoorkeuren.

Het opslaan van informatie binnen de app op hetzelfde apparaat is op zichzelf niet hetzelfde als gegevens van het apparaat verzamelen volgens de algemene uitleg in het Google Play-formulier. Dit is echter niet voldoende om zonder verdere controle te concluderen dat het hele product niets verwerkt of deelt.

## Functies waarvoor extra verificatie nodig is

### 1. Inspreken / spraakherkenning

De app opent Androids spraakherkenningsactiviteit wanneer de gebruiker expliciet op een inspreekknop drukt. Het systeem retourneert herkende tekst aan de app. De gekozen spraakprovider en diens instellingen bepalen of audio of tekst lokaal of via een dienst buiten het apparaat wordt verwerkt.

Controleer op een echte telefoon en tablet welke spraakdienst actief is, of offline herkenning beschikbaar is en wat de provider in zijn eigen privacy-/Data safety-informatie zegt. Maak geen aanname dat gesproken data nooit van het apparaat afgaat alleen omdat de KlaarKompas-app zelf geen INTERNET-permissie heeft.

### 2. Tekst-naar-spraak

De app gebruikt de Android TextToSpeech-service die op het apparaat is ingesteld. Controleer of de gekozen engine lokaal werkt of externe verwerking gebruikt en beoordeel de gegevenspraktijken van die engine.

### 3. Door de gebruiker delen

Wanneer iemand op 'Deel deze handleiding' tikt en vervolgens een app in het Android-deelmenu kiest, ontvangt die gekozen app de tekst van de geselecteerde handleiding. De ontvanger kan daardoor de titel, samenvatting, instructiestappen, tips en waarschuwingen ontvangen. Neem deze bewuste deelmogelijkheid mee in je beoordeling en in de privacy-uitleg.

### 4. Back-upbestand

Een back-up wordt alleen op een door de gebruiker gekozen locatie weggeschreven; KlaarKompas uploadt deze niet zelf. Het bestand is een niet-versleuteld ZIP-bestand. Leg uit dat de inhoud toegankelijk kan zijn voor iedereen die toegang heeft tot het bestand. Controleer of het gekozen opslagdoel synchroniseert of bestanden deelt via een afzonderlijke dienst; dat gedrag ligt buiten de eigen app.

### 5. Meldingen en foto's

Herinneringen zijn lokale Android-meldingen en kunnen de titel van een taak weergeven. Foto's die de gebruiker kiest, worden naar de interne app-opslag gekopieerd. De app heeft geen eigen online fotoverwerking.

## Voordat je het formulier indient

- [ ] Controleer de volledige APK/AAB en de definitieve dependencies voor exact versie 0.3.0.
- [ ] Test welke spraakherkennings- en tekst-naar-spraakdienst werkelijk op de apparaten wordt gebruikt.
- [ ] Controleer de privacy- en Data safety-informatie van die dienst en bepaal of gegevens buiten het apparaat worden verwerkt.
- [ ] Beoordeel hoe de handmatig gekozen Android-deelfunctie in het formulier moet worden verklaard.
- [ ] Controleer dat privacybeleid, in-app tekst, store listing en Data safety-antwoorden elkaar niet tegenspreken.
- [ ] Bewaar de ingevulde antwoorden en werk ze bij zodra de app, dependencies of gegevensstromen veranderen.

Vul het formulier niet op basis van deze tekst automatisch als 'geen gegevens verzameld of gedeeld' in. Alleen de ontwikkelaar, na verificatie van de gebruikte diensten en de werkelijke app, kan die verklaring betrouwbaar doen.
