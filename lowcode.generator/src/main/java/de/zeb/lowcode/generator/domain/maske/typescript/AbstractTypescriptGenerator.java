/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED
 * BY LAW. YOU WILL NOT REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION,
 * TRANSFER, DISTRIBUTION OR MODIFICATION OF PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR
 * WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 09.03.2023 - 10:54:17
 */
package de.zeb.lowcode.generator.domain.maske.typescript;

import de.zeb.lowcode.generator.domain.AbstractGenerator;
import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.model.TypescriptImport;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.domain.Wertebereich;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public abstract class AbstractTypescriptGenerator extends AbstractGenerator {

    protected void importErgaenzen(final Collection<TypescriptImport> imports,
                                   final Entitaet entitaet, final TypescriptLocation location, final TypImport tsImport) {
        String pathPrefix = getPathPrefix(location, entitaet.getPaket());
        String importType = getImportType(entitaet, tsImport);

        switch (tsImport) {
            case TYPE_CHECK_REFERENCE:
            case TYPE_DEFAULT_REFERENCE:
            case TYPE_REFERENCE:
            case TYPE_SPEC_REFERENCE:
                imports.add(TypescriptImport.builder()
                        .from("""
                                %s/%sReference.gen""".formatted(pathPrefix, entitaet.getNameUncapitalized()))
                        .type(importType)
                        .build());
                break;
            default:
                imports.add(TypescriptImport.builder()
                        .from("""
                                %s/%s.gen""".formatted(pathPrefix, entitaet.getNameUncapitalized()))
                        .type(importType)
                        .build());

        }
    }

    protected void importWertebereichErgaenzen(final Collection<TypescriptImport> imports,
                                               final Wertebereich wertebereich, final TypescriptLocation location,
                                               final EnumImport tsImport, final boolean multipleValues) {
        String pathPrefix = getPathPrefix(location, "wertebereich");
        String importType = getImportType(wertebereich, tsImport) + (multipleValues ? "Array" : "");
        imports.add(TypescriptImport.builder()
                .from("""
                        %s/%sEnum.gen""".formatted(pathPrefix, wertebereich.getNameUncapitalized()))
                .type(importType)
                .build());
    }

    protected void importTypAusScreenGenErgaenzen(final Collection<TypescriptImport> imports,
                                                  final Entitaet entitaet) {
        importErgaenzen(imports, entitaet, TypescriptLocation.SCREEN_GEN, TypImport.TYPE);
    }

    protected void importTypReferenceAusScreenGenErgaenzen(final Collection<TypescriptImport> imports,
                                                           final Entitaet entitaet) {
        importErgaenzen(imports, entitaet, TypescriptLocation.SCREEN_GEN, TypImport.TYPE_REFERENCE);
    }

    protected void importTypCheckAusScreenGen(final Collection<TypescriptImport> imports,
                                              final Entitaet entitaet) {
        importErgaenzen(imports, entitaet, TypescriptLocation.SCREEN_GEN, TypImport.TYPE_CHECK);
    }

    protected void importTypCheckReferenceAusScreenGen(final Collection<TypescriptImport> imports,
                                                       final Entitaet entitaet) {
        importErgaenzen(imports, entitaet, TypescriptLocation.SCREEN_GEN, TypImport.TYPE_CHECK_REFERENCE);
    }

    protected String typescriptImportStatementsErzeugen(
            final Collection<TypescriptImport> imports) {
        StringBuilder importSb = new StringBuilder();
        Map<String, List<String>> importMap = new HashMap<>();
        Map<String, String> defaultImportMap = new HashMap<>();
        for (TypescriptImport typescriptImport : imports) {
            List<String> importList = importMap.getOrDefault(typescriptImport.getFrom(),
                    new ArrayList<>());
            for (String type : typescriptImport.getTypes()) {
                if (!importList.contains(type)) {
                    importList.add(type);
                }
            }
            importMap.put(typescriptImport.getFrom(), importList);
            if (!StringUtils.isEmpty(typescriptImport.getDefaultType())) {
                defaultImportMap.put(typescriptImport.getFrom(), typescriptImport.getDefaultType());
            }
        }
        for (Entry<String, List<String>> entry : importMap.entrySet()) {
            if (defaultImportMap.containsKey(entry.getKey())) {
                if (entry.getValue()
                        .isEmpty()) {
                    appendLn(importSb, """
                            import %s from '%s';""".formatted(defaultImportMap.get(entry.getKey()),
                            entry.getKey()));
                } else {
                    appendLn(importSb,
                            """
                                    import %s, { %s } from '%s';""".formatted(
                                    defaultImportMap.get(entry.getKey()), entry.getValue()
                                            .stream()
                                            .collect(Collectors.joining(", ")), //$NON-NLS-1$
                                    entry.getKey()));
                }
            } else {
                appendLn(importSb, """
                        import { %s } from '%s';""".formatted(entry.getValue()
                        .stream()
                        .collect(Collectors.joining(", ")), entry.getKey())); //$NON-NLS-1$
            }
        }
        appendLn(importSb, StringUtils.EMPTY);
        return importSb.toString();
    }

    protected String getGeneratedDomainFolder(final Entitaet entitaet) {
        if ((entitaet != null) && (!StringUtils.isEmpty(entitaet.getPaket()))) {
            return "domain/generated/" + entitaet.getPaket()
                    .toLowerCase();
        }
        return "domain/generated";
    }

    protected String getGeneratedDomainWertebereich() {
        return "domain/generated/wertebereich";
    }

    protected void entitaetTypErzeugen(final StringBuilder sb, final Set<TypescriptImport> imports,
                                       final String paket, final Entitaetsfeld entitaetsfeld, final DomainModel domain,
                                       final boolean zielIdentischMitQuelle, boolean nurReferenzFelderGenerieren, boolean useReference) {
        if (entitaetsfeld.getZielEntitaet() != null) {
            Entitaet zielEntitaet = domain
                    .getEntitaetByReference(entitaetsfeld.getZielEntitaet());

            if (!zielIdentischMitQuelle) {
                if (!nurReferenzFelderGenerieren || !(entitaetsfeld.isAlsTabelle() || entitaetsfeld.isAlsListe())) {
                    if (zielEntitaet.isEigenstaendig() && useReference) {
                        importTypReferenceAusScreenGenErgaenzen(imports, zielEntitaet);
                    } else {
                        importTypAusScreenGenErgaenzen(imports, zielEntitaet);
                    }
                }
            }

            String optionalSuffix = "";
            if (entitaetsfeld.isOptional()) {
                optionalSuffix = "?";
            }
            String zielName = entitaetsfeld.getZielEntitaet()
                    .getName();
            if (zielEntitaet.isEigenstaendig() && useReference) {
                zielName += "Reference";
            }
            if (entitaetsfeld.isAlsTabelle()) {
                if (!nurReferenzFelderGenerieren) {
                    imports.add(TypescriptImport.builder()
                            .from("@myorg/cpl-module-reactui")
                            .type("TabellenZeile")
                            .build());
                    appendLn(sb,
                            """
                                    \t%s: %s;""".formatted(entitaetsfeld.getName() + optionalSuffix,
                                    "TabellenZeile<" + zielName + ">[]"));
                }
            } else if (entitaetsfeld.isAlsListe()) {
                if (!nurReferenzFelderGenerieren) {
                    appendLn(sb,
                            """
                                    \t%s: %s;""".formatted(entitaetsfeld.getName() + optionalSuffix,
                                    zielName + "[]"));
                }
            } else {
                appendLn(sb,
                        """
                                \t%s: %s;""".formatted(entitaetsfeld.getName() + optionalSuffix,
                                zielName));
            }

        } else {
            if (entitaetsfeld.getWertebereich() != null) {
                importWertebereichErgaenzen(imports, entitaetsfeld.getWertebereich(),
                        StringUtils.isEmpty(paket) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        EnumImport.TYPE, false);
                String typSuffix = "";
                if (entitaetsfeld.isAlsListe()) {
                    typSuffix = "[]";
                }
                if (entitaetsfeld.isOptional()) {
                    appendLn(sb,
                            """
                                    \t%s?: %s;""".formatted(entitaetsfeld.getName(),
                                    entitaetsfeld.getWertebereich()
                                            .getNameCapitalized() + "Enum" + typSuffix));
                } else {
                    appendLn(sb,
                            """
                                    \t%s: %s;""".formatted(entitaetsfeld.getName(),
                                    entitaetsfeld.getWertebereich()
                                            .getNameCapitalized() + "Enum" + typSuffix));
                }
            } else {
                if (entitaetsfeld.isAlsListe()) {
                    String optionalSuffix = "";
                    if (entitaetsfeld.isOptional()) {
                        optionalSuffix = "?";
                    }
                    appendLn(sb, """
                            \t%s: %s;""".formatted(entitaetsfeld.getName() + optionalSuffix,
                            GeneratorUtils.getTypescriptType(entitaetsfeld) + "[]"));
                } else if (entitaetsfeld.isOptional()) {
                    appendLn(sb, """
                            \t%s?: %s;""".formatted(entitaetsfeld.getName(),
                            GeneratorUtils.getTypescriptType(entitaetsfeld)));
                } else {
                    appendLn(sb, """
                            \t%s: %s;""".formatted(entitaetsfeld.getName(),
                            GeneratorUtils.getTypescriptType(entitaetsfeld)));
                }
            }
        }
    }

    protected void typeGuardFuerEntitaetsfeld(final StringBuilder sb,
                                              final Set<TypescriptImport> imports, final Entitaet entitaet,
                                              final DomainModel domain, boolean referenz, boolean useReference) {
        for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
            if (entitaetsfeld.getZielEntitaet() != null) {
                Entitaet zielEntitaet = domain
                        .getEntitaetByReference(entitaetsfeld.getZielEntitaet());
                boolean zielIdentischMitQuelle = zielEntitaet.getNameCapitalized()
                        .equals(entitaet.getNameCapitalized());

                if (!zielIdentischMitQuelle) {
                    String zielTyp = entitaetsfeld.getZielEntitaet()
                            .getName();

                    if (!referenz || !(entitaetsfeld.isAlsTabelle() || entitaetsfeld.isAlsListe())) {
                        if (zielEntitaet.isEigenstaendig() && useReference) {
                            zielTyp += "Reference";
                            importTypCheckReferenceAusScreenGen(imports, zielEntitaet);
                        } else {
                            importTypCheckAusScreenGen(imports, zielEntitaet);
                        }
                        if (entitaetsfeld.isOptional()) {
                            imports.add(TypescriptImport.builder()
                                    .type("optional")
                                    .from("@myorg/cpl-module-reactui")
                                    .build());
                        }
                    }

                    String pattern = """
                            \t%s: %s,
                            """;
                    if (entitaetsfeld.isOptional()) {
                        pattern = """
                                \t%s: optional(%s),
                                """;
                    }
                    if (entitaetsfeld.isAlsTabelle()) {
                        if (!referenz) {
                            imports.add(TypescriptImport.builder()
                                    .type("isArrayOf")
                                    .from("@myorg/cpl-module-reactui")
                                    .build());

                            imports.add(TypescriptImport.builder()
                                    .type("isTabellenZeile")
                                    .from("@myorg/cpl-module-reactui")
                                    .build());
                            appendLn(sb,
                                    pattern.formatted(entitaetsfeld.getName(),
                                            "isArrayOf(isTabellenZeile(is%s))".formatted(
                                                    zielTyp)));
                        }
                    } else if (entitaetsfeld.isAlsListe()) {
                        if (!referenz) {
                            imports.add(TypescriptImport.builder()
                                    .type("isArrayOf")
                                    .from("@myorg/cpl-module-reactui")
                                    .build());
                            appendLn(sb,
                                    pattern.formatted(entitaetsfeld.getName(), "isArrayOf(is%s)".formatted(
                                            zielTyp)));
                        }
                    } else {
                        appendLn(sb,
                                pattern.formatted(entitaetsfeld.getName(), "is%s".formatted(zielTyp)));
                    }
                } else {
                    if (!referenz || !entitaetsfeld.isAlsListe()) {
                        imports.add(TypescriptImport.builder()
                                .type("isAny")
                                .from("@myorg/cpl-module-reactui")
                                .build());
                        appendLn(sb, """
                                \t%s: isAny,""".formatted(entitaetsfeld.getName()));
                    }
                }
            } else {

                if (entitaetsfeld.getWertebereich() != null) {
                    String type = (entitaetsfeld.isOptional() ? "optional" : "required")
                            + entitaetsfeld.getWertebereich()
                            .getNameCapitalized()
                            + "Enum" + (entitaetsfeld.isAlsListe() ? "Array" : "");
                    importWertebereichErgaenzen(imports, entitaetsfeld.getWertebereich(),
                            TypescriptLocation.SCREEN_GEN,
                            entitaetsfeld.isOptional() ? EnumImport.TYPE_CHECK_OPTIONAL
                                    : EnumImport.TYPE_CHECK_REQUIRED,
                            entitaetsfeld.isAlsListe());
                    appendLn(sb, """
                            \t%s: %s,""".formatted(entitaetsfeld.getName(), type));
                } else {
                    String einbetten = "%s";
                    if (entitaetsfeld.isAlsListe()) {
                        imports.add(TypescriptImport.builder()
                                .type("isArrayOf")
                                .from("@myorg/cpl-module-reactui")
                                .build());
                        einbetten = "isArrayOf(%s)";
                    }
                    String type = (entitaetsfeld.isOptional() ? "optional" : "required")
                            + StringUtils
                            .capitalize(GeneratorUtils.getTypescriptType(entitaetsfeld));
                    imports.add(TypescriptImport.builder()
                            .type(type)
                            .from("@myorg/cpl-module-reactui")
                            .build());
                    appendLn(sb, """
                            \t%s: %s,""".formatted(entitaetsfeld.getName(),
                            einbetten.formatted(type)));
                }
            }
        }
        if (entitaet.getErbtVon() != null || !GeneratorUtils.werErbtVonDieserEntitaet(entitaet, domain)
                .isEmpty()) {
            if (entitaet.isAbstrakt()) {
                //Typunterscheidung ergänzen für korrekte (De-)Serialisierung
                imports.add(TypescriptImport.builder()
                        .type("requiredString")
                        .from("@myorg/cpl-module-reactui")
                        .build());
                sb.append("""
                            $type: requiredString,
                        """);
            } else {
                //Typunterscheidung ergänzen für korrekte (De-)Serialisierung
                imports.add(TypescriptImport.builder()
                        .type("isExactly")
                        .from("@myorg/cpl-module-reactui")
                        .build());
                sb.append("""
                            $type: isExactly('%s'),
                        """.formatted(entitaet.getNameCapitalized()));
            }
        }
    }

    private String getImportType(final Entitaet entitaet, final TypImport tsImport) {
        String importType = entitaet.getNameCapitalized();
        switch (tsImport) {
            case TYPE:
                break;
            case TYPE_REFERENCE:
                importType += "Reference";
                break;
            case TYPE_CHECK:
                importType = "is" + entitaet.getNameCapitalized();
                break;
            case TYPE_CHECK_REFERENCE:
                importType = "is" + entitaet.getNameCapitalized() + "Reference";
                break;
            case TYPE_DEFAULT:
                importType = entitaet.getNameUncapitalized() + "Defaults";
                break;
            case TYPE_DEFAULT_REFERENCE:
                importType = entitaet.getNameUncapitalized() + "ReferenceDefaults";
                break;
            case TYPE_SPEC:
                importType = entitaet.getNameCapitalized() + "Spec";
                break;
            case TYPE_SPEC_REFERENCE:
                importType = entitaet.getNameCapitalized() + "ReferenceSpec";
                break;
            case TYPE_CHECK_OPTIONAL:
                importType = "optional" + entitaet.getNameCapitalized();
                break;
            case TYPE_CHECK_REQUIRED:
                importType = "required" + entitaet.getNameCapitalized();
                break;
            default:
                break;

        }
        return importType;
    }

    private String getImportType(final Wertebereich wb, final EnumImport tsImport) {
        String importType = "";
        switch (tsImport) {
            case TYPE:
                importType = wb.getNameCapitalized() + "Enum";
                break;
            case TYPE_CHECK_OPTIONAL:
                importType = "optional" + wb.getNameCapitalized() + "Enum";
                break;
            case TYPE_CHECK_REQUIRED:
                importType = "required" + wb.getNameCapitalized() + "Enum";
                break;
            case TYPE_REFERENCE:
                importType = wb.getNameCapitalized() + "EnumReference";
                break;
            default:
                break;

        }
        return importType;
    }

    private String getPathPrefix(final TypescriptLocation location, final String paket) {
        String pathPrefix = "";
        String suffix = StringUtils.isEmpty(paket) ? "" : "/" + paket;
        switch (location) {
            case DOMAIN_ROOT:
                pathPrefix = ".";
                break;
            case DOMAIN_WITH_PACKAGE:
                pathPrefix = "..";
                break;
            case SCREEN:
                pathPrefix = "../../domain/generated";
                break;
            case SCREEN_GEN:
                pathPrefix = "../../../domain/generated";
                break;
            default:
                break;

        }
        return pathPrefix + suffix;
    }

    protected static String getDefaultwert(final Entitaetsfeld feld) {
        switch (feld.getDatenTyp()) {
            case BOOLEAN:
                return "true";
            case ZAHL:
            case PROZENTZAHL:
            case GELD_BETRAG:
                return "'0'";
            case GANZZAHL:
            case GANZZAHL_ERWEITERT:
            case BASISPUNKT:
            case VERSION:
                return "0";
            case ZEITSTEMPEL:
            case ZEITPUNKT_LETZTE_AENDERUNG:
            case MODIFIZIERT_VON:
            case ERSTELLT_VON:
            case ZEITPUNKT_ERSTELLUNG:
            case TEXT:
            case ID:
            case URL:
            case DATUM:
            case BINARY:
                return "''";
            case TEXT_JN:
                return "'J'";
            default:

        }
        return "undefined";
    }

}
