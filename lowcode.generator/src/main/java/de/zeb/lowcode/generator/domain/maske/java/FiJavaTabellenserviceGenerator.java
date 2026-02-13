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

import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.ui.Maske;
import de.zeb.lowcode.model.ui.MaskenelementMitParent;
import de.zeb.lowcode.model.ui.maskenelemente.MaskeGridItems;
import de.zeb.lowcode.model.ui.maskenelemente.UiModelReact;
import de.zeb.lowcode.model.ui.tabelle.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public class FiJavaTabellenserviceGenerator extends AbstractJavaGenerator {

    public static String getSpaltenname(final Tabellenspalte tabellenspalte) {
        if (!StringUtils.isEmpty(tabellenspalte.getLabel())) {
            return tabellenspalte.getLabel();
        }
        if (tabellenspalte instanceof AbstractTabellenspalte sp &&
                !StringUtils.isEmpty(sp.getFeld().getFachlicherName())) {
            return sp.getFeld().getFachlicherName();
        }
        throw new IllegalStateException("Für " + tabellenspalte + " fehlt ein Label");
    }

    protected static String getDefaultwert(final Entitaetsfeld feld) {
        return switch (feld.getDatenTyp()) {
            case ID -> "''";
            case BOOLEAN, TEXT_JN -> "true";
            case GELD_BETRAG, BASISPUNKT, GANZZAHL_ERWEITERT, PROZENTZAHL, GANZZAHL, ZAHL -> "0";
            case URL, TEXT -> "\"\"";
            case DATUM -> "new Date()";
            case ZEITSTEMPEL -> "new LocalDateTime()";
            case BINARY -> "null";
            default -> null;
        };
    }

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {

        UiModelReact uimodell = (UiModelReact) lcm.getUi();
        List<GeneratedFile> result = new ArrayList<>();

        if (uimodell != null) {
            for (MaskeGridItems fiMaske : uimodell.getMasken()) {
                List<Tabelle> tabellen = new ArrayList<>();
                for (MaskenelementMitParent maskenelementMitParent : Maske
                        .rekursivMaskenelementeErmitteln(null, fiMaske.getElemente())) {
                    if (maskenelementMitParent.getChild() instanceof Tabelle) {
                        tabellen.add((Tabelle) maskenelementMitParent.getChild());
                    }
                }

                for (Tabelle tabelle : tabellen) {
                    if (!tabelle.getTabellendefinitionParameterFelder()
                            .isEmpty()) {
                        result.add(tabledefinitionParameterErzeugen(lcm.getAnwendungskuerzel(), fiMaske,
                                tabelle, lcm.getDomain()));
                    }
                    if (!tabelle.getParameterFelder()
                            .isEmpty()) {
                        result.add(additionalStateErzeugen(lcm.getAnwendungskuerzel(), fiMaske, tabelle,
                                lcm.getDomain()));
                    }
                    result.add(updateRowPayloadErzeugen(lcm.getAnwendungskuerzel(), fiMaske, tabelle,
                            lcm.getDomain()));
                    result.add(tabellenServiceErzeugen(lcm.getAnwendungskuerzel(), fiMaske, tabelle,
                            lcm.getDomain()));
                }
            }
        }
        return result;
    }

    private GeneratedFile updateRowPayloadErzeugen(final String shortApplicationName,
                                                   final MaskeGridItems fiMaske, final Tabelle tabelle, final DomainModel domain) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "model");
        String javaName = tabelle.getFeld()
                .getNameCapitalized() + "UpdateRowPayload";
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + shortApplicationName + "/"
                        + fiMaske.getNameUncapitalized().toLowerCase() + "/model")
                .file(javaName + ".java");

        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.RequiredArgsConstructor")
                .from("example.myframework.api.business.table.UpdateRowsPayload")
                .from("java.util.List")
                .from("java.io.Serializable")
                .build());

        if (GeneratorUtils.entityForMaskIsPartOfDomain(fiMaske, domain)) {
            imports.add(JavaImport.builder()
                    .from(getEntitaetDomainPackage(shortApplicationName, fiMaske.getEntitaet())
                            + "." + tabelle.getFeld()
                            .getNameCapitalized()
                            + "Zeile")
                    .build());
        }

        // import benötigen wir hier nicht, liegt im gleichen Package
        String additionalStateTyp = getAdditionalStateType(shortApplicationName, fiMaske, tabelle,
                new HashSet<>());

        appendLn(sb, """
                
                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @Getter
                @Setter
                @ToString
                @RequiredArgsConstructor
                public class %s implements UpdateRowsPayload<%sZeile, %s>, Serializable {
                    private static final long serialVersionUID = 1L;
                    private List<%sZeile> allRows;
                    private List<%sZeile> selectedRows;
                    private %s            additionalState;
                
                }
                """.formatted(javaName, tabelle.getFeld()
                        .getNameCapitalized(), additionalStateTyp,
                tabelle.getFeld()
                        .getNameCapitalized(),
                tabelle.getFeld()
                        .getNameCapitalized(),
                additionalStateTyp));
        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();

    }

    private GeneratedFile additionalStateErzeugen(final String shortApplicationName,
                                                  final MaskeGridItems fiMaske, final Tabelle tabelle, final DomainModel domain) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "model");
        String javaName = tabelle.getNameCapitalized() + "TabelleAdditionalState";
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + shortApplicationName + "/"
                        + fiMaske.getNameUncapitalized().toLowerCase() + "/model")
                .file(javaName + ".java");
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.RequiredArgsConstructor")
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

        for (Entitaetsfeld feld : tabelle.getParameterFelder()) {
            maskenfeldZeileErzeugen(shortApplicationName, sb, imports, feld, domain);
        }

        FiJavaDomainGenerator.generateHashcodeEquals(tabelle.getParameterFelder(), sb, imports, javaName);

        appendLn(sb, """
                }
                """);

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();

    }

    private GeneratedFile tabledefinitionParameterErzeugen(final String shortApplicationName,
                                                           final MaskeGridItems fiMaske, final Tabelle tabelle, final DomainModel domain) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "model");
        String javaName = tabelle.getNameCapitalized() + "TabellendefinitionParameter";
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + shortApplicationName + "/"
                        + fiMaske.getNameUncapitalized().toLowerCase() + "/model")
                .file(javaName + ".java");
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.RequiredArgsConstructor")
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

        for (Entitaetsfeld feld : tabelle.getTabellendefinitionParameterFelder()) {
            maskenfeldZeileErzeugen(shortApplicationName, sb, imports, feld, domain);
        }

        appendLn(sb, """
                }
                """);

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();

    }

    private GeneratedFile tabellenServiceErzeugen(final String shortApplicationName,
                                                  final MaskeGridItems fiMaske, final Tabelle tabelle, final DomainModel domain) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(shortApplicationName, fiMaske, "service");
        String javaName = tabelle.getFeld()
                .getNameCapitalized();
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/main/java/example/" + shortApplicationName + "/"
                        + fiMaske.getNameUncapitalized().toLowerCase() + "/service")
                .file(javaName + "TabellenService.java");
        Set<JavaImport> imports = new HashSet<>(tabelleServiceMethodenErzeugen(shortApplicationName, fiMaske, tabelle, sb, domain));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();

    }

    private Set<JavaImport> tabelleServiceMethodenErzeugen(final String shortApplicationName,
                                                           final MaskeGridItems fiMaske, final Tabelle tabelle, final StringBuilder sb,
                                                           final DomainModel domain) {
        Set<JavaImport> imports = new HashSet<>();
        Entitaetsfeld feld = tabelle.getFeld();

        String feldname = tabelle.getFeld()
                .getNameCapitalized();
        Entitaet zielEntitaet = domain.getEntitaetByReference(feld.getZielEntitaet());
        String zeilenImport = "example.%s.%s.model.%sZeile".formatted(shortApplicationName, fiMaske.getNameUncapitalized().toLowerCase(), feldname);
        if (GeneratorUtils.entityIsPartOfDomain(feld.getZielEntitaet(), domain)) {
            zeilenImport = "example.%s.%s.domain.%sZeile".formatted(shortApplicationName, feld.getZielEntitaet().getPaket().toLowerCase(), feldname);
            imports.add(JavaImport.builder()
                    .from(getEntitaetDomainPackage(shortApplicationName, zielEntitaet) + "."
                            + zielEntitaet.getNameCapitalized())
                    .build());
        }

        imports.add(JavaImport.builder()
                .from("lombok.AllArgsConstructor")
                .from("example.apl.uui.api.UiService")
                .from("example.myframework.api.business.table.TableValidationResultBuilder")
                .from("java.util.List")
                .from("java.util.Set")
                .from("java.util.Map.Entry")
                .from("java.util.HashSet")
                .from("java.io.Serializable")
                .from("example.apl.uui.api.MethodType")
                .from("example.apl.uui.api.UiModule")
                .from("example.myframework.api.business.table.ITableFactoryService")
                .from("example.myframework.api.business.table.TableDefinition")
                .from("example.myframework.api.business.table.TableOperationResult")
                .from("example.myframework.api.business.table.TableOperationResultBuilder")
                .from("example.myframework.api.business.table.TableValidationResult")
                .from("example.myframework.api.business.table.FieldIdentifier")
                .from("example.myframework.api.business.table.TableBuilder")
                .from("example.myframework.api.ui.table.ITableUiService")
                .from(zeilenImport)
                .from("example.%s.%s.model.%sUpdateRowPayload".formatted(shortApplicationName, fiMaske.getNameUncapitalized().toLowerCase(), feldname))
                .build());

        getMapperImport(shortApplicationName, domain.getEntitaetByReference(feld.getZielEntitaet()), imports, fiMaske.getEntitaet());


        String tabellendefinitionParameterTyp = "Serializable";
        if (!tabelle.getTabellendefinitionParameterFelder()
                .isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("example.%s.%s.model.%sTabellendefinitionParameter"
                            .formatted(shortApplicationName, fiMaske.getNameUncapitalized(),
                                    tabelle.getFeld()
                                            .getNameCapitalized()))
                    .build());
            tabellendefinitionParameterTyp = feldname + "TabellendefinitionParameter";
        }
        String additionalStateTyp = getAdditionalStateType(shortApplicationName, fiMaske, tabelle,
                imports);

        String modellname = domain.getEntitaetByReference(feld.getZielEntitaet())
                .getNameCapitalized();
        // Service Klasse Header
        appendLn(sb,
                """
                        /**
                         * Einmalig generierter Code, Änderungen dürfen vorgenommen werden.
                         * Um diesen Code erneut zu generieren, bitte die Klasse löschen, generieren und dann im Diff die Änderungen prüfen
                         *
                         */
                        @AllArgsConstructor
                        @UiModule("%s_%s_TabellenService")
                        public class %sTabellenService implements ITableUiService<%s, %s, %s, %sZeile, %sUpdateRowPayload> {
                        
                            private final ITableFactoryService     tableFactory;
                               \s"""
                        .formatted(fiMaske.getName()
                                        .toLowerCase(), feld.getNameUncapitalized(), feldname, modellname,
                                tabellendefinitionParameterTyp, additionalStateTyp, feldname,
                                feld.getNameCapitalized()));

        // Methode zum Speichern
        appendLn(sb,
                """
                            @Override
                            @UiService(value = "getTabelleDefinition", method = MethodType.POST)
                            public TableDefinition getTabelleDefinition(final %s parameter) {
                                //FIXME Selektionsvariante aus Modell übernehmen
                                final TableBuilder<FieldIdentifier> builder = this.tableFactory.createMultiSelection();
                        
                                %s
                        
                                return builder.toDefinition();
                            }
                        
                        """
                        .formatted(tabellendefinitionParameterTyp, getTabellendefinitionInhalt(
                                shortApplicationName, fiMaske, tabelle, imports)));

        StringBuilder defaultWerte = new StringBuilder();
        for (Entitaetsfeld entitaetsfeld : domain.getEntitaetByReference(feld.getZielEntitaet())
                .getFelder()) {
            if (!entitaetsfeld.isOptional()) {
                appendLn(defaultWerte, "." + entitaetsfeld.getNameUncapitalized() + "("
                        + getDefaultwert(entitaetsfeld) + ")");
            }
        }
        // Methode zum Laden
        appendLn(sb, """
                    @Override
                    @UiService(value = "onAddRow", method = MethodType.POST)
                    public TableOperationResult<%s> onAddRow(
                            final %sUpdateRowPayload updatePayload) {
                        TableOperationResultBuilder<%s> builder = this.tableFactory
                                .createOperationResultBuilder();
                
                        builder.addNewRow(%sZeile.builder()
                                .fachobjekt(%s.builder()
                                %s
                                .build())
                            .build());
                
                        return builder.toResult();
                    }
                """.formatted(modellname, feldname, modellname, feld.getNameCapitalized(), zielEntitaet.getNameCapitalized(),
                defaultWerte));

        // Methode zum Laden
        appendLn(sb, """
                    @Override
                    @UiService(value = "onDeleteRow", method = MethodType.POST)
                    public TableOperationResult<%s> onDeleteRow(
                            final %sUpdateRowPayload updatePayload) {
                        TableOperationResultBuilder<%s> builder = this.tableFactory
                                .createOperationResultBuilder();
                
                        if ((updatePayload != null) && (updatePayload.getSelectedRows() != null)) {
                            updatePayload.getSelectedRows()
                                    .stream()
                                    .map(r -> r.getId())
                                    .forEach(builder::addDeleteRow);
                        }
                
                        return builder.toResult();
                    }
                """.formatted(modellname, feldname, modellname));

        // Methode zum Validieren
        appendLn(sb, """
                    @Override
                    @UiService(value = "onEditRow", method = MethodType.POST)
                    public TableOperationResult<%s> onEditRow(
                            final %sUpdateRowPayload updatePayload) {
                        TableOperationResultBuilder<%s> builder = this.tableFactory
                                .createOperationResultBuilder();
                
                        updatePayload.getSelectedRows()
                                .forEach(builder::addEditRow);
                
                        return builder.toResult();
                    }
                """.formatted(modellname, feldname, modellname));

        // Methode zum Validieren
        appendLn(sb,
                """
                            @Override
                            @UiService(value = "validateRows", method = MethodType.POST)
                            public List<TableValidationResult> validateRows(
                                    final %sUpdateRowPayload validationPayload) {
                                TableValidationResultBuilder builder = this.tableFactory.createValidationResultBuilder();
                                primaerschlusselValidieren(validationPayload, builder);
                                return builder.toResult();
                            }
                        """
                        .formatted(feldname));

        // Methode zum Validieren
        appendLn(sb,
                """
                            private void primaerschlusselValidieren(final %sUpdateRowPayload validationPayload,
                                final TableValidationResultBuilder builder) {
                                Set<String> keys = new HashSet<>();
                                for (%sZeile tabelleZeile : validationPayload.getMergedRows()) {
                                    StringBuilder key = new StringBuilder();
                                    for (Entry<String, Object> pk : tabelleZeile.getFachobjekt()
                                            .getPk()
                                            .entrySet()) {
                                        if ((pk.getValue() == null) || ((pk.getValue() instanceof String)
                                                && ((String) pk.getValue()).isEmpty())) {
                                            builder.appendError(tabelleZeile.getId(), () -> pk.getKey(),
                                                    MessageFactory.create("Fehlender Wert"));
                                        }
                                        key.append(pk.getKey() + ": " + pk.getValue());
                                    }
                                    String keyFertig = key.toString();
                                    if (keys.contains(keyFertig)) {
                                        for (Entry<String, Object> pk : tabelleZeile.getFachobjekt()
                                                .getPk()
                                                .entrySet()) {
                                            builder.appendError(tabelleZeile.getId(), () -> pk.getKey(), MessageFactory
                                                    .create("Mehrere Zeilen haben identische Schlüsselwerte"));
                                        }
                                    } else {
                                        keys.add(keyFertig);
                                    }
                                }
                            }
                        """
                        .formatted(feldname, feldname));

        appendLn(sb, """
                }
                """);

        return imports;
    }

    private String getAdditionalStateType(final String shortApplicationName,
                                          final MaskeGridItems fiMaske, final Tabelle tabelle, final Set<JavaImport> imports) {
        String additionalStateTyp = "Serializable";
        imports.add(JavaImport.builder()
                .from("java.io.Serializable")
                .build());
        if (!tabelle.getParameterFelder()
                .isEmpty()) {
            String feldname = tabelle.getFeld()
                    .getNameCapitalized();
            imports.add(JavaImport.builder()
                    .from("example.%s.%s.model.%sTabelleAdditionalState".formatted(
                            shortApplicationName, fiMaske.getNameUncapitalized().toLowerCase(), feldname))
                    .build());
            additionalStateTyp = feldname + "TabelleAdditionalState";
        }
        return additionalStateTyp;
    }

    private String getPackageLine(final String shortApplicationName, final MaskeGridItems fiMaske,
                                  final String suffix) {
        return "package " + getPackageName(shortApplicationName, fiMaske, suffix)
                + ";" + LINE_SEPARATOR + LINE_SEPARATOR;
    }

    private String getPackageName(final String shortApplicationName, final MaskeGridItems fiMaske,
                                  final String suffix) {
        if (suffix != null) {
            return "example." + shortApplicationName + "." + fiMaske.getNameUncapitalized().toLowerCase() + "."
                    + suffix;
        }
        return "example." + shortApplicationName + "." + fiMaske.getNameUncapitalized().toLowerCase();
    }

    private Map<String, MaskenelementMitParent> getMaskenelementeFuerSpalten(
            final MaskeGridItems fiMaske, final Tabelle tabelle) {
        Map<String, MaskenelementMitParent> ergebnis = new HashMap<>();
        for (MaskenelementMitParent maskenelementMitParent : fiMaske.getMaskenelementeRekursiv()) {
            if (maskenelementMitParent.getChild() instanceof Tabelle t &&
                    t.getName().equals(tabelle.getName())) {
                for (Tabellenspalte tabellenspalte : t.getSpalten()) {
                    ergebnis.put(tabellenspalte.getName(), MaskenelementMitParent.builder()
                            .child(tabellenspalte)
                            .parent(maskenelementMitParent)
                            .build());
                }
            }
        }
        return ergebnis;
    }

    private String getTabellendefinitionInhalt(final String shortApplicationName,
                                               final MaskeGridItems fiMaske, final Tabelle tabelle, final Set<JavaImport> imports) {
        Map<String, MaskenelementMitParent> maskenelementeFuerSpalten = getMaskenelementeFuerSpalten(
                fiMaske, tabelle);
        StringBuilder sb = new StringBuilder();
        for (Tabellenspalte tabellenspalte : tabelle.getSpalten()) {
            imports.add(JavaImport.builder()
                    .from("example.myframework.api.domain.messages.MessageFactory")
                    .build());
            getFieldnameImport(shortApplicationName, fiMaske, imports, fiMaske.getEntitaet());
            StringBuilder ca = new StringBuilder();
            if (tabellenspalte instanceof AbstractTabellenspalte sp) {
                appendLn(ca, ".editable(%s)".formatted(sp.isEditable() ? "true" : "false"));
                appendLn(ca, ".visible(%s)".formatted(sp.isVisible() ? "true" : "false"));
                appendLn(ca, ".sortable(%s)".formatted(sp.isSortable() ? "true" : "false"));
                if (sp.getMinWidth() != null) {
                    appendLn(ca, ".minWidth(%s)".formatted(sp.getMinWidth()));
                }
                if (sp.getMaxWidth() != null) {
                    appendLn(ca, ".maxWidth(%s)".formatted(sp.getMaxWidth()));
                }
                String feldEnumName = maskenelementeFuerSpalten.get(tabellenspalte.getName())
                        .getName("_")
                        .toUpperCase();
                String konstruktor = """
                        (
                            MessageFactory.create("%s")
                            , %sFieldEnum.%s
                            )
                        """.formatted(getSpaltenname(tabellenspalte), fiMaske.getNameCapitalized(),
                        feldEnumName);

                String builderAufruf = "";

                switch (tabellenspalte) {
                    case TextTabellenspalte spalte -> {
                        builderAufruf = "appendTextColumn";
                        if (spalte.isMultiline()) {
                            appendLn(ca, ".multiline(%s)"
                                    .formatted("true"));
                        }
                    }
                    case BooleanTabellenspalte spalte -> {
                        builderAufruf = "appendBooleanColumn";
                        if (spalte.isAlsCheckbox()) {
                            appendLn(ca, ".alsCheckbox(%s)"
                                    .formatted("true"));
                        }
                    }
                    case DatumTabellenspalte datumTabellenspalte -> builderAufruf = "appendDateColumn";
                    case DatumUhrzeitTabellenspalte datumUhrzeitTabellenspalte ->
                            builderAufruf = "appendDateTimeColumn";
                    case WertebereichTabellenspalte wertebereichTabellenspalte ->
                            builderAufruf = "appendWertebereichColumn";
                    case ZahlTabellenspalte spalte -> {
                        builderAufruf = "appendNumberColumn";
                        if (spalte.getFormat() != null) {
                            appendLn(ca, ".format(NumberFormat.%s)".formatted(spalte.getFormat()
                                    .name()));
                        }
                    }
                    default -> {
                    }
                }

                appendLn(sb, """
                        builder.%s %s
                        %s;
                        """.formatted(builderAufruf, konstruktor, ca.toString()));

            }

        }
        return sb.toString();
    }
}
