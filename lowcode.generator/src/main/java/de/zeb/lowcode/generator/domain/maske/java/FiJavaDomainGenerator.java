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
import de.zeb.lowcode.model.domain.*;
import de.zeb.lowcode.model.ui.Maske;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public class FiJavaDomainGenerator extends AbstractJavaGenerator {

    public static void generateHashcodeEquals(Maske entitaet, StringBuilder sb, Set<JavaImport> imports, String className) {
        generateHashcodeEquals(entitaet.getParameterFelder(), sb, imports, className);
    }

    public static void generateHashcodeEquals(List<Entitaetsfeld> entitaetsFelder, StringBuilder sb, Set<JavaImport> imports, String className) {
        if (!entitaetsFelder.isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("java.util.Objects")
                    .build());

            String equalsString = entitaetsFelder.stream().map(feld -> "Objects.equals(get%s(), that.get%s())".formatted(feld.getNameCapitalized(), feld.getNameCapitalized())).collect(Collectors.joining(" && " + System.lineSeparator()));
            String hashcodeString = entitaetsFelder.stream().map(feld -> "get%s()".formatted(feld.getNameCapitalized())).collect(Collectors.joining(","));
            //Hashcode und Equals
            appendLn(sb, """
                    @Override
                    public boolean equals(Object o) {
                        if (this == o) {
                            return true;
                        }
                        if (o == null || getClass() != o.getClass()) {
                            return false;
                        }
                        %s that = (%s) o;
                        return %s;
                    }
                    
                    @Override
                    public int hashCode() {
                        return Objects.hash(%s);
                    }
                    """.formatted(className, className, equalsString, hashcodeString));
        }
    }

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {

        List<GeneratedFile> result = new ArrayList<>();

        result.addAll(wertebereichEnumsErzeugen(lcm));
        result.addAll(modellErzeugen(lcm));

        return result;
    }

    private List<GeneratedFile> wertebereichEnumsErzeugen(final LowCodeModel domainModel) {
        List<GeneratedFile> results = new ArrayList<>();
        List<String> verarbeitet = new ArrayList<>();
        if (domainModel.getDomain() != null) {
            for (Entitaet entitaet : domainModel.getDomain()
                    .getEntitaeten()) {
                enumFuerEntitaetRekursivErzeugen(domainModel, results, entitaet, verarbeitet);
            }
            if (domainModel.getUi() != null) {
                for (Maske<?> maske : domainModel.getUi()
                        .getMasken()) {
                    enumFuerEntitaetRekursivErzeugen(domainModel, results, maske.getEntitaet(),
                            verarbeitet);
                }
            }
        }
        return results;

    }

    private void enumFuerEntitaetRekursivErzeugen(final LowCodeModel modell,
                                                  final List<GeneratedFile> results, final Entitaet entitaet,
                                                  final List<String> verarbeitet) {
        for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
            Wertebereich wertebereich = entitaetsfeld.getWertebereich();
            if (wertebereich != null) {
                results.add(wertebereichEnumErzeugen(modell, wertebereich));
            }
            if ((entitaetsfeld.getZielEntitaet() != null)
                    && !verarbeitet.contains(getEntitaetDomainPackage("", modell.getDomain()
                    .getEntitaetByReference(entitaetsfeld.getZielEntitaet())))) {
                verarbeitet.add(getEntitaetDomainPackage("", modell.getDomain()
                        .getEntitaetByReference(entitaetsfeld.getZielEntitaet())));
                enumFuerEntitaetRekursivErzeugen(modell, results, modell.getDomain()
                        .getEntitaetByReference(entitaetsfeld.getZielEntitaet()), verarbeitet);
            }
        }
    }

    private GeneratedFile wertebereichEnumErzeugen(final LowCodeModel modell,
                                                   final Wertebereich wertebereich) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLineWertebereich(modell.getAnwendungskuerzel(),
                wertebereich);
        String packageFolder = "wertebereich/";
        if (!StringUtils.isEmpty(wertebereich.getPaket())) {
            packageFolder = wertebereich.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseGenErzeugen(modell, packageFolder,
                "domain", StringUtils.capitalize(wertebereich.getName()) + "Enum.java");

        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.AllArgsConstructor")
                .from("lombok.Getter")
                .from("java.util.Arrays")
                .from("org.apache.commons.lang3.StringUtils")
                .from("org.slf4j.Logger")
                .from("org.slf4j.LoggerFactory")
                .build());

        appendLn(sb, """
                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @AllArgsConstructor
                @Getter
                @SuppressWarnings( "nls" )
                public enum %sEnum {
                
                """.formatted(StringUtils.capitalize(wertebereich.getName())));

        for (WertebereichEintrag dropdownValue : wertebereich.getEintraege()) {
            append(sb, """                                        
                    %s ("%s", "%s"),""".indent(4)
                    .formatted(dropdownValue.getName()
                            .toUpperCase(), dropdownValue.getLabel(), dropdownValue.getValue()));

        }
        if (!wertebereich.getEintraege().isEmpty()) {
            append(sb, """
                    UNBEKANNT ("Unbekannt", "UNBEKANNT");""");
        }

        appendLn(sb, """
                
                  private final String label;
                  private final String value;
                """);

        String returnValue = StringUtils.capitalize(wertebereich.getName()) + "Enum";

        append(sb, """
                     private final static Logger LOGGER = LoggerFactory.getLogger(REPLACE_RETURN_VALUE.class);
                \s
                     public static REPLACE_RETURN_VALUE findByLabel(String label) {
                         if (StringUtils.isEmpty(label)) {
                                 return null;
                         }
                         return Arrays.stream(REPLACE_RETURN_VALUE.values()).filter(f -> f.getLabel().equals(label.trim())).findFirst()
                                 .orElseGet(() -> {
                                     LOGGER.error("Enum für Wert {} konnte nicht ermittelt werden!", label);
                                     return UNBEKANNT;
                                 });
                     }
                \s
                     public static REPLACE_RETURN_VALUE findByValue(String value) {
                         if (StringUtils.isEmpty(value)) {
                                 return null;
                         }
                         return Arrays.stream(REPLACE_RETURN_VALUE.values()).filter(f -> f.getValue().equalsIgnoreCase(value.trim())).findFirst()
                                 .orElseGet(() -> {
                                     LOGGER.error("Enum für Wert {} konnte nicht ermittelt werden!", value);
                                     return UNBEKANNT;
                                 });
                     }
                    \s
                     public static REPLACE_RETURN_VALUE findByName(String name) {
                         if (StringUtils.isEmpty(name)) {
                                 return null;
                         }
                         return Arrays.stream(REPLACE_RETURN_VALUE.values()).filter(f -> f.name().equalsIgnoreCase(name.trim())).findFirst()
                                 .orElseGet(() -> {
                                     LOGGER.error("Enum für Wert {} konnte nicht ermittelt werden!", name);
                                     return UNBEKANNT;
                                 });
                     }
                 }
                \s""".replaceAll("REPLACE_RETURN_VALUE", returnValue).indent(4));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        return generatedFileBuilder.content(packageLine + importStatements + content)
                .build();
    }

    private List<GeneratedFile> modellErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();

        if (modell.getDomain() != null) {
            for (Entitaet entitaet : modell.getDomain()
                    .getEntitaeten()) {
                if (entitaet.isEigenstaendig()) {
                    modelFuerEntitaetErzeugen(modell, results, entitaet, true);
                }
                modelFuerEntitaetErzeugen(modell, results, entitaet, false);
            }
        }
        return results;
    }

    private void modelFuerEntitaetErzeugen(final LowCodeModel modell,
                                           final List<GeneratedFile> results, final Entitaet entitaet,
                                           boolean reference) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLineDomain(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseGenErzeugen(modell, packageFolder,
                "domain", StringUtils.capitalize(entitaet.getName()) + (reference ? "Reference.java" : ".java"));

        Set<JavaImport> imports = new HashSet<>(variablenDefinitionErzeugen(modell.getAnwendungskuerzel(), entitaet, sb,
                modell.getDomain(), reference));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private Set<JavaImport> variablenDefinitionErzeugen(final String shortApplicationName,
                                                        final Entitaet entitaet, final StringBuilder sb, final DomainModel modell, boolean reference) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.RequiredArgsConstructor")
                .from("java.util.HashMap")
                .from("java.util.Map")
                .from("lombok.experimental.SuperBuilder")
                .from("lombok.extern.jackson.Jacksonized")
                .from("com.fasterxml.jackson.annotation.JsonIgnore")
                .build());
        String erbtVonText = "";
        boolean referenceFelderErlaubt = !reference || !entitaet.isEigenstaendig();
        if (!reference && entitaet.isEigenstaendig()) {
            erbtVonText = "extends " + entitaet.getNameCapitalized() + "Reference";
        } else if (entitaet.getErbtVon() != null) {
            Entitaet zielEntitaet = modell.getEntitaetByReference(entitaet.getErbtVon());
            getModelImport(shortApplicationName, zielEntitaet, imports, entitaet);
            erbtVonText = "extends " + modell.getEntitaetByReference(entitaet.getErbtVon())
                    .getNameCapitalized();
        }

        //Das Domänenmodell ist immer serialisierbar
        String implementiertSerializableText = "implements Serializable";
        imports.add(JavaImport.builder().from("java.io.Serializable").build());

        StringBuilder pks = new StringBuilder();
        for (Entitaetsfeld feld : entitaet.getFelder()) {
            if (feld.isPk()) {
                appendLn(pks, """
                        pk.put("%s", %s());
                        """.formatted(feld.getNameUncapitalized(),
                        GeneratorUtils.getJavaGetterMethodName(feld)));
            }
        }
        appendLn(sb, """
                
                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @Getter
                @Setter
                @ToString
                @RequiredArgsConstructor
                @Jacksonized
                @SuperBuilder
                """);
        List<Entitaet> kinder = GeneratorUtils.werErbtVonDieserEntitaet(entitaet, modell);
        if (!kinder.isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("com.fasterxml.jackson.annotation.JsonTypeInfo")
                    .build());
            appendLn(sb, """
                    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "$type")
                    """);
        }
        if (entitaet.getErbtVon() != null) {
            imports.add(JavaImport.builder()
                    .from("com.fasterxml.jackson.annotation.JsonTypeName")
                    .build());
            appendLn(sb, """
                    @JsonTypeName("%s")
                    """.formatted(entitaet.getNameCapitalized()));
        }
        String className = entitaet.getNameCapitalized();
        if (reference) {
            className += "Reference";
        }
        if (entitaet.isAbstrakt()) {
            appendLn(sb, """
                    public abstract class %s %s %s{
                    """.formatted(className, erbtVonText,
                    implementiertSerializableText));
        } else {
            appendLn(sb, """
                    public class %s %s %s{
                    """.formatted(className, erbtVonText, implementiertSerializableText));

        }
        appendLn(sb, """
                    private static final long serialVersionUID = 1L;
                """);


        String entitaetDomainPackage = getEntitaetDomainPackage(shortApplicationName, entitaet);
        for (Entitaetsfeld feld : entitaet.getFelder()) {
            getWertebereichImport(shortApplicationName, imports, feld.getWertebereich(),
                    entitaetDomainPackage);
            if (feld.getZielEntitaet() != null) {
                Entitaet zielEntitaet = modell.getEntitaetByReference(feld.getZielEntitaet());
                String zielTyp = zielEntitaet.getName();
                if (!feld.isAlsTabelle()) {
                    if (zielEntitaet.isEigenstaendig()) {
                        getModelReferenceImport(shortApplicationName, zielEntitaet, imports);
                        zielTyp += "Reference";
                    } else {
                        getModelImport(shortApplicationName, zielEntitaet, imports, entitaet);
                    }
                }

                if (feld.isAlsTabelle()) {
                    if (referenceFelderErlaubt) {
                        imports.add(JavaImport.builder()
                                .from("java.util.List")
                                .build());
                        append(sb, """
                                private List<%sZeile> %s;""".indent(4)
                                .formatted(feld.getNameCapitalized(), feld.getNameUncapitalized()));
                    }
                } else if (feld.isAlsListe()) {
                    if (referenceFelderErlaubt) {
                        imports.add(JavaImport.builder()
                                .from("java.util.List")
                                .build());

                        append(sb, """
                                private List<%s> %s;""".indent(4)
                                .formatted(zielTyp, feld.getName()));

                    }
                } else {
                    append(sb, """
                            private %s %s;""".indent(4)
                            .formatted(zielTyp, feld.getName()));

                }
            } else {
                String typ = GeneratorUtils.getJavaType(feld, imports, modell, true);
                if (feld.isAlsListe()) {
                    imports.add(JavaImport.builder()
                            .from("java.util.List")
                            .build());
                    append(sb, """
                            private %s %s;""".indent(4)
                            .formatted(typ, feld.getName()));
                } else {
                    if (feld.getAnzahlZeichen() != null) {
                        imports.add(JavaImport.builder()
                                .from("jakarta.validation.constraints.Size")
                                .build());
                        append(sb,
                                """
                                        @Size(max = %s, message = "Das Feld '%s' darf höchstens %s Zeichen aufweisen.")"""
                                        .formatted(feld.getAnzahlZeichen(),
                                                feld.getFachlicherName(), feld.getAnzahlZeichen())
                                        .indent(4));
                    }
                    append(sb, """
                            private %s %s;""".indent(4)
                            .formatted(typ, feld.getName()));
                }
            }
        }

        generateHashcodeEquals(entitaet.getFelderMitVererbung(modell).stream().filter(f -> f.getZielEntitaet() == null).collect(Collectors.toList()), sb, imports, className);


        if (erbtVonText.isEmpty()) {
            List<String> fieldsToReset = List.of("id", "version");

            String elementToCopyRef = "elementToCopy";
            List<Entitaetsfeld> filteredFields = entitaet.getFelder().stream().filter(f -> fieldsToReset.contains(f.getName())).toList();
            if (!filteredFields.isEmpty()) {
                appendLn(sb, """
                        /**
                         * Bereitet ein Element für einen Kopiervorgang vor bei dem die ID sowie die Version auf null gesetzt werden.
                        */
                        public static %s prepareDeepCopy(%s %s) {""".formatted(entitaet.getName(), entitaet.getName(), elementToCopyRef));
                for (Entitaetsfeld feld : entitaet.getFelder()) {
                    if (filteredFields.contains(feld)) {
                        appendLn(sb, "  %s.set%s(null);".formatted(elementToCopyRef, feld.getNameCapitalized()));
                    } else if (feld.getZielEntitaet() != null && feld.isAlsListe()) {
                        buildRecursive(sb, modell, feld, fieldsToReset, elementToCopyRef);
                    }
                }
                appendLn(sb, """
                        return elementToCopy;
                        }
                        """);
            }
        }

        appendLn(sb, """
                    @JsonIgnore
                    public Map<String, Object> getPk() {
                        Map<String, Object> pk = new HashMap<>();
                        %s
                        return pk;
                    }
                }
                """.formatted(pks.toString()));


        return imports;
    }

    private void buildRecursive(StringBuilder sb, DomainModel modell, Entitaetsfeld feld, List<String> fieldsToReset, String elementToCopyRef) {
        Entitaet ziel = modell.getEntitaetByReference(feld.getZielEntitaet());
        List<Entitaetsfeld> nestedFields = ziel.getFelder().stream().filter(f -> fieldsToReset.contains(f.getName())).toList();

        for (Entitaetsfeld field : nestedFields) {
            if (!ziel.isEigenstaendig() && !feld.isAlsTabelle()) {
                appendLn(sb, "  %s.get%s().forEach(%s::prepareDeepCopy);".formatted(elementToCopyRef, feld.getNameCapitalized(), feld.getZielEntitaet().getName()));
            } else if (field.getZielEntitaet() != null) {
                buildRecursive(sb, modell, field, fieldsToReset, field.getZielEntitaet().getName() + "_copy");
            }
        }

    }

}
