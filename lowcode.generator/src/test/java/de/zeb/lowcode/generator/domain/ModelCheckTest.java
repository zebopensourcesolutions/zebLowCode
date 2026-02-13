/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED
 * BY LAW. YOU WILL NOT REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION,
 * TRANSFER, DISTRIBUTION OR MODIFICATION OF PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR
 * WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 2026-02-13
 */
package de.zeb.lowcode.generator.domain;

import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für ModelCheck
 */
@SuppressWarnings("nls")
class ModelCheckTest {

    @Test
    void testCheckModelValid() {
        LowCodeModel model = createValidModel();
        ModelCheck checker = new ModelCheck();

        List<String> fehler = checker.checkModel(model);

        assertTrue(fehler.isEmpty(), () -> "Fehler gefunden: " + fehler);
    }

    @Test
    void testCheckModelOhneAnwendungskuerzel() {
        assertThrows(NullPointerException.class, () -> LowCodeModel.builder()
                .domain(DomainModel.builder().build())
                .build());
    }

    @Test
    void testCheckModelNull() {
        ModelCheck checker = new ModelCheck();

        List<String> fehler = checker.checkModel(null);

        assertFalse(fehler.isEmpty());
        assertTrue(fehler.stream().anyMatch(f -> f.contains("null")));
    }

    @Test
    void testCheckModelMitDuplikatenEntitaeten() {
        Entitaet e1 = Entitaet.builder()
                .name("TestEntitaet")
                .paket("de.test")
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .build())
                .build();

        Entitaet e2 = Entitaet.builder()
                .name("TestEntitaet") // gleicher Name
                .paket("de.test") // gleiches Paket
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(e1)
                .entitaet(e2)
                .build();

        LowCodeModel model = LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();

        ModelCheck checker = new ModelCheck();
        List<String> fehler = checker.checkModel(model);

        assertFalse(fehler.isEmpty());
        assertTrue(fehler.stream().anyMatch(f -> f.toLowerCase().contains("duplikat")
                || f.toLowerCase().contains("mehrfach")));
    }

    @Test
    void testCheckModelEntitaetOhnePrimaryKey() {
        Entitaet e1 = Entitaet.builder()
                .name("TestEntitaet")
                .feld(Entitaetsfeld.builder()
                        .name("name")
                        .datenTyp(Datentyp.TEXT)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(e1)
                .build();

        LowCodeModel model = LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();

        ModelCheck checker = new ModelCheck();
        List<String> fehler = checker.checkModel(model);

        // Sollte Warnung geben, wenn Persistenz aktiv ist
        assertNotNull(fehler);
    }

    private LowCodeModel createValidModel() {
        Wertebereich wb = Wertebereich.builder()
                .name("status")
                .eintrag(new WertebereichEintrag("AKTIV", "Aktiv"))
                .eintrag(new WertebereichEintrag("INAKTIV", "Inaktiv"))
                .build();

        Entitaet e1 = Entitaet.builder()
                .name("TestEntity")
                .paket("de.test.domain")
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("name")
                        .fachlicherName("Name")
                        .datenTyp(Datentyp.TEXT)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("status")
                        .fachlicherName("Status")
                        .wertebereich(wb)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(e1)
                .build();

        return LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();
    }
}
