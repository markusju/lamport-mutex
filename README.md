# ava.lamport: Eine Implementierung von Übung 3
Markus Jungbluth

Dieses Dokument enthält Informationen über den Aufbau und die Funktionen von ava.lamport.

## Einführung
Das vorliegende Programm ist eine Implementierung des Lamport-Algorithmus für gegenseitigen Ausschluss (Mutex).  Er erlaubt den wechselseitigen exklusiven Zugriff unabhängiger verteilter Anwendungen auf eine gemeinsame Resource.

Ein Mutex wird für gewöhnlich über Semaphoren realisiert, jedoch ist dies bei verteilten Anwendungen nicht möglich, da kein gemeinsamer Speicher verfügbar ist, auf den alle Prozesse zugreifen können. Der Lamport-Algorithmus zielt daher darauf ab, die Funktionalität -die für gewöhnlich durch Semaphoren realisiert wird- allein durch den Austausch von Nachrichten zu ermöglichen. 

### Der Algorithmus
Zur Umsetzung dieser Funktionalität erfordert der Lamport-Algorithmus, dass alle beteiligten Prozesse die Lamport-Zeit implementieren. Dafür führt jeder Prozess einen Counter, der seine aktuelle Lamport-Zeit darstellt. Bei der Kommunikation mit anderen Prozessen synchronisieren sich die Counter gegenseitig auf. Erhält ein Knoten A beispielsweise eine Nachricht von Knoten B mit einer Lamport-Zeit, die größer ist als die Lamport-Zeit von Knoten A, so synchronisiert Knoten A seine Zeit auf die Zeit von Knoten B.
Vor dem Senden und Empfangen von Nachrichten inkrementieren die Knoten ihre Counter. Mit Hilfe dieses Verfahrens können die Knonten die kausalen Abhängigkeiten zwischen verschiedenen Nachrichten feststellen.

###### Senden einer Nachricht
    clock++
    sendMsg(Message, clock)
###### Empfangen einer Nachricht
    (Message, remoteClock) = recvMsg()
    clock = max(clock, remoteClock)+1
    
Der Lamport-Algorithmus für gegenseitgen Ausschluss (Mutex) nutzt diese Möglichkeit um festzustellen, welcher Prozess gerade Zugriff auf eine exklusive Ressource haben darf, und welcher nicht.
Dafür führt er eine sortierte Request-Queue auf jedem Prozess ein. Diese akzeptiert Tupel bestehend aus einem Zeitstempel und einem Prozess-Identifikator.

###### Queue-Datentyp
    <LamportTime, Prozess-ID>

Die Einträge in der Queue werden aufsteigend nach dem ersten Element des Tupels (der Lamport-Zeit) in der Queue angeordnet. Ist die Lamport-Zeit für zwei Einträge gleich, so wird der Eintrag mit einer kleineren Prozess-ID als kleiners Element angesehen.

###### Beispiel für Elemente in der Queue
    [<1,1>, <2,2>, <3,1>]
    
#### Ablauf
Um Zugriff auf die Resource anzufordern sendet ein Prozess zunächst eine `ACQUIRE` Nachricht an **alle anderen** Prozesse und trägt seine Anfrage in seine lokale Request-Queue ein. 

*Wichtig*: Das versenden der `ACQUIRE` Nachricht an alle anderen Prozesse wird im Kontext des Lamport-Algorithmus als "atomare" Aktion betrachtet. Sprich, die übermittelte Vektorzeit muss für alle `ACQUIRE` Nachrichten gleich sein.

    
    clock++
    addToQueue(own_process, clock)
    
    for process in other_processes:
        sendMsg(ACQUIRE, process, clock)
        
Die empfangenden Prozesse tragen die übermittelte Anfrage unter dem übermittelten TimeStamp in ihre eigenen Queues ein. Sie bestätigen die Eintragung mit einer `ACK` Nachricht an den anfragenden Prozess. Mit dieser Bestätigung wird erneut die Lamport-Zeit zwischen dem anfragenden Prozess und allen anderen synchronisiert.
Ein Prozess kann sicher auf die gemeinsame Resource zugreifen, wenn er von allen anderen Prozessen eine `ACK` Nachricht erhalten hat und sein eigener Eintrag am Kopf der Queue steht.
Wenn der Prozess den Zugriff auf die gemeinesame Ressource nicht mehr benötigt gibt er sie wieder frei und signalisiert dies über das Versenden einer `RELEASE` Nachricht an alle anderen Prozesse. Die anderen Prozesse entfernen dann den Eintrag aus ihren Queues und der nächste Prozess kann Zugriff auf die freigegeben Ressource erlangen.
    
## Getting Started


