# ava.lamport: Eine Implementierung von Übung 3
Markus Jungbluth

Dieses Dokument enthält Informationen über den Aufbau und die Funktionen von ava.lamport.

## Einführung
Das vorliegende Programm ist eine Implementierung des Lamport-Algorithmus für gegenseitigen Ausschluss (Mutex).  Er erlaubt den wechselseitigen exklusiven Zugriff unabhängiger verteilter Anwendungen auf eine gemeinsame Ressource.

Ein Mutex wird für gewöhnlich über Semaphoren realisiert, jedoch ist dies bei verteilten Anwendungen nicht möglich, da kein gemeinsamer Speicher verfügbar ist, auf den alle Prozesse zugreifen können. Der Lamport-Algorithmus zielt daher darauf ab, die Funktionalität -die für gewöhnlich durch Semaphoren realisiert wird- allein durch den Austausch von Nachrichten zu ermöglichen. 

### Der Algorithmus
Zur Umsetzung dieser Funktionalität erfordert der Lamport-Algorithmus, dass alle beteiligten Prozesse die Lamport-Zeit implementieren. Dafür führt jeder Prozess einen Counter, der seine aktuelle Lamport-Zeit darstellt. Bei der Kommunikation mit anderen Prozessen synchronisieren sich die Counter gegenseitig auf. Erhält ein Knoten A beispielsweise eine Nachricht von Knoten B mit einer Lamport-Zeit, die größer ist als die Lamport-Zeit von Knoten A, so synchronisiert Knoten A seine Zeit auf die Zeit von Knoten B.
Vor dem Senden und Empfangen von Nachrichten inkrementieren die Knoten ihre Counter. Mit Hilfe dieses Verfahrens können die Knoten die kausalen Abhängigkeiten zwischen verschiedenen Nachrichten feststellen.

###### Senden einer Nachricht
    clock++
    sendMsg(Message, clock)
###### Empfangen einer Nachricht
    (Message, remoteClock) = recvMsg()
    clock = max(clock, remoteClock)+1
    
Der Lamport-Algorithmus für gegenseitigen Ausschluss (Mutex) nutzt diese Möglichkeit um festzustellen, welcher Prozess gerade Zugriff auf eine exklusive Ressource haben darf, und welcher nicht.
Dafür führt er eine sortierte Request-Queue auf jedem Prozess ein. Diese akzeptiert Tupel bestehend aus einem Zeitstempel und einem Prozess-Identifikator.

###### Queue-Datentyp
    <LamportTime, Prozess-ID>

Die Einträge in der Queue werden aufsteigend nach dem ersten Element des Tupels (der Lamport-Zeit) in der Queue angeordnet. Ist die Lamport-Zeit für zwei Einträge gleich, so wird der Eintrag mit einer kleineren Prozess-ID als kleineres Element angesehen.

###### Beispiel für Elemente in der Queue
    [<1,1>, <2,2>, <3,1>]
    
#### Ablauf
Um Zugriff auf die Ressource anzufordern sendet ein Prozess zunächst eine `ACQUIRE` Nachricht an **alle anderen** Prozesse und trägt seine Anfrage in seine lokale Request-Queue ein. 

*Wichtig*: Das versenden der `ACQUIRE` Nachricht an alle anderen Prozesse wird im Kontext des Lamport-Algorithmus als "atomare" Aktion betrachtet. Sprich, die übermittelte Vektorzeit muss für alle `ACQUIRE` Nachrichten gleich sein.

    
    clock++
    addToQueue(own_process, clock)
    
    for process in other_processes:
        sendMsg(ACQUIRE, process, clock)
        
