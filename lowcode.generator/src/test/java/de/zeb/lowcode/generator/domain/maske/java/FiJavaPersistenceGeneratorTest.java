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
 * Tests für FiJavaPersistenceGenerator
 */
@SuppressWarnings("nls")
class FiJavaPersistenceGeneratorTest {

    @Test
    void testPrepareErzeugtPersistenzDateien() {
        LowCodeModel model = createTestModel();
        FiJavaPersistenceGenerator generator = new FiJavaPersistenceGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        assertNotNull(files);
        assertFalse(files.isEmpty());
    }

    @Test
    void testPersistenzEntitaetHatJpaAnnotationen() {
        LowCodeModel model = createTestModel();
        FiJavaPersistenceGenerator generator = new FiJavaPersistenceGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatJpaAnnotationen = files.stream()
                .anyMatch(f -> f.getContent().contains("@Entity")
                        || f.getContent().contains("@Table")
                        || f.getContent().contains("@Column"));

        assertTrue(hatJpaAnnotationen, "Persistenz-Entitäten sollten JPA-Annotationen haben");
    }

    @Test
    void testPersistenzEntitaetHatEmbeddedId() {
        LowCodeModel model = createTestModelWithCompositePk();
        FiJavaPersistenceGenerator generator = new FiJavaPersistenceGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatEmbeddedId = files.stream()
                .anyMatch(f -> f.getContent().contains("@EmbeddedId"));

        assertTrue(hatEmbeddedId, "Entität mit mehreren PK-Feldern sollte @EmbeddedId haben");
    }



    @Test
    void testTabellennameMitPrefix() {
        LowCodeModel model = createTestModel();
        FiJavaPersistenceGenerator generator = new FiJavaPersistenceGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatTableAnnotation = files.stream()
                .anyMatch(f -> f.getContent().contains("@Table(name"));

        assertTrue(hatTableAnnotation, "Persistenz-Entitäten sollten @Table Annotation haben");
    }

    @Test
    void testManyToOneBeziehungWirdGeneriert() {
        LowCodeModel model = createTestModelWithRelation();
        FiJavaPersistenceGenerator generator = new FiJavaPersistenceGenerator();

        List<GeneratedFile> files = generator.prepare(model);

        boolean hatManyToOne = files.stream()
                .anyMatch(f -> f.getContent().contains("@ManyToOne")
                        || f.getContent().contains("@JoinColumn"));

        assertTrue(hatManyToOne, "n:1 Beziehungen sollten mit @ManyToOne generiert werden");
    }

    private LowCodeModel createTestModel() {
        Entitaet entitaet = Entitaet.builder()
                .name("TestEntity")
                .paket("de.test")
                .persistenz(true)
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .persistenz(true)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("name")
                        .fachlicherName("Name")
                        .datenTyp(Datentyp.TEXT)
                        .persistenz(true)
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

    private LowCodeModel createTestModelWithCompositePk() {
        Entitaet entitaet = Entitaet.builder()
                .name("TestEntity")
                .paket("de.test")
                .persistenz(true)
                .feld(Entitaetsfeld.builder()
                        .name("id1")
                        .datenTyp(Datentyp.TEXT)
                        .pk(true)
                        .persistenz(true)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("id2")
                        .datenTyp(Datentyp.TEXT)
                        .pk(true)
                        .persistenz(true)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("name")
                        .datenTyp(Datentyp.TEXT)
                        .persistenz(true)
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

    private LowCodeModel createTestModelWithRelation() {


        Entitaet detailEntitaet = Entitaet.builder()
                .name("DetailEntity")
                .paket("de.test")
                .persistenz(true)
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .persistenz(true)
                        .build())
                .build();

        Entitaet hauptEntitaet = Entitaet.builder()
                .name("HauptEntity")
                .paket("de.test")
                .persistenz(true)
                .feld(Entitaetsfeld.builder()
                        .name("id")
                        .datenTyp(Datentyp.ID)
                        .pk(true)
                        .persistenz(true)
                        .build())
                .feld(Entitaetsfeld.builder()
                        .name("detail")
                        .zielEntitaet(Entitaetreferenz.builder()
                                .name("DetailEntity")
                                .paket("de.test")
                                .build())
                        .alsListe(true)
                        .persistenz(true)
                        .build())
                .build();

        DomainModel domain = DomainModel.builder()
                .entitaet(hauptEntitaet)
                .entitaet(detailEntitaet)
                .build();

        return LowCodeModel.builder()
                .anwendungskuerzel("test")
                .domain(domain)
                .build();
    }
}

