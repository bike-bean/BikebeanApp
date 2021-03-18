
# Technical Overview

## Use Cases

![](img/Overview.png)

## Views

![](img/Overview_Views.png)

## Detailed Use Cases

### Read Settings

![](img/UC_Read_Settings.png)

### Change Settings

![](img/UC_Change_Settings.png)

## SMS API

| SMS Befehl    | Antwort                              | Beschreibung | Umgesetzt? |
| ------------- | ------------------------------------ | ------------ | ---------- |
| Pos           | \[ `mcc`,`mnc`,`lac(Hex)`,`cellid (Hex)`,`Signal strength` \]<br>.............<br>Battery Status: `xx %` | Sendet die Daten der umliegenden GSM-Funktürme. Die einzelnen Funktürme kannst du bei https://cellidfinder.com/eingeben und triangulieren. In Zukunft ist zur Nutzerfreundlichkeit eine App geplant. | &#9745;<br>(s. Wapp) |
| Status        | Warningnumber: `Warningnumber`<br>Interval: `Interval h`<br>Wifi Status: `<on/off>`<br>Battery Status: `xx %` | Sendet Informationen über die aktuellen Einstellungen und den Akkustand. | &#9744; (1) |
| Wifi on       | \[ `SSID`,`Signal quality in dbm` \]<br>Wifi is on!<br>Battery Status: `xx %` | Sendet die umliegenden Wifis und schaltet das BikeBean-Wifi mit der SSID "Bike-Bean" an. Bike Bean schaltet alle 15 Minuten für ca. 30 Sekunden das Wifi ab und danach wieder an um Empfangsstörungen für die GSM-Verbindung zu vermeiden und auf eventuelle SMS-Befehle antworten zu können. | &#9744; (2) |
| Wifi off      | Wifi Off<br>Battery Status: `xx %` | Schaltet das Wifi mit der SSID "Bike Bean" aus. | &#9744; (2) |
| Warningnumber | Warningnumber has been changed to `warningnumber`<br>Battery Status: `xx %` | Stellt die Nummer ein, die genutzt werden soll um einen niedrigen Akkustatuszu melden. Es wird automatisch die Nummer eingestellt, von der der Befehl abgesendet wird. Bei einem Akkustand von 20% und 10% wird jeweils einmal gewarnt. Wenn BikeBean vom Strom getrennt wird, muss dieser Befehl neu gesendet werden um die Nummer wieder einzurichten. | &#9744; (2) |
| Wapp          | \[ `Signal Strength` `Mac Adresse ohne ":"` \]<br>..............<br>`Battery Status`<br>\[ `mcc`,`mnc`,`lac(Hex)`,`cellid (Hex)`,`Signal strength` \]<br>............. | Dieser Befehl ist für die geplante App. Er sendet die Rohdaten (umliegende Wlans & Funktürme) | &#9745; |
| Int \<x\>     | GSM will be switched on every \<x\> hour(s).<br>Battery Status: `xx %` | Einmal alle \<x\> Stunden stellt sich Bike Bean an um SMS-Befehle entgegenzunehmen. | &#9744; (2) |

### Tests

To test the SMS API, use the following strings:

| SMS Befehl    | Text |
| ------------- | ------------------------------------ |
| Pos           | 262,03,55f1,a473,36<br>262,03,55f1,5653,21<br>262,03,55f1,4400,20<br>262,03,55f1,8b40,12<br>262,03,55f1,6bb2,10<br>262,03,55f1,0833,09<br>262,03,55f1,6bcd,03<br>.............<br>Battery Status: 30% |
| Status        | Warningnumber: 015112345678<br>Interval: 12h<br>Wifi Status: on<br>Battery Status: 90% |
| Wifi on       | Fancy Wifi SSID,30<br>Another Wifi SSID,60<br>Wifi is on!<br>Battery Status: 5% |
| Wifi off      | Wifi Off<br>Battery Status: 100% |
| Warningnumber | Warningnumber has been changed to 0179987654321<br>Battery Status: 15% |
| Wapp          | 77788102493fd4<br>831062e5b58896<br>6564cc22b982ba<br>6758904350f67d<br>42<br>..............<br>262,03,55f1,a473,34<br>262,03,55f1,efb4,21<br>262,03,55f1,5653,21<br>262,03,55f1,4400,21<br>262,03,55f1,8b40,20<br>262,03,55f1,efb6,13<br>262,03,55f1,6bb2,11<br> |
| Int \<x\>     | GSM will be switched on every 12 hours.<br>Battery Status: 64% |
| **Edge Cases** |  |
| Low Battery   | BATTERY LOW!<br>BATTERY STATUS: 19% |
| Very Low Battery | BATTERY LOW!<br>BATTERY STATUS: 8% <br>Interval set to 24h |
| No Wifi Available | no wifi available |
| No Wifi Available 2 | no wifi available<br> <br> |