Die empfangenden Prozesse tragen die übermittelte Anfrage unter dem übermittelten TimeStamp in ihre eigenen Queues ein. Sie bestätigen die Eintragung mit einer `ACK` Nachricht an den anfragenden Prozess. Mit dieser Bestätigung wird erneut die Lamport-Zeit zwischen dem anfragenden Prozess und allen anderen synchronisiert.
Ein Prozess kann sicher auf die gemeinsame Ressource zugreifen, wenn er von allen anderen Prozessen eine `ACK` Nachricht erhalten hat und sein eigener Eintrag am Kopf der Queue steht.
Wenn der Prozess den Zugriff auf die gemeinsame Ressource nicht mehr benötigt gibt er sie wieder frei und signalisiert dies über das Versenden einer `RELEASE` Nachricht an alle anderen Prozesse. Die anderen Prozesse entfernen dann den Eintrag aus ihren Queues und der nächste Prozess kann Zugriff auf die freigegeben Ressource erlangen.
    
## Implementierung
### Getting Started
*ava.lamport* implementiert den beschriebenen Algorithmus und demonstriert anhand eines Beispiels seine Funktionalität.

Das Programm stellt *n* Prozessen den wechselseitigen Zugriff auf eine Datei zur Verfügung. Um zu Verhindern, dass es durch den konkurrierenden Zugriff auf die Datei zu Inonsiszenzen kommt, wird durch den Lamport-Algorithmus geregelt, welches Prozess auf die Datei zugreifen darf und welcher warten muss.

Durch Ausführung des Programms aus der `Main` Klasse kann ein Testlauf gestartet werden.
Das Programm startet dadurch automatisch eine als Programm-Argument übergebene gerade Anzahl an Prozessen die um den Zugriff auf die Datei konkurrieren.
Während der Algorithmus arbeitet wird eine detaillierte Log produziert, der er erlaubt die einzelnen Aktionen nachzuvollziehen

