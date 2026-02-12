/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED
 * BY LAW. YOU WILL NOT REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION,
 * TRANSFER, DISTRIBUTION OR MODIFICATION OF PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR
 * WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 06.03.2023 - 08:13:36
 */
package de.zeb.lowcode.generator.domain;

import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.LowCodeModel;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public abstract class AbstractGenerator {

    public static final String LINE_SEPARATOR = "\r\n";

    public abstract List<GeneratedFile> prepare(final LowCodeModel modell);

    /**
     * @param imports
     * @return
     */
    public static String javaImportStatementsErzeugen(final Set<JavaImport> imports) {
        Set<String> importLines = new TreeSet<>();
        StringBuilder importSb = new StringBuilder();
        for (JavaImport javaImport : imports) {
            for (String importFrom : javaImport.getImports()) {
                importLines.add("import " + importFrom + (importFrom.endsWith(";") ? "" : ";"));
            }
        }
        // Duplikate entfernen und sortieren
        for (String il : importLines) {
            appendLn(importSb, il);
        }
        return importSb.toString();
    }

    public static void appendLn(final StringBuilder sb, final String string) {
        sb.append(GeneratorUtils.normalizeLineBreaks(string));
        sb.append(AbstractGenerator.LINE_SEPARATOR);
    }

    public void append(final StringBuilder sb, final String string) {
        sb.append(GeneratorUtils.normalizeLineBreaks(string));
    }

    /**
     * @param sb
     */
    protected void addKommentarEditierbar(final StringBuilder sb) {
        appendLn(sb, """
                /**
                 * Initial wurde diese Datei generiert. Solange keine Änderungen stattfinden, wird der Inhalt weiter durch das Modell aktualisiert.
                 * Sobald Änderungen vorgenommen werden, wird diese Datei nicht mehr aktualisiert, sodass die Änderungen erhalten bleiben, potentiell
                 * aber das Modell nicht mehr zum Code passt.
                 */""");
    }

}
