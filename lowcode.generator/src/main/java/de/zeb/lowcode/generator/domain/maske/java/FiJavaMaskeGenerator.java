/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED
 * BY LAW. YOU WILL NOT REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION,
 * TRANSFER, DISTRIBUTION OR MODIFICATION OF PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR
 * WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 06.03.2023 - 07:58:37
 */
package de.zeb.lowcode.generator.domain.maske.java;

import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.ui.MaskenelementMitFeldIf;
import de.zeb.lowcode.model.ui.MaskenelementMitParent;
import de.zeb.lowcode.model.ui.maskenelemente.MaskeGridItems;
import de.zeb.lowcode.model.ui.maskenelemente.UiModelReact;
import de.zeb.lowcode.model.ui.tabelle.Tabelle;
import de.zeb.lowcode.model.ui.tabelle.Tabellenspalte;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public class FiJavaMaskeGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {

        UiModelReact uimodell = (UiModelReact) lcm.getUi();
        List<GeneratedFile> result = new ArrayList<>();
        if (uimodell != null) {
            for (MaskeGridItems fiMaske : uimodell.getMasken()) {
                result.add(
                        maskenParameterErzeugen(lcm.getAnwendungskuerzel(), fiMaske, lcm.getDomain()));
                result.add(uiFeldnamenEnumErzeugen(lcm.getAnwendungskuerzel(), fiMaske));
                result.add(serviceErzeugen(lcm.getAnwendungskuerzel(), fiMaske));
            }
        }

        return result;
    }

    private GeneratedFile maskenParameterErzeugen(final String shortApplicationName,
                                                  final MaskeGridItems fiMaske, final DomainModel domain) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "model");
        String javaName = fiMaske.getNameCapitalized() + "Parameter";
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + shortApplicationName + "/"
                        + fiMaske.getName().toLowerCase() + "/model")
                .file(javaName + ".java");

        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.RequiredArgsConstructor")
                .build());
        imports.add(JavaImport.builder()
                .from("java.io.Serializable")
                .build());

        appendLn(sb, """
                
                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @Getter
                @Setter
                @ToString
                @RequiredArgsConstructor
                public class %s implements Serializable {
                    private static final long serialVersionUID = 1L;
                """.formatted(javaName));

        for (Entitaetsfeld feld : fiMaske.getParameterFelder()) {
            maskenfeldZeileErzeugen(shortApplicationName, sb, imports, feld, domain);
        }
        FiJavaDomainGenerator.generateHashcodeEquals(fiMaske, sb, imports, javaName);

        appendLn(sb, """
                }
                """);

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();

    }

    private GeneratedFile uiFeldnamenEnumErzeugen(final String shortApplicationName,
                                                  final MaskeGridItems fiMaske) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "model");
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + shortApplicationName + "/" + fiMaske.getName().toLowerCase()
                        + "/model/")
                .file(StringUtils.capitalize(fiMaske.getName()) + "FieldEnum.java");
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.AllArgsConstructor")
                .from("example.myframework.api.business.table.FieldIdentifier")
                .from("lombok.Getter")
                .build());

        appendLn(sb, """
                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @AllArgsConstructor
                @Getter
                @SuppressWarnings( "nls" )
                public enum %sFieldEnum implements FieldIdentifier{
                
                   \s""".formatted(StringUtils.capitalize(fiMaske.getName())));

        List<MaskenelementMitParent> maskenelementeRekursiv = fiMaske.getMaskenelementeRekursiv();
        for (MaskenelementMitParent maskenelementMitParent : fiMaske.getMaskenelementeRekursiv()) {
            if (maskenelementMitParent.getChild() instanceof Tabelle t) {
                for (Tabellenspalte tabellenspalte : t.getSpalten()) {
                    maskenelementeRekursiv.add(MaskenelementMitParent.builder()
                            .child(tabellenspalte)
                            .parent(maskenelementMitParent)
                            .build());
                }
            }
        }
        for (Iterator<MaskenelementMitParent> iterator = maskenelementeRekursiv.iterator(); iterator
                .hasNext(); ) {
            MaskenelementMitParent maskenelementMitParent = iterator.next();
            String terminator = ",";
            if (!iterator.hasNext()) {
                terminator = ";";
            }
            String childName = maskenelementMitParent.getChild()
                    .getNameUncapitalized();
            if (sb.indexOf(maskenelementMitParent.getName("_").toUpperCase()) == -1) {
                //doppelte Einträge verhindern
                append(sb, """
                        %s ("%s")%s""".indent(4)
                        .formatted(maskenelementMitParent.getName("_")
                                .toUpperCase(), childName, terminator));
            }
        }

        appendLn(sb, """
                   \s
                
                    String fieldName;
                
                }""");

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();

    }

    private GeneratedFile serviceErzeugen(final String shortApplicationName,
                                          final MaskeGridItems fiMaske) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "service");
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/main/java/example/" + shortApplicationName + "/" + fiMaske.getName().toLowerCase()
                        + "/service")
                .file(StringUtils.capitalize(fiMaske.getName()) + "UiService.java");
        Set<JavaImport> imports = new HashSet<>(serviceMethodenErzeugen(shortApplicationName, fiMaske, sb));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();
    }

    private String getPackageLine(final String shortApplicationName, final MaskeGridItems fiMaske,
                                  final String suffix) {
        return "package " + getPackageName(shortApplicationName, fiMaske, suffix)
                + ";" + LINE_SEPARATOR + LINE_SEPARATOR;
    }

    private Set<JavaImport> serviceMethodenErzeugen(final String shortApplicationName,
                                                    final MaskeGridItems fiMaske, final StringBuilder sb) {
        Set<JavaImport> imports = new HashSet<>();

        imports.add(JavaImport.builder()
                .from("lombok.AllArgsConstructor")
                .from("example.apl.uui.api.UiService")
                .from("java.util.HashMap")
                .from("java.io.Serializable")
                .from("java.util.Map")
                .from("example.apl.uui.api.MethodType")
                .from("example.apl.uui.api.UiModule")
                .from("example.%s.%s.model.%sModel".formatted(shortApplicationName, fiMaske.getNameUncapitalized(), fiMaske.getNameCapitalized()))
                .build());

        String modelklasse = fiMaske.getNameCapitalized() + "Model";
        String parameterTyp = "Serializable";
        if (!fiMaske.getParameterFelder()
                .isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("example.%s.%s.model.%sParameter".formatted(shortApplicationName,
                            fiMaske.getNameUncapitalized(), fiMaske.getNameCapitalized()))
                    .build());
            parameterTyp = fiMaske.getNameCapitalized() + "Parameter";
        }

        // Service Klasse Header
        appendLn(sb,
                """
                        /**
                         * Einmalig generierter Code, Änderungen dürfen vorgenommen werden.
                         * Um diesen Code erneut zu generieren, bitte die Klasse löschen, generieren und dann im Diff die Änderungen prüfen
                         *
                         */
                        @AllArgsConstructor
                        @UiModule("%s")
                        public class %sUiService {
                               \s"""
                        .formatted(fiMaske.getName()
                                .toLowerCase(), fiMaske.getNameCapitalized()));

        // Methode zum Speichern
        appendLn(sb, """
                    @UiService(value = "speichereDaten", method = MethodType.POST)
                    public %s speichereDaten(final %s uiDaten) {
                        //TODO: Daten aus DB laden
                        //TODO: uiDaten in Daten aus DB übernehmen
                        //TODO: Ggfs. Änderungen durch Speichern als uiDaten zurückgeben
                        return uiDaten;
                    }
                """.formatted(modelklasse, modelklasse));

        // Methode zum Laden
        appendLn(sb, """
                    @UiService(value = "ladeDaten", method = MethodType.POST)
                    public %s ladeDaten(final %s parameter) {
                        %s uiDaten = %s.builder().build();
                        //TODO: Daten aus DB laden
                        //TODO: DB Daten auf uiDaten mappen
                        return uiDaten;
                    }
                """.formatted(modelklasse, parameterTyp, modelklasse, modelklasse));

        // Methode zum Validieren
        appendLn(sb, """
                    @UiService(value = "validiereDaten", method = MethodType.POST)
                    public Map<String, String> validiereDaten(final %s uiDaten) {
                        final Map<String, String> validateResultMap = new HashMap<>();
                        if (uiDaten == null) {
                            return validateResultMap;
                        }
                """.formatted(modelklasse));
        for (MaskenelementMitParent maskenelement : fiMaske
                .getMaskenelementeRekursivMitFeldBeiZuEinsRelation()) {
            MaskenelementMitFeldIf fiMaskenelement = (MaskenelementMitFeldIf) maskenelement
                    .getChild();
            if (!fiMaskenelement.getFeld()
                    .isOptional()) {
                imports.add(JavaImport.builder()
                        .from(getPackageName(shortApplicationName, fiMaske, "model")
                                + ".%sFieldEnum".formatted(fiMaske.getNameCapitalized()))
                        .build());
                appendLn(sb, """
                        if (uiDaten.get%s() == null) {
                            validateResultMap.put(%sFieldEnum.%s, "Fehlender Wert im Feld '%s'");
                        }""".indent(8)
                        .formatted(fiMaskenelement.getNameCapitalized(),
                                fiMaske.getNameCapitalized(), maskenelement.getName("_")
                                        .toUpperCase() + ".getFieldName()",
                                fiMaskenelement.getLabel()));
            }

        }
        // Methode zum Validieren
        appendLn(sb, """
                         return validateResultMap;
                    }
                
                """);

        appendLn(sb, """
                }
                """);

        return imports;
    }

    private String getPackageName(final String shortApplicationName, final MaskeGridItems fiMaske,
                                  final String suffix) {
        if (suffix != null) {
            return "example." + shortApplicationName + "." + fiMaske.getName().toLowerCase() + "." + suffix;
        }
        return "example." + shortApplicationName + "." + fiMaske.getName().toLowerCase();
    }

}
