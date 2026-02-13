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
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für AbstractGenerator
 */
@SuppressWarnings("nls")
class AbstractGeneratorTest {

    @Test
    void testLineSeperatorIstDefiniert() {
        assertEquals("\r\n", AbstractGenerator.LINE_SEPARATOR);
    }

    @Test
    void testAppendLn() {
        StringBuilder sb = new StringBuilder();
        AbstractGenerator.appendLn(sb, "Test");

        assertEquals("Test\r\n", sb.toString());
    }

    @Test
    void testAppendLnMitMehrerenZeilen() {
        StringBuilder sb = new StringBuilder();
        AbstractGenerator.appendLn(sb, "Zeile 1");
        AbstractGenerator.appendLn(sb, "Zeile 2");
        AbstractGenerator.appendLn(sb, "Zeile 3");

        assertEquals("Zeile 1\r\nZeile 2\r\nZeile 3\r\n", sb.toString());
    }

    @Test
    void testJavaImportStatementsErzeugen() {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("java.util.List")
                .build());
        imports.add(JavaImport.builder()
                .from("java.util.ArrayList")
                .build());

        String result = AbstractGenerator.javaImportStatementsErzeugen(imports);

        assertNotNull(result);
        assertTrue(result.contains("import java.util.List;"));
        assertTrue(result.contains("import java.util.ArrayList;"));
    }

    @Test
    void testJavaImportStatementsErzeugenOhneDuplikate() {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("java.util.List")
                .from("java.util.ArrayList")
                .build());
        imports.add(JavaImport.builder()
                .from("java.util.List") // Duplikat
                .build());

        String result = AbstractGenerator.javaImportStatementsErzeugen(imports);

        assertNotNull(result);
        // Prüfe, dass List nur einmal vorkommt
        int count = result.split("import java.util.List;", -1).length - 1;
        assertEquals(1, count, "List sollte nur einmal importiert werden");
    }

    @Test
    void testJavaImportStatementsErzeugenMitSemikolon() {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("java.util.List;") // bereits mit Semikolon
                .build());

        String result = AbstractGenerator.javaImportStatementsErzeugen(imports);

        assertNotNull(result);
        assertTrue(result.contains("import java.util.List;"));
        assertFalse(result.contains(";;"), "Doppelte Semikolons sollten nicht vorkommen");
    }

    @Test
    void testJavaImportStatementsErzeugenLeer() {
        Set<JavaImport> imports = new HashSet<>();

        String result = AbstractGenerator.javaImportStatementsErzeugen(imports);

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testJavaImportStatementsErzeugenSortierung() {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("java.util.Map")
                .build());
        imports.add(JavaImport.builder()
                .from("java.util.ArrayList")
                .build());
        imports.add(JavaImport.builder()
                .from("java.util.List")
                .build());

        String result = AbstractGenerator.javaImportStatementsErzeugen(imports);

        assertNotNull(result);
        // Prüfe alphabetische Sortierung
        int posArrayList = result.indexOf("ArrayList");
        int posList = result.indexOf("List");
        int posMap = result.indexOf("Map");

        assertTrue(posArrayList > 0);
        assertTrue(posList > posArrayList);
        assertTrue(posMap > posList);
    }
}

