# zebLowCode
ZebLowCode ist eine Sammlung an Modulen die bei der Entwicklung von Software helfen. 
Die Idee ist, dass das Domänenmodell per Builderpattern in Javacode abgebildet wird und daraus für Zielsprachen und Frameworks konkrete Implementierungen generiert werden können.

# Umfang Version 0.1
- Komplette JPA / Spring Data / Hibernate 6 Persistenz
- Domänenmodell in Java und Typescript

# Ablauf
Als Verwender sollte man sich ein Modell-Projekt bauen. In diesem liegen alle Modellklassen die das Domänenmodell abbilden. Aus dem Modellprojekt heraus wird der relevante Code generiert und dann in produktiven Modulen genutzt.

Ein Beispielprojekt folgt.
