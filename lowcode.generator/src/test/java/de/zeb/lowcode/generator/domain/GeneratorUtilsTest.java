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

import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.domain.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für GeneratorUtils
 */
@SuppressWarnings("nls")
class GeneratorUtilsTest {

    @Test
    void testNormalizeLineBreaks() {
        String input = "Zeile 1\nZeile 2\r\nZeile 3\rZeile 4";
        String result = GeneratorUtils.normalizeLineBreaks(input);

        assertNotNull(result);
        assertEquals("Zeile 1\r\nZeile 2\r\nZeile 3\r\nZeile 4", result);
    }

    @Test
    void testNormalizeLineBreaksLeer() {
        String result = GeneratorUtils.normalizeLineBreaks("");

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testNormalizeLineBreaksNull() {
        String result = GeneratorUtils.normalizeLineBreaks(null);

        assertNull(result);
    }

    @Test
    void testGetJavaTypeString() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.TEXT)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("String", result);
    }

    @Test
    void testGetJavaTypeInteger() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.GANZZAHL)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("Integer", result);
    }

    @Test
    void testGetJavaTypeLong() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.GANZZAHL_ERWEITERT)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("Long", result);
    }

    @Test
    void testGetJavaTypeBigDecimal() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.ZAHL)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("BigDecimal", result);
        assertTrue(imports.stream()
                .anyMatch(i -> i.getImports().contains("java.math.BigDecimal")));
    }

    @Test
    void testGetJavaTypeBoolean() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.BOOLEAN)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("Boolean", result);
    }

    @Test
    void testGetJavaTypeDatum() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.DATUM)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("LocalDate", result);
        assertTrue(imports.stream()
                .anyMatch(i -> i.getImports().contains("java.time.LocalDate")));
    }

    @Test
    void testGetJavaTypeDatumUhrzeit() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.ZEITSTEMPEL)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("LocalDateTime", result);
        assertTrue(imports.stream()
                .anyMatch(i -> i.getImports().contains("java.time.LocalDateTime")));
    }

    @Test
    void testGetJavaTypeAlsListe() {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.TEXT)
                .alsListe(true)
                .build();

        String result = GeneratorUtils.getJavaType(feld, imports,
                DomainModel.builder().build(), false);

        assertEquals("List<String>", result);
        assertTrue(imports.stream()
                .anyMatch(i -> i.getImports().contains("java.util.List")));
    }

    @Test
    void testGetJavaGetterMethodNameNormal() {
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.TEXT)
                .build();

        String result = GeneratorUtils.getJavaGetterMethodName(feld);

        assertEquals("getTestFeld", result);
    }

    @Test
    void testGetJavaGetterMethodNameBoolean() {
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("aktiv")
                .datenTyp(Datentyp.BOOLEAN)
                .build();

        String result = GeneratorUtils.getJavaGetterMethodName(feld);

        assertEquals("isAktiv", result);
    }

    @Test
    void testGetTypescriptTypeString() {
        Wertebereich wb = Wertebereich.builder()
                .name("testWb")
                .build();

        String result = GeneratorUtils.getTypescriptType(wb);

        assertEquals("TestWbEnum", result);
    }

    @Test
    void testGetTypescriptTypeFeldString() {
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.TEXT)
                .build();

        String result = GeneratorUtils.getTypescriptType(feld);

        assertEquals("string", result);
    }

    @Test
    void testGetTypescriptTypeFeldBoolean() {
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.BOOLEAN)
                .build();

        String result = GeneratorUtils.getTypescriptType(feld);

        assertEquals("boolean", result);
    }

    @Test
    void testGetTypescriptTypeFeldDatum() {
        Entitaetsfeld feld = Entitaetsfeld.builder()
                .name("testFeld")
                .datenTyp(Datentyp.DATUM)
                .build();

        String result = GeneratorUtils.getTypescriptType(feld);

        assertEquals("string", result);
    }
}

