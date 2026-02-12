package de.myorg.myapp.parts;


import de.myorg.myapp.ModelPart;
import de.zeb.lowcode.generator.persistenz.AuditEntitaetsfeld;
import de.zeb.lowcode.model.domain.*;
import de.zeb.lowcode.model.domain.DomainModel.DomainModelBuilder;

public class Bericht implements ModelPart {

    public final static String PAKET = "bericht";


    public static Entitaet getBericht() {
        return Entitaet.builder().name("Bericht")
                .paket(PAKET)
                .abstrakt(false)
                .beschreibung("Berichte sind die Grundlage für die Berichtserstellung, Abfrageausführung, etc. Sie enthalten alle notwendigen Informationen um eine Abfrage auszuführen oder einen Bericht zu erstellen.")
                .erbtVon(Querschnitt.getMyAppBasisElementRef())
                .dbTabellenname(Querschnitt.getMyAppBasisElement().getDbTabellenname())
                .feld(getDb())
                .feld(getInstitute())
                .feld(getAblaufzeitraum())
                .feld(AuditEntitaetsfeld.builder()
                        .name("vertrieblicheNutzung")
                        .fachlicherName("vertriebliche Nutzung")
                        .dbSpaltenname("VTRB_NTZG_KZ")
                        .beschreibung("Wird die Abfrage vertrieblich genutzt? Hat Auswirkung auf DSGVO")
                        .datenTyp(Datentyp.BOOLEAN)
                        .optional(true)
                        .build()
                )
                .feld(AuditEntitaetsfeld.builder()
                        .name("berichtsinhalt")
                        .fachlicherName("Design des Berichts")
                        .dbSpaltenname("BERICHT")
                        .beschreibung("Das JSON definiert den Inhalt des Berichts, z.B. welche Kennzahlen, Dimensionen, etc. enthalten sein sollen.")
                        .datenTyp(Datentyp.BINARY)
                        .optional(false)
                        .build()
                )
                .build();
    }

    public static Entitaetsfeld getAblaufzeitraum() {
        return AuditEntitaetsfeld.builder()
                .name("ablaufzeitraum")
                .beschreibung("Gibt an nach welchem Zeitraum ein Ergebnis abgelaufen ist und gelöscht wird.")
                .dbSpaltenname("ERGS_ABLF_ZRM")
                .datenTyp(Datentyp.TEXT)
                .optional(true)
                .build();
    }

    private static Entitaetsfeld getInstitute() {
        return Entitaetsfeld.builder()
                .name("institute")
                .fachlicherName("institute")
                .dbSpaltenname("INST_GLBL")
                .beschreibung("Institut, für das die globale Berichtserstellung/Abfrageausführung/etc durchgeführt werden soll")
                .datenTyp(Datentyp.TEXT)
                .alsListe(true)
                .build();
    }


    public static Entitaetsfeld getDb() {
        Wertebereich abfrageDatenquelle = Wertebereich.builder()
                .name("AbfrageDatenquelle")
                .paket(PAKET)
                .eintrag(new WertebereichEintrag("Exa", "Exasol"))
                .eintrag(new WertebereichEintrag("Datalake", "Data Lakehouse"))
                .eintrag(new WertebereichEintrag("DB2", "DB2"))
                .build();

        return AuditEntitaetsfeld.builder()
                .name("dbType")
                .dbSpaltenname("ZIEL_DB")
                .beschreibung("Zieldatenbank")
                .wertebereich(abfrageDatenquelle)
                .optional(false)
                .datenTyp(Datentyp.TEXT)
                .build();
    }


    @Override
    public void buildDomainModel(final DomainModelBuilder domain) {
        
        Entitaet abfrage = getBericht();
        domain.entitaet(abfrage);
    }

}
