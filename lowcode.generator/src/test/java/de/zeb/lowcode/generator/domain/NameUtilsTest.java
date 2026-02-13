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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für NameUtils
 */
@SuppressWarnings("nls")
class NameUtilsTest {

    @Test
    void testCamelToUnderscore() {
        assertEquals("test_Name", NameUtils.camelToUnderscore("testName"));
    }

    @Test
    void testCamelToUnderscoreEinfach() {
        assertEquals("test", NameUtils.camelToUnderscore("test"));
    }

    @Test
    void testCamelToUnderscoreMehrfachGross() {
        assertEquals("test_Name_Value", NameUtils.camelToUnderscore("testNameValue"));
    }

    @Test
    void testCamelToUnderscoreMitLeerzeichen() {
        assertEquals("test_name", NameUtils.camelToUnderscore("test name"));
    }

    @Test
    void testCamelToUnderscoreMitPunkt() {
        assertEquals("test_name", NameUtils.camelToUnderscore("test.name"));
    }

    @Test
    void testCamelToUnderscoreUpperCase() {
        assertEquals("TEST_NAME", NameUtils.camelToUnderscoreUpperCase("testName"));
    }

    @Test
    void testCamelToUnderscoreUpperCaseEinfach() {
        assertEquals("TEST", NameUtils.camelToUnderscoreUpperCase("test"));
    }

    @Test
    void testCamelToUnderscoreUpperCaseMehrfach() {
        assertEquals("TEST_NAME_VALUE", NameUtils.camelToUnderscoreUpperCase("testNameValue"));
    }

    @Test
    void testPrefixErweiternOhnePrefix() {
        assertEquals("TEST_NAME", NameUtils.prefixErweitern("", "TEST_NAME"));
    }

    @Test
    void testPrefixErweiternNullPrefix() {
        assertEquals("TEST_NAME", NameUtils.prefixErweitern(null, "TEST_NAME"));
    }

    @Test
    void testPrefixErweiternMitPrefix() {
        assertEquals("APP_TEST_NAME", NameUtils.prefixErweitern("APP", "TEST_NAME"));
    }

    @Test
    void testPrefixErweiternPrefixBereitsVorhanden() {
        assertEquals("APP_TEST_NAME", NameUtils.prefixErweitern("APP", "APP_TEST_NAME"));
    }

    @Test
    void testPrefixErweiternDoppelteUnterstriche() {
        assertEquals("APP_TEST", NameUtils.prefixErweitern("APP", "_TEST"));
    }

    @Test
    void testPrefixErweiternEndetMitUnderscore() {
        String result = NameUtils.prefixErweitern("APP", "");
        assertFalse(result.endsWith("_"), "Ergebnis sollte nicht mit Underscore enden");
    }

    @Test
    void testCamelToUnderscoreMitZahlen() {
        assertEquals("test123_Name", NameUtils.camelToUnderscore("test123Name"));
    }

    @Test
    void testCamelToUnderscoreBereitsUnderscore() {
        assertEquals("test_name", NameUtils.camelToUnderscore("test_name"));
    }

    @Test
    void testPrefixErweiternKomplexesBeispiel() {
        assertEquals("ZEB_NACHTVERARBEITUNG_PAR",
                NameUtils.prefixErweitern("ZEB", "NACHTVERARBEITUNG_PAR"));
    }

    @Test
    void testPrefixErweiternMitTeilweisemPrefix() {
        String result = NameUtils.prefixErweitern("APP", "APPTEST");
        assertEquals("APP_TEST", result);
    }
}

