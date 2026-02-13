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
package de.zeb.lowcode.generator.domain.maske.typescript;

import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.TypescriptImport;
import de.zeb.lowcode.model.domain.*;
import de.zeb.lowcode.model.ui.Maske;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public class FiTypescriptDomainModelGenerator extends AbstractTypescriptGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel modell) {

        List<GeneratedFile> result = new ArrayList<>();
        DomainModel dm = modell.getDomain();

        for (Entitaet entitaet : dm.getEntitaeten()) {
            if (entitaet.getErbtVon() != null) {
                result.addAll(enumsProEntitaetErzeugen(modell.getDomain()
                        .getEntitaetByReference(entitaet.getErbtVon())));
                result.addAll(modellErzeugen(modell.getDomain()
                        .getEntitaetByReference(entitaet.getErbtVon()), modell.getDomain()));
            }
            result.addAll(enumsProEntitaetErzeugen(entitaet));
            result.addAll(modellErzeugen(entitaet, modell.getDomain()));
        }
        if (modell.getUi() != null) {
            for (Maske<?> maske : modell.getUi()
                    .getMasken()) {
                result.addAll(enumsProEntitaetErzeugen(maske.getEntitaet()));
            }
        }

        return result;
    }

    private List<GeneratedFile> modellErzeugen(final Entitaet entitaet, final DomainModel domain) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder(getGeneratedDomainFolder(entitaet))
                .file(StringUtils.uncapitalize(entitaet.getName()) + ".gen.ts");
        Set<TypescriptImport> imports = new HashSet<>();
        imports.addAll(typProEntitaetErzeugen(entitaet, sb, domain, false));
        imports.addAll(typeGuardProEntitaetErzeugen(entitaet, sb, domain, false));
        imports.addAll(defaultProEntitaetErzeugen(entitaet, sb, domain, false));
        String content = sb.toString();
        String importStatements = typescriptImportStatementsErzeugen(imports);
        generatedFiles.add(generatedFileBuilder.content(importStatements + content)
                .build());

        if (entitaet.isEigenstaendig()) {
            sb = new StringBuilder();
            generatedFileBuilder = GeneratedFile.builder()
                    .folder(getGeneratedDomainFolder(entitaet))
                    .file(StringUtils.uncapitalize(entitaet.getName()) + "Reference.gen.ts");
            imports = new HashSet<>();
            imports.addAll(typProEntitaetErzeugen(entitaet, sb, domain, true));
            imports.addAll(typeGuardProEntitaetErzeugen(entitaet, sb, domain, true));
            imports.addAll(defaultProEntitaetErzeugen(entitaet, sb, domain, true));
            content = sb.toString();
            importStatements = typescriptImportStatementsErzeugen(imports);
            generatedFiles.add(generatedFileBuilder.content(importStatements + content)
                    .build());
        }

        return generatedFiles;

    }

    private Set<TypescriptImport> defaultProEntitaetErzeugen(final Entitaet entitaet,
                                                             final StringBuilder sb,
                                                             final DomainModel domain,
                                                             boolean referenz) {
        Set<TypescriptImport> imports = new HashSet<>();

        String zielTyp = entitaet.getNameUncapitalized();
        if (referenz) {
            zielTyp += "Reference";
        }
        appendLn(sb, """
                export const %sDefaults: %s = {""".formatted(zielTyp, StringUtils.capitalize(zielTyp)));

        if (entitaet.getErbtVon() != null) {
            Entitaet zielEntitaet = domain.getEntitaetByReference(entitaet.getErbtVon());
            if (zielEntitaet.isEigenstaendig() && referenz) {
                importErgaenzen(imports, zielEntitaet,
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        TypImport.TYPE_DEFAULT_REFERENCE);
                append(sb, """
                            ...%sReferenceDefaults,
                        """.formatted(zielEntitaet
                        .getNameUncapitalized()));
            } else {
                importErgaenzen(imports, zielEntitaet,
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        TypImport.TYPE_DEFAULT);
                append(sb, """
                            ...%sDefaults,
                        """.formatted(zielEntitaet
                        .getNameUncapitalized()));
            }
        }
        for (Entitaetsfeld feld : entitaet.getFelder()) {
            defaultProFeldErgaenzen(sb, imports, entitaet, feld, domain, referenz);
        }
        if (entitaet.getErbtVon() != null || !GeneratorUtils.werErbtVonDieserEntitaet(entitaet, domain)
                .isEmpty()) {
            sb.append("""
                        $type: '%s',
                    """.formatted(StringUtils.capitalize(zielTyp)));
        }

        appendLn(sb, """
                };""");

        return imports;
    }

    private void defaultProFeldErgaenzen(final StringBuilder sb,
                                         final Set<TypescriptImport> imports,
                                         final Entitaet entitaet,
                                         final Entitaetsfeld feld,
                                         final DomainModel domain,
                                         boolean referenz) {
        if (feld.isAlsListe()) {
            if (feld.getZielEntitaet() == null || !referenz) {
                appendLn(sb, """
                        \t%s: %s,""".formatted(StringUtils.uncapitalize(feld.getName()), "[]"));
            }
        } else if (!feld.isOptional()) {
            if (feld.getZielEntitaet() != null) {
                Entitaet zielEntitaet = domain.getEntitaetByReference(feld.getZielEntitaet());

                if (zielEntitaet.isEigenstaendig()) {
                    importErgaenzen(imports, zielEntitaet,
                            StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                    : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                            TypImport.TYPE_DEFAULT_REFERENCE);
                    appendLn(sb,
                            """
                                    \t%s: %s,""".formatted(StringUtils.uncapitalize(feld.getName()),
                                    StringUtils.uncapitalize(feld.getZielEntitaet()
                                            .getName()) + "ReferenceDefaults"));
                } else {
                    importErgaenzen(imports, zielEntitaet,
                            StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                    : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                            TypImport.TYPE_DEFAULT);
                    appendLn(sb,
                            """
                                    \t%s: %s,""".formatted(StringUtils.uncapitalize(feld.getName()),
                                    StringUtils.uncapitalize(feld.getZielEntitaet()
                                            .getName()) + "Defaults"));
                }
            } else if (feld.getWertebereich() != null) {
                importWertebereichErgaenzen(imports, feld.getWertebereich(),
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        EnumImport.TYPE, false);
                // Bei Enums ersten Eintrag als Defaultwert
                appendLn(sb,
                        """
                                \t%s: %s.%s,""".formatted(StringUtils.uncapitalize(feld.getName()),
                                feld.getWertebereich()
                                        .getNameCapitalized() + "Enum",
                                feld.getWertebereich()
                                        .getEintraege()
                                        .get(0)
                                        .getName()
                                        .toUpperCase()));
            } else {
                String defaultwert = getDefaultwert(feld);
                appendLn(sb, """
                        \t%s: %s,""".formatted(StringUtils.uncapitalize(feld.getName()), defaultwert));
            }
        }
    }

    private List<GeneratedFile> enumsProEntitaetErzeugen(final Entitaet entitaet) {

        List<GeneratedFile> results = new ArrayList<>();

        for (Entitaetsfeld feld : entitaet.getFelder()) {
            enumFeldErgaenzen(results, feld);
        }

        return results;
    }

    private void enumFeldErgaenzen(final List<GeneratedFile> results, final Entitaetsfeld feld) {
        if (feld.getWertebereich() != null) {
            Wertebereich wertebereich = feld.getWertebereich();
            GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                    .folder(getGeneratedDomainWertebereich())
                    .file(StringUtils.uncapitalize(GeneratorUtils.getTypescriptType(wertebereich))
                            + ".gen.ts");

            final StringBuilder sb = new StringBuilder();
            // Enum Typ erzeugen
            appendLn(sb,
                    """
                            /**
                             * Es handelt sich hier um generierten Code, bitte keine Änderungen in der Datei vornehmen
                             */
                            export enum %s {"""
                            .formatted(GeneratorUtils.getTypescriptType(wertebereich)));

            for (WertebereichEintrag dropdownValue : feld.getWertebereich()
                    .getEintraege()) {
                appendLn(sb, """
                        \t%s = '%s',""".formatted(dropdownValue.getName()
                        .toUpperCase(), dropdownValue.getValue()
                        .toUpperCase()));
            }

            appendLn(sb, """
                    }
                    """);

            // Enum TypeGuard erzeugen
            appendLn(sb,
                    """
                            export const required%s = (v: unknown): v is %s => {
                            \treturn Object.keys(%s).some((o) => o === v) || Object.values(%s).some((o) => o === v);
                            };""".formatted(GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich)));

            Set<TypescriptImport> imports = new HashSet<>();
            imports.add(TypescriptImport.builder()
                    .type("isArrayOf")
                    .from("@myorg/cpl-module-reactui")
                    .build());

            // Enum TypeGuard Array erzeugen
            appendLn(sb,
                    """
                            export const required%sArray = (v: unknown): v is %s[] => {
                            \treturn isArrayOf(required%s)(v);
                            };""".formatted(GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich)));

            // Enum TypeGuard erzeugen
            appendLn(sb,
                    """
                            export const optional%s = (v: unknown): v is %s | undefined => {
                            \treturn v === undefined || Object.keys(%s).some((o) => o === v) || Object.values(%s).some((o) => o === v);
                            };""".formatted(GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich),
                            GeneratorUtils.getTypescriptType(wertebereich)));

            String importStatements = typescriptImportStatementsErzeugen(imports);

            results.add(generatedFileBuilder.content(importStatements + sb)
                    .build());
        }
    }

    private Set<TypescriptImport> typProEntitaetErzeugen(final Entitaet entitaet,
                                                         final StringBuilder sb,
                                                         final DomainModel domain,
                                                         boolean referenz) {
        Set<TypescriptImport> imports = new HashSet<>();
        String typName = entitaet.getNameCapitalized();
        if (referenz) {
            typName += "Reference";
        }
        if (entitaet.getErbtVon() != null) {
            Entitaet zielEntitaet = domain.getEntitaetByReference(entitaet.getErbtVon());
            if (referenz) {
                importErgaenzen(imports, domain.getEntitaetByReference(entitaet.getErbtVon()),
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        TypImport.TYPE_REFERENCE);
                appendLn(sb,
                        """
                                export type %s = %sReference & {""".formatted(typName,
                                zielEntitaet.getNameCapitalized()));
            } else {
                importErgaenzen(imports, domain.getEntitaetByReference(entitaet.getErbtVon()),
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        TypImport.TYPE);
                appendLn(sb,
                        """
                                export type %s = %s & {""".formatted(typName,
                                zielEntitaet.getNameCapitalized()));
            }
        } else {
            appendLn(sb, """
                    export type %s = {""".formatted(typName));
        }

        for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
            boolean zielIdentischMitQuelle = false;
            if (entitaetsfeld.getZielEntitaet() != null) {
                zielIdentischMitQuelle = domain
                        .getEntitaetByReference(entitaetsfeld.getZielEntitaet())
                        .getNameCapitalized()
                        .equals(entitaet.getNameCapitalized()) && referenz;
            }
            entitaetTypErzeugen(sb, imports, entitaet.getPaket(), entitaetsfeld, domain,
                    zielIdentischMitQuelle, referenz, true);
        }
        if (!GeneratorUtils.werErbtVonDieserEntitaet(entitaet, domain)
                .isEmpty()) {
            sb.append("""
                        $type: string;
                    """);
        }

        appendLn(sb, """
                };""");
        appendLn(sb, StringUtils.EMPTY);

        return imports;
    }

    private Set<TypescriptImport> typeGuardProEntitaetErzeugen(final Entitaet entitaet,
                                                               final StringBuilder sb,
                                                               final DomainModel domain,
                                                               boolean referenz) {
        Set<TypescriptImport> imports = new HashSet<>();
        String zielTyp = entitaet.getNameCapitalized();
        if (referenz) {
            zielTyp += "Reference";
        }
        appendLn(sb, """
                export const %sSpec: TypeCheck<%s> = {""".formatted(zielTyp,
                zielTyp));

        imports.add(TypescriptImport.builder()
                .type("conformsTo")
                .from("@myorg/cpl-module-reactui")
                .build());
        imports.add(TypescriptImport.builder()
                .type("TypeCheck")
                .from("@myorg/cpl-module-reactui")
                .build());
        if (entitaet.getErbtVon() != null) {
            Entitaet zielEntitaet = domain.getEntitaetByReference(entitaet.getErbtVon());
            if (zielEntitaet.isEigenstaendig() && referenz) {
                importErgaenzen(imports, zielEntitaet,
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        TypImport.TYPE_SPEC_REFERENCE);
                append(sb, """
                            ...%sReferenceSpec,
                        """.formatted(zielEntitaet
                        .getNameCapitalized()));
            } else if (!referenz) {
                importErgaenzen(imports, zielEntitaet,
                        StringUtils.isEmpty(entitaet.getPaket()) ? TypescriptLocation.DOMAIN_ROOT
                                : TypescriptLocation.DOMAIN_WITH_PACKAGE,
                        TypImport.TYPE_SPEC);
                append(sb, """
                            ...%sSpec,
                        """.formatted(zielEntitaet
                        .getNameCapitalized()));
            }
        }
        typeGuardFuerEntitaetsfeld(sb, imports, entitaet, domain, referenz, true);
        appendLn(sb, """
                };
                
                export const is%s  = conformsTo(%sSpec);
                
                """.formatted(zielTyp, zielTyp));

        return imports;
    }

}