###### Beispiel-Log
    04.02.2017 12:32:41 [3] - INFORMATION: Acquire  
    04.02.2017 12:32:41 [1] - INFORMATION: Acquire  
    04.02.2017 12:32:41 [4] - INFORMATION: Acquire  
    04.02.2017 12:32:41 [2] - INFORMATION: Acquire  
    04.02.2017 12:32:41 [1] - INFORMATION: Received ACQUIRE from 3 with t=1 [<1,1>]  
    04.02.2017 12:32:41 [4] - INFORMATION: Received ACQUIRE from 1 with t=1 [<1,4>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received ACQUIRE from 1 with t=1 [<1,3>]  
    04.02.2017 12:32:41 [2] - INFORMATION: Received ACQUIRE from 1 with t=1 [<1,2>]  
    04.02.2017 12:32:41 [4] - INFORMATION: Received ACQUIRE from 3 with t=1 [<1,1>, <1,4>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received ACQUIRE from 2 with t=1 [<1,1>, <1,3>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Received ACQUIRE from 2 with t=1 [<1,1>, <1,3>]  
    04.02.2017 12:32:41 [2] - INFORMATION: Received ACQUIRE from 3 with t=1 [<1,1>, <1,2>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received ACQUIRE from 4 with t=1 [<1,1>, <1,2>, <1,3>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Received ACQUIRE from 4 with t=1 [<1,1>, <1,2>, <1,3>]  
    04.02.2017 12:32:41 [4] - INFORMATION: Received ACQUIRE from 2 with t=1 [<1,1>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [2] - INFORMATION: Received ACQUIRE from 4 with t=1 [<1,1>, <1,2>, <1,3>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received ACK from 1 with t=3 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Received ACK from 4 with t=3 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received ACK from 4 with t=5 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [4] - INFORMATION: Received ACK from 3 with t=7 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [2] - INFORMATION: Received ACK from 3 with t=5 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received ACK from 2 with t=5 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [4] - INFORMATION: Received ACK from 1 with t=7 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Received ACK from 3 with t=3 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [2] - INFORMATION: Received ACK from 1 with t=5 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [4] - INFORMATION: Received ACK from 2 with t=7 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Received ACK from 2 with t=3 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [1] - INFORMATION: [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [2] - INFORMATION: Received ACK from 4 with t=7 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Lock released  
    04.02.2017 12:32:41 [1] - INFORMATION: Release  
    04.02.2017 12:32:41 [2] - INFORMATION: Received RELEASE from 1 with t=11 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [2] - INFORMATION: [<1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [3] - INFORMATION: Received RELEASE from 1 with t=12 [<1,1>, <1,2>, <1,3>, <1,4>]  
    04.02.2017 12:32:41 [1] - INFORMATION: Acquire  
    04.02.2017 12:32:41 [2] - INFORMATION: Lock released  
    04.02.2017 12:32:41 [2] - INFORMATION: Release  


### Protokoll
ava.lamport greift auf die Protokoll-Implementierung der vorhergehenden Übungen zurück und verwendet ein auf TCP/IP basierendes einfaches ASCII-basiertes Kommunikationsprotokoll.
Das verwendete Protokoll wird dabei nur zur unidirektionalen Kommunikation verwendet. Das bedeutet, dass eine sendender Client keine direkte Rückmeldung vom Server über die Ausführung des Kommandos erhält.

#### Methoden
Alle Nachrichten bestehen zwangsläufig aus einer sog. *Methode*.
Die Methode ist ein Bezeichner für das auszuführende Kommando.
Das Protokoll kennt folgende Methoden:
* ACK
* ACQUIRE
* RELEASE
* TERMINATE

In unserem Protokoll kann ein Methodenname von einem Argument gefolgt werden. Es wird als Methoden Argument bezeichnet.

#### Parameter und Identifizierung
Um Nodes zweifelsfrei und eindeutig identifizieren zu können, kennt das verwendete Kommunikationsprotokoll sog. Parameter.
Parameter sind Key-Value Pairs, die durch Zeilenumbruch getrennt unmittelbar auf die Methode folgt.

Jede Nachricht wird vom Sender somit mit einem "SRC" Parameter versehen, der einen Prozess identifiziert.
Weiterhin wird die Lamport-Zeit über einen "TIMESTAMP" Parameter übertragen.

#### Beispiel
    ACK
    SRC: 1
    TIMESTAMP: 12

### Software-Architektur
Die Architektur von ava.lamport verfolgt einen stark modularisierten Objekt-orientierten Ansatz. Uns war es wichtig, die Funktion des realisierten Mutex mit Hilfe des Lamport-Algorithmus derart zu kapseln, dass eine Wiederverwendung uns Austauschbarkeit ermöglicht wird.

Die Funktionen zur Modifikation der gemeinsamen Ressource in Form einer Datei wurden in der Klasse `LamportFile` gesammelt. Die Geschäftslogik der Prozesse wurde in der `LamportProcess` Klasse abgebildet. Sie besitzt eine Referenz auf eine Instanz der `LamportFile` Klasse. Die Funktionen zur Realisierung des Lamport Algorithmus wurden in der Klasse `LamportMutex` verankert, welche von jedem Prozess in eigener Instanz verwendet wird.

Die Klasse LamportMutex stellt dem Prozess zwei Methoden zur Verfügung. Über die `acquire()` Methoden kann der Zugriff auf die Ressource angefordert werden, während die `release()` Methode sie wieder freigibt.

Intern besitzt die Implementierung des LamportMutex eine Semaphore, die es erlaubt den Thread des Prozesses nach Aufruf der `acquire()` Methode zu blockieren. Bevor der Thread an dieser Semaphore wartet, sendet er eine ACQUIRE` Nachricht an alle anderen Prozesse.

Sobald der Server-Thread alle `ACK` Nachrichten erhalten hat und festgestellt hat, dass nun ein Zugriff auf die gemeinsame Ressource möglich ist, weckt er den wartenden Prozess wieder auf und gibt den Zugriff auf die Ressource frei.

Wir möchten an dieser Stelle nochmals hervorheben, dass die verwendeten Semaphore lediglich dazu dient den Prozess so lange zu blockieren, bis der Server-Thread eine Freigabe erhalten hat. Sie dient nicht der Einschränkung des Zugriffs auf die Datei.


