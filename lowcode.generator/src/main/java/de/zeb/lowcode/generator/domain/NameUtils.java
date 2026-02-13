package de.zeb.lowcode.generator.domain;

import org.apache.commons.lang3.StringUtils;

public class NameUtils {

    public static String camelToUnderscore(final String value) {
        String result = value.replace(" ", "_");
        result = result.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        result = result.replace(".", "_");
        return result;
    }

    public static String camelToUnderscoreUpperCase(final String value) {
        return camelToUnderscore(value).toUpperCase();
    }

    public static String prefixErweitern(final String prefix, final String name) {
        String ergebnis = name;
        if (!StringUtils.isEmpty(prefix)) {
            ergebnis = prefix + "_" + ergebnis.replace(prefix, "");
            ergebnis = ergebnis.replace("__", "_");
            if (ergebnis.endsWith("_")) {
                ergebnis = ergebnis.substring(0, ergebnis.length() - 1);
            }
        }
        return ergebnis;
    }

}
