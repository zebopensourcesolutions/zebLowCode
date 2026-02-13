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
import de.zeb.lowcode.model.ui.maskenelemente.MaskeGridItems;
import de.zeb.lowcode.model.ui.maskenelemente.UiModelReact;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für FiJavaMaskeGenerator
 */
@SuppressWarnings("nls")
class FiJavaMaskeGeneratorTest {

    @Test
    void testPrepareErzeugtDateien() {
        LowCodeModel model = createTestModel();
        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        assertNotNull(files);
        assertFalse(files.isEmpty());
        // Pro Maske sollten mehrere Dateien erzeugt werden
        assertTrue(files.size() >= 3);
    }

    @Test
    void testPrepareOhneUiModel() {
        LowCodeModel model = LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(DomainModel.builder().build())
                .build();

        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();
        List<GeneratedFile> files = generator.prepare(model);

        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void testGenerierteFilesHabenInhalt() {
        LowCodeModel model = createTestModel();
        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        for (GeneratedFile file : files) {
            assertNotNull(file.getContent(), "Datei sollte Inhalt haben: " + file.getRelativePath());
            assertFalse(file.getContent().isEmpty(), "Datei sollte nicht leer sein: " + file.getRelativePath());
            assertNotNull(file.getRelativePath(), "Datei sollte Pfad haben");
        }
    }

    @Test
    void testGenerierteFilesHabenKorrektenPackage() {
        LowCodeModel model = createTestModel();
        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        for (GeneratedFile file : files) {
            if (file.getRelativePath().endsWith(".java")) {
                assertTrue(file.getContent().contains("package example.test."),
                        "Java-Datei sollte package Statement haben: " + file.getRelativePath());
            }
        }
    }

    @Test
    void testGenerierteFilesHabenImports() {
        LowCodeModel model = createTestModel();
        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatImports = files.stream()
                .anyMatch(f -> f.getContent().contains("import "));

        assertTrue(hatImports, "Mindestens eine Datei sollte Imports haben");
    }

    @Test
    void testMaskenParameterWirdErzeugt() {
        LowCodeModel model = createTestModel();
        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatMaskenParameter = files.stream()
                .anyMatch(f -> f.getRelativePath().contains("Parameter"));

        assertTrue(hatMaskenParameter, "MaskenParameter sollte erzeugt werden");
    }

    @Test
    void testServiceWirdErzeugt() {
        LowCodeModel model = createTestModel();
        FiJavaMaskeGenerator generator = new FiJavaMaskeGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatService = files.stream()
                .anyMatch(f -> f.getRelativePath().contains("Service"));

        assertTrue(hatService, "Service sollte erzeugt werden");
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
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(entitaet)
                .build();

        MaskeGridItems maske = MaskeGridItems.builder()
                .name("TestMaske")
                .titel("Test")
                .url("/test")
                .entitaet(Entitaet.builder()
                        .name("TestEntity")
                        .paket("de.test")
                        .build())
                .build();

        UiModelReact uiModel = UiModelReact.builder()
                .maske(maske)
                .apiPagesFile("/src/main/typescript/apiPages.ts")
                .build();

        return LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .ui(uiModel)
                .build();
    }
}

