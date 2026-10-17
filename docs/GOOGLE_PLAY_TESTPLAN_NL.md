# Google Play-testplan — KlaarKompas 0.3.0

Dit document is een uitvoerbaar plan voor de telefoon-app en tablet-app. Het is geen bewijs dat Play Console al is ingevuld of dat de apps gepubliceerd zijn. Voer de acties in je eigen Play Console-account uit en noteer de werkelijke resultaten.

## Contactgegevens

- Ontwikkelaarsnaam: DRAVEINX
- Privacy- en ondersteuningscontact: draveinx@gmail.com
- Privacyverklaring: https://github.com/DRAVEINX/KlaarKompas/blob/main/docs/PRIVACY_POLICY_NL.md
- Telefoon-app-ID: \`nl.draveinx.klaarkompas.phone\`
- Tablet-app-ID: \`nl.draveinx.klaarkompas.tablet\`
- Release: 0.3.0 — versionCode 3

Controleer dat beide app-vermeldingen hetzelfde juiste contactadres bevatten en dat de mailbox actief is.

## Voor je begint

- [ ] Download de ondertekende APK's en installeer de telefoon-APK op een echte telefoon en de tablet-APK op een echte tablet.
- [ ] Test de nieuwe functies afzonderlijk op beide apparaten. Een groene build bewijst niet dat de gebruikerservaring op elk apparaat werkt.
- [ ] Werk eerst met testgegevens; gebruik geen wachtwoorden, medische gegevens, bankgegevens of andere zeer gevoelige informatie als gidsinhoud.
- [ ] Maak vóór een restore-test een back-up van gegevens die je wilt bewaren. Terugzetten vervangt bestaande app-gegevens.
- [ ] Controleer dat beide AAB's de juiste app-ID en versionCode 3 hebben. Upload de telefoon-AAB alleen bij de telefoon-app en de tablet-AAB alleen bij de tablet-app.
- [ ] Houd de upload-keystore veilig en maak daarvan een aparte versleutelde reservekopie.

## Functietest — vul per apparaat in

Vul bij elke test **Geslaagd**, **Mislukt** of **Niet getest** in. Noteer bij een fout ook Android-versie, toestelmodel en de stappen om het probleem na te bootsen.

| Test | Wat je controleert | Resultaat / notities |
|---|---|---|
| Startscherm | Recente gidsen, favorieten, hervatten en overzicht voor vandaag passen bij de opgeslagen gegevens. | |
| Zoeken en categorieën | Zoek een woord, een gedeeltelijke term en een term zonder resultaten; open een gids en keer terug. | |
| Spraak invoeren | Spreek titel, samenvatting, stap en waarschuwing in Nederlands in; annuleer herkenning en test ook zonder beschikbare spraakdienst. | |
| Voorlezen | Lees een stap handmatig voor; zet automatisch voorlezen aan en uit; test pauzeren/stoppen en TalkBack. | |
| Dagplanner | Maak een taak voor vandaag en morgen, verander de datum, vink aan/uit, verwijder een taak en sluit/heropen de app. | |
| Herinneringen | Plan een toekomstige melding, geef en weiger de meldingsmachtiging, controleer instelling van Android-meldingen en test opnieuw na herstart. Test niet alleen met een tijdstip in het verleden. | |
| Eigen gids maken | Maak gids met meerdere stappen, leestekens, accenten en regeleinden; probeer leeg titelveld en lege stappen; probeer de limiet van 20 stappen. | |
| Gidsfoto's | Voeg omslagfoto en stapfoto toe, verander/verwijder ze, sluit/heropen de app en controleer dat de foto's zichtbaar blijven. | |
| Tips en waarschuwingen | Bewaar een tip en veiligheidswaarschuwing; open opnieuw en controleer dat beide correct zichtbaar zijn. | |
| Gids beheren | Bewerk, kopieer en verwijder een eigen gids; controleer dat bevestiging wordt gevraagd voor verwijderen en dat ingebouwde gidsen blijven bestaan. | |
| Delen | Tik op delen, kies een testdoel zoals e-mail aan jezelf of een notitie-app en controleer dat alleen de gekozen gidstekst wordt gedeeld. | |
| Toegankelijkheid | Zet grotere tekst, hoog contrast, eenvoudigere uitleg en automatisch voorlezen aan; controleer alle hoofdknoppen met TalkBack. | |
| Back-up maken | Exporteer een ZIP-bestand; controleer dat bestand aangemaakt is. Denk eraan dat deze ZIP niet versleuteld is. | |
| Back-up terugzetten | Gebruik een aparte testset of testinstallatie; herstel gidsen, foto's, favorieten, voortgang, planner en instellingen. Controleer dat bestaande gegevens worden vervangen. | |
| Offlinegebruik | Zet netwerk uit; blader door ingebouwde gidsen, persoonlijke gidsen en lokale taken. Test apart of de gekozen Android-spraakdienst offline werkt. | |
| Schermformaat | Op telefoon: kleine breedte en grotere tekst. Op tablet: staand en liggend. Controleer op afgekapte tekst en verborgen knoppen. | |
| Bijwerken | Installeer de ondertekende 0.3.0-release over een eerdere installatie en controleer of de app bijwerkt zonder gegevensverlies. Gebruik hiervoor de passende, geregistreerde upload key. | |

## Play Console — interne test

Doe dit afzonderlijk voor de twee app-vermeldingen:

1. Open [Google Play Console](https://play.google.com/console/).
2. Kies de app **KlaarKompas – telefoon** en ga naar **Testen en releasen → Testen → Interne test**.
3. Maak of open de interne test, voeg vertrouwde testers toe en upload de **telefoon-AAB** bij deze app.
4. Vul release notes in, controleer de versie en start de uitrol naar de interne test.
5. Open de testlink op een Android-toestel met een account dat aan de test is toegevoegd. Installeer en test.
6. Herhaal dit afzonderlijk voor **KlaarKompas – tablet**, met de tablet-AAB.
7. Test de ondertekende release op echte apparaten. Upload nooit een APK/AAB die als **UNSIGNED** is gemarkeerd.

De interne test is nuttig om upload, installatie, toestellen en basisfuncties te controleren. Voor een persoonlijk ontwikkelaarsaccount dat onder de nieuwe-accountregel valt, vervangt de interne test de verplichte gesloten test niet.

## Als jouw persoonlijke account een gesloten test vereist

Google meldt dat persoonlijke ontwikkelaarsaccounts die na 13 november 2023 zijn aangemaakt vóór productietoegang een gesloten test moeten doorlopen met minimaal 12 testers die zich minimaal 14 aaneengesloten dagen hebben aangemeld. Controleer de vereiste status in je Play Console-dashboard: dit hangt af van je account en het dashboard vertelt welke stappen nog ontbreken.

Als de eis voor jouw account geldt:

1. Voltooi eerst de app-instelling voor de telefoon-app en maak onder **Testen en releasen → Testen → Gesloten test** een gesloten testtrack.
2. Voeg minimaal 12 testers toe via e-mailadressen of een Google-groep. Deel de opt-in-link met hen; alleen hun e-mailadres op de lijst zetten is niet hetzelfde als daadwerkelijk deelnemen.
3. Laat testers de opt-in-stap afronden en controleer dat Play Console hen als aangemeld toont. Vraag hen tijdens de hele testperiode niet uit te schrijven.
4. Noteer startdatum, feedback, fouten, fixes en nieuwe buildnummers. Vraag testers de belangrijke functies daadwerkelijk te testen en hun bevindingen terug te sturen.
5. Houd minimaal 12 testers onafgebroken aangemeld gedurende 14 volledige dagen; houd rekening met vertraging als iemand zich uitschrijft of de deelname niet goed afrondt.
6. Herhaal de gesloten test apart voor de tablet-app als Play Console dit voor die app vraagt; iedere app heeft een eigen pakket-ID en eigen testtrack.
7. Als de periode voorbij is en de dashboardstatus het toestaat, gebruik **Dashboard → Productietoegang aanvragen** en beantwoord de vragen eerlijk over testers, feedback, wijzigingen en gereedheid. Productietoegang wordt niet automatisch verleend alleen omdat 14 dagen verstreken zijn.

Officiële uitleg: https://support.google.com/googleplay/android-developer/answer/14151465?hl=nl

## Testers uitnodigen — voorbeeldbericht

**Onderwerp:** Wil je KlaarKompas 0.3.0 testen?

Hoi! Ik zoek testers voor KlaarKompas, een app met praktische handleidingen in kleine stappen. Wil je via de officiële Google Play-testlink deelnemen en de app gedurende de volledige testperiode geïnstalleerd houden? Test vooral de zoekfunctie, spraakinvoer, dagplanner, herinneringen, eigen gidsen met foto's, toegankelijkheidsinstellingen en back-up/terugzetten. Meld fouten met je apparaatmodel, Android-versie en de stappen waarmee je de fout krijgt. Gebruik alsjeblieft geen echte wachtwoorden of gevoelige gegevens in testgidsen. Bedankt voor je hulp!

Vervang dit bericht door de juiste opt-in-link nadat je de betreffende testtrack hebt aangemaakt. Maak geen testlink voordat Play Console deze heeft aangemaakt.

## Privacybeleid en Data safety

- [ ] Publiceer de privacyverklaring op een stabiele, publiek toegankelijke HTTPS-webpagina.
- [ ] Open de link in een browser waarin niet bij GitHub is ingelogd; controleer dat de pagina zonder extra rechten of bestanden te downloaden leesbaar is.
- [ ] Vul draveinx@gmail.com in als contactadres voor privacy en ondersteuning in beide Play Store-vermeldingen.
- [ ] Controleer de gegevensstromen van de werkelijke Android-spraakherkenningsdienst en tekst-naar-spraakengine op de testapparaten. Afhankelijk van provider en instellingen kunnen audio of tekst door die dienst worden verwerkt.
- [ ] Beoordeel de gegevens die de gebruiker bewust deelt via het Android-deelmenu.
- [ ] Vul Data safety eerlijk in voor elke app zodra dat in de Play Console vereist is. De officiële Google-uitleg zegt dat apps die uitsluitend actief zijn op de interne testtrack hiervan zijn vrijgesteld; bij gesloten/open/productietracks is de verklaring vereist. Controleer de huidige console-status.
- [ ] Controleer doelgroep/leeftijd, inhoudsclassificatie, advertentiesverklaring, app-toegang en andere Play Console-vragen. Beantwoord geen verklaring op basis van aannames.
- [ ] Maak echte screenshots van de huidige release op een telefoon en tablet. Gebruik geen ontwerpconcept-afbeeldingen alsof het echte app-schermen zijn.

Officiële Data safety-uitleg: https://support.google.com/googleplay/android-developer/answer/10787469?hl=nl

## Bijhouden van testers en resultaten

| # | Naam/alias of e-mail (privé bijhouden) | Telefoon of tablet | Opt-in bevestigd op | Feedback ontvangen | Problemen opgelost |
|---:|---|---|---|---|---|
| 1 | | | | | |
| 2 | | | | | |
| 3 | | | | | |
| 4 | | | | | |
| 5 | | | | | |
| 6 | | | | | |
| 7 | | | | | |
| 8 | | | | | |
| 9 | | | | | |
| 10 | | | | | |
| 11 | | | | | |
| 12 | | | | | |

Bewaar de e-mailadressen van testers privé; zet ze niet in deze openbare GitHub-documentatie. Houd de ingevulde kopie lokaal of in een privébestand.

## Definitieve go/no-go

Ga pas naar productie wanneer:
- de relevante tests op echte telefoon en tablet zijn geslaagd;
- privacyverklaring en Data safety de werkelijke app en gebruikte diensten correct beschrijven;
- de juiste ondertekende AAB voor de juiste app is geüpload;
- Play Console geen open verplichte taken of beleidwaarschuwingen toont; en
- eventuele vereiste gesloten test is afgerond en productietoegang is toegekend.
