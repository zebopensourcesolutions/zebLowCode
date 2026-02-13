package de.myorg.myapp.parts;


import de.myorg.myapp.ModelPart;
import de.zeb.lowcode.generator.persistenz.AuditEntitaetsfeld;
import de.zeb.lowcode.generator.persistenz.IdEntitaetsfeld;
import de.zeb.lowcode.model.domain.*;
import de.zeb.lowcode.model.domain.DomainModel.DomainModelBuilder;

public class Querschnitt implements ModelPart {

    private final static String PAKET = "common";

    public static Wertebereich getStatusWertebereich() {
        return Wertebereich.builder()
                .name("Status")
                .paket(PAKET)
                .eintrag(new WertebereichEintrag("IN_BEARBEITUNG", "in Bearbeitung"))
                .eintrag(new WertebereichEintrag("ZUR_FREIGABE", "Freigabeprüfung"))
                .eintrag(new WertebereichEintrag("FREIGEGEBEN", "Freigegeben"))
                .eintrag(new WertebereichEintrag("ZUR_INAKTIVIERUNG", "Inaktivierungsprüfung"))
                .eintrag(new WertebereichEintrag("INAKTIV", "Inaktiv"))
                .build();
    }

    public static Entitaetreferenz getMyAppBasisElementRef() {

        return Entitaetreferenz.builder()
                .paket(PAKET)
                .name("MyAppBasisElement")
                .build();
    }


    public static Entitaet getMyAppBasisElement() {
        Entitaetsfeld alias = getAlias();
        return Entitaet.builder()
                .paket(getMyAppBasisElementRef().getPaket())
                .name(getMyAppBasisElementRef().getName())
                .dbTabellenname("MYDB_BSIS_ELEM")
                .beschreibung("Alle Basis-Objekte (Berichte etc)")
                .feld(getId("BSIS_ELEM"))
                .feld(getName("BSIS_ELEM"))
                .feld(getBeschreibung("BSIS_ELEM"))
                .feld(getLetzterAenderungszeitpunktFeld())
                .feld(getLetzterBearbeiterUidFeld())
                .feld(getVersionFeld())
                .feld(getStatus())
                .feld(alias)
                .dbIndex(DbIndex.builder()
                        .name("mobi_bsis_elem_alias")
                        .spalte(alias)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("tags")
                        .beschreibung("Tags zu Identifikation eines Elements")
                        .fachlicherName("Tags")
                        .dbSpaltenname("STWT")
                        .datenTyp(Datentyp.TEXT)
                        .persistenz(true)
                        .alsListe(true)
                        .build()
                )
                .build();


    }

    public static Entitaetsfeld getAlias() {
        return AuditEntitaetsfeld.builder()
                .name("alias")
                .beschreibung("Alias des Elements (zur Ansprache von außen)")
                .fachlicherName("Alias")
                .dbSpaltenname("ALIAS")
                .datenTyp(Datentyp.TEXT)
                .optional(true)
                .build();
    }


    protected static Entitaetsfeld getVersionFeld() {
        //Versionsfeld wird automatisch durch JPA gepflegt, daher keine Protokollierung bei Änderungen
        return Entitaetsfeld.builder()
                .name("version")
                .fachlicherName("Version")
                .dbSpaltenname("VERS_NR_ELEM")
                .beschreibung("Versionsnummer des Elements")
                .datenTyp(Datentyp.VERSION)
                .build();
    }

    protected static Entitaetsfeld getLetzterBearbeiterUidFeld() {
        return Entitaetsfeld.builder()
                .name("letzterBearbeiterUid")
                .fachlicherName("Letzter Bearbeiter UID")
                .beschreibung("Letzter Bearbeiter UID")
                .datenTyp(Datentyp.TEXT)
                .dbSpaltenname("LTZT_AEND_BNTZ_ID")
                .build();
    }

    protected static Entitaetsfeld getLetzterAenderungszeitpunktFeld() {
        return Entitaetsfeld.builder()
                .name("letzterAenderungszeitpunkt")
                .fachlicherName("Letzter Änderungszeitpunkt")
                .beschreibung("Letzter Änderungszeitpunkt")
                .datenTyp(Datentyp.ZEITSTEMPEL)
                .dbSpaltenname("LTZT_AEND_ZPKT")
                .build();
    }

    public static Entitaetsfeld getBeschreibung(String prefix) {
        return AuditEntitaetsfeld.builder()
                .name("beschreibung")
                .fachlicherName("Beschreibung")
                .beschreibung("Vom Anwender vergebene Beschreibung für " + prefix)
                .dbSpaltenname(prefix + "_BSBG")
                .datenTyp(Datentyp.TEXT)
                .anzahlZeichen(8192)
                .optional(true)
                .build();
    }


    protected static Entitaetsfeld getStatus() {
        return AuditEntitaetsfeld.builder()
                .name("status")
                .fachlicherName("Status")
                .beschreibung("Status des Elements")
                .datenTyp(Datentyp.TEXT)
                .wertebereich(getStatusWertebereich())
                .dbSpaltenname("STAT_ELEM")
                .build();
    }


    public static Entitaetsfeld getId(String prefix) {
        return IdEntitaetsfeld.builder()
                .name("id")
                .fachlicherName("Eindeutige ID")
                .beschreibung("Eindeutige ID für " + prefix)
                .dbSpaltenname(prefix + "_ID")
                .datenTyp(Datentyp.ID)
                .pk(true)
                .build();
    }

    public static Entitaetsfeld getName(String prefix) {
        return AuditEntitaetsfeld.builder()
                .name("name")
                .fachlicherName("Name")
                .dbSpaltenname(prefix + "_NAME")
                .beschreibung("Vom Anwender vergebener Name für " + prefix)
                .datenTyp(Datentyp.TEXT)
                .build();
    }

    @Override
    public void buildDomainModel(final DomainModelBuilder domain) {
        domain.entitaet(getMyAppBasisElement());
    }

}
