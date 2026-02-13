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
package de.zeb.lowcode.generator.domain.maske.java;

import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für FiJavaDomainGenerator
 */
@SuppressWarnings("nls")
class FiJavaDomainGeneratorTest {

    @Test
    void testPrepareErzeugtEntitaetsDateien() {
        LowCodeModel model = createTestModel();
        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        assertNotNull(files);
        assertFalse(files.isEmpty());
    }

    @Test
    void testPrepareOhneDomain() {
        LowCodeModel model = LowCodeModel.builder()
                .anwendungskuerzel("test")
                .build();

        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();
        List<GeneratedFile> files = generator.prepare(model);

        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void testGenerierteEntitaetHatLombokAnnotationen() {
        LowCodeModel model = createTestModel();
        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatLombok = files.stream()
                .anyMatch(f -> f.getContent().contains("@Data")
                        || f.getContent().contains("@Builder")
                        || f.getContent().contains("@Getter"));

        assertTrue(hatLombok, "Generierte Entitäten sollten Lombok-Annotationen haben");
    }

    @Test
    void testGenerierteEntitaetHatFelder() {
        LowCodeModel model = createTestModel();
        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatFelder = files.stream()
                .anyMatch(f -> f.getContent().contains("private "));

        assertTrue(hatFelder, "Generierte Entitäten sollten Felder haben");
    }

    @Test
    void testGenerierteEntitaetHatKorrektenPackagePfad() {
        LowCodeModel model = createTestModel();
        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        for (GeneratedFile file : files) {
            String relativePath = file.getRelativePath();
            // Sollte den Paketpfad enthalten
            assertTrue(relativePath.contains("example") || relativePath.contains("de/test"),
                    "Pfad sollte Paketstruktur enthalten: " + relativePath);
        }
    }

    @Test
    void testWertebereichWirdAlsEnumGeneriert() {
        Wertebereich wb = Wertebereich.builder()
                .name("status")
                .eintrag(new WertebereichEintrag("AKTIV", "Aktiv"))
                .eintrag(new WertebereichEintrag("INAKTIV", "Inaktiv"))
                .build();

        Entitaet entitaet = Entitaet.builder()
                .name("TestEntity")
                .paket("de.test")
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("status")
                        .wertebereich(wb)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(entitaet)
                .build();

        LowCodeModel model = LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();

        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();
        List<GeneratedFile> files = generator.prepare(model);

        boolean hatEnum = files.stream()
                .anyMatch(f -> f.getContent().contains("enum ")
                        && f.getRelativePath().contains("Enum"));

        assertTrue(hatEnum, "Wertebereich sollte als Enum generiert werden");
    }

    @Test
    void testVererbungWirdKorrektGeneriert() {
        Entitaet basisEntitaet = Entitaet.builder()
                .name("BasisEntity")
                .paket("de.test")
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .build())
                .build();

        Entitaet abgeleiteteEntitaet = Entitaet.builder()
                .name("AbgeleiteteEntity")
                .paket("de.test")
                .erbtVon(Entitaetreferenz.builder()
                        .name("BasisEntity")
                        .paket("de.test")
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("name")
                        .datenTyp(Datentyp.TEXT)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(basisEntitaet)
                .entitaet(abgeleiteteEntitaet)
                .build();

        LowCodeModel model = LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();

        FiJavaDomainGenerator generator = new FiJavaDomainGenerator();
        List<GeneratedFile> files = generator.prepare(model);

        boolean hatExtends = files.stream()
                .anyMatch(f -> f.getContent().contains("extends BasisEntity"));

        assertTrue(hatExtends, "Vererbung sollte mit 'extends' generiert werden");
    }

    private LowCodeModel createTestModel() {
        Entitaet entitaet = Entitaet.builder()
                .name("TestEntity")
                .paket("de.test")
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
                        .name("aktiv")
                        .fachlicherName("Aktiv")
                        .datenTyp(Datentyp.BOOLEAN)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(entitaet)
                .build();

        return LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();
    }
}

