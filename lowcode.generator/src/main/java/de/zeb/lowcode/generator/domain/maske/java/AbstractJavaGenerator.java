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
package de.zeb.lowcode.generator.domain.maske.java;

import de.zeb.lowcode.generator.domain.AbstractGenerator;
import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.domain.Wertebereich;
import de.zeb.lowcode.model.ui.Maske;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.Set;

@SuppressWarnings("nls")
public abstract class AbstractJavaGenerator extends AbstractGenerator {

    protected final void maskenfeldZeileErzeugen(final String shortApplicationName,
                                                 final StringBuilder sb, final Set<JavaImport> imports, final Entitaetsfeld feld,
                                                 final DomainModel domain) {
        if (feld.getWertebereich() != null) {
            imports.add(JavaImport.builder()
                    .from(getWertebereichPackage(shortApplicationName, feld.getWertebereich()) + "."
                            + feld.getWertebereich()
                            .getNameCapitalized()
                            + "Enum")
                    .build());
        }
        if (feld.isPk()) {
            imports.add(JavaImport.builder()
                    .from("lombok.NonNull")
                    .build());
            append(sb, """
                    @NonNull
                    """.indent(4));
        }
        if (feld.getZielEntitaet() != null) {
            if (feld.isAlsTabelle()) {
                imports.add(JavaImport.builder()
                        .from("java.util.List")
                        .build());
                append(sb, """
                        private List<%sZeile> %s;""".indent(4)
                        .formatted(feld.getNameCapitalized(), feld.getName()));
            } else if (feld.isAlsListe()) {
                imports.add(JavaImport.builder()
                        .from("java.util.List")
                        .build());
                append(sb, """
                        private List<%s> %s;""".indent(4)
                        .formatted(feld.getZielEntitaet()
                                .getName(), feld.getName()));
            } else {
                append(sb, """
                        private %s %s;""".indent(4)
                        .formatted(feld.getZielEntitaet()
                                .getName(), feld.getName()));
            }
        } else {
            String typ = GeneratorUtils.getJavaType(feld, imports, domain, true);
            if (feld.getWertebereich() != null) {
                typ = feld.getWertebereich()
                        .getNameCapitalized() + "Enum";
            }
            if (feld.isAlsListe()) {
                imports.add(JavaImport.builder()
                        .from("java.util.List")
                        .build());
                append(sb, """
                        private List<%s> %s;""".indent(4)
                        .formatted(typ, feld.getName()));
            }
            append(sb, """
                    private %s %s;""".indent(4)
                    .formatted(typ, feld.getName()));
        }
    }

    protected final String getPackageLineDomain(final String shortApplicationName,
                                                final Entitaet entitaet) {
        return "package " + getEntitaetDomainPackage(shortApplicationName, entitaet) + ";"
                + AbstractGenerator.LINE_SEPARATOR + AbstractGenerator.LINE_SEPARATOR;
    }

    protected final String getPackageLinePersistence(final String shortApplicationName,
                                                     final Entitaet entitaet) {
        return "package " + getPersistenceDomainPackage(shortApplicationName, entitaet) + ";"
                + AbstractGenerator.LINE_SEPARATOR + AbstractGenerator.LINE_SEPARATOR;
    }

    protected final String getPackageLineRepository(final String shortApplicationName,
                                                    final Entitaet entitaet) {
        return "package " + getRepositoryPackage(shortApplicationName, entitaet) + ";"
                + AbstractGenerator.LINE_SEPARATOR + AbstractGenerator.LINE_SEPARATOR;
    }

    protected final String getPackageLineMapper(final String shortApplicationName,
                                                final Entitaet entitaet) {
        return "package " + getPersistenceMapperPackage(shortApplicationName, entitaet) + ";"
                + AbstractGenerator.LINE_SEPARATOR + AbstractGenerator.LINE_SEPARATOR;
    }

    protected final String getPackageLineWertebereich(final String anwendungskuerzel,
                                                      final Wertebereich wertebereich) {
        return "package " + getWertebereichPackage(anwendungskuerzel, wertebereich) + ";"
                + AbstractGenerator.LINE_SEPARATOR + AbstractGenerator.LINE_SEPARATOR;
    }

    protected final String getWertebereichPackage(final String shortApplicationName,
                                                  final Wertebereich wertebereich) {
        String packageFolder = ".wertebereich";
        if (!StringUtils.isEmpty(wertebereich.getPaket())) {
            packageFolder = "." + wertebereich.getPaket()
                    .toLowerCase();
        }
        return "example." + shortApplicationName + packageFolder + ".domain".toLowerCase();
    }

    protected final String getEntitaetDomainPackage(final String shortApplicationName,
                                                    final Entitaet entitaet) {
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = "." + entitaet.getPaket()
                    .toLowerCase();
        }
        return "example." + shortApplicationName + packageFolder + ".domain".toLowerCase();
    }

    protected final String getPersistenceDomainPackage(final String shortApplicationName,
                                                       final Entitaet entitaet) {
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = "." + entitaet.getPaket()
                    .toLowerCase();
        }
        return "example." + shortApplicationName + packageFolder + ".db".toLowerCase();
    }

    protected final String getRepositoryPackage(final String shortApplicationName,
                                                final Entitaet entitaet) {
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = "." + entitaet.getPaket()
                    .toLowerCase();
        }
        return "example." + shortApplicationName + packageFolder + ".repo".toLowerCase();
    }

    protected final String getPersistenceMapperPackage(final String shortApplicationName,
                                                       final Entitaet entitaet) {
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = "." + entitaet.getPaket()
                    .toLowerCase();
        }
        return "example." + shortApplicationName + packageFolder + ".mapper".toLowerCase();
    }

    protected final void getMapperImport(final String shortApplicationName, final Entitaet entitaet,
                                         final Set<JavaImport> imports, final Entitaet quelle) {
        if (!Strings.CS.equals(quelle.getPaket(), entitaet.getPaket())) {
            imports.add(JavaImport.builder()
                    .from(getPersistenceMapperPackage(shortApplicationName, entitaet) + "."
                            + entitaet.getNameCapitalized() + "Mapper")
                    .build());
        }
    }

    protected final void getModelImport(final String shortApplicationName, final Entitaet entitaet,
                                        final Set<JavaImport> imports, final Entitaet quelle) {
        if (!Strings.CS.equals(quelle.getPaket(), entitaet.getPaket())) {
            imports.add(JavaImport.builder()
                    .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                            + entitaet.getNameCapitalized())
                    .build());
        }
    }

    protected final void getModelReferenceImport(final String shortApplicationName, final Entitaet entitaet,
                                                 final Set<JavaImport> imports) {
        imports.add(JavaImport.builder()
                .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "Reference")
                .build());
    }

    protected void getPkImport(final String shortApplicationName, final Entitaet entitaet,
                               final Set<JavaImport> imports, final String quelle) {
        if (!Strings.CS.equals(getPersistenceDomainPackage(shortApplicationName, entitaet),
                quelle)) {
            imports.add(JavaImport.builder()
                    .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                            + entitaet.getNameCapitalized() + "PK")
                    .build());
        }
    }

    protected void getPOImport(final String shortApplicationName, final Entitaet entitaet,
                               final Set<JavaImport> imports, final Entitaet quelle) {
        if (!Strings.CS.equals(quelle.getPaket(), entitaet.getPaket())) {
            imports.add(JavaImport.builder()
                    .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                            + entitaet.getNameCapitalized() + "PO")
                    .build());
        }
    }

    protected final void getFieldnameImport(final String shortApplicationName, final Maske<?> maske,
                                            final Set<JavaImport> imports, final Entitaet quelle) {
        if (!Strings.CS.equals(quelle.getPaket(), maske.getNameUncapitalized())) {
            imports.add(JavaImport.builder()
                    .from("example." + shortApplicationName + "." + maske.getName().toLowerCase()
                            + ".model." + maske.getNameCapitalized() + "FieldEnum")
                    .build());
        }
    }

    protected final void getWertebereichImport(final String shortApplicationName,
                                               final Set<JavaImport> imports, final Wertebereich wertebereich, final String quelle) {
        if ((wertebereich != null) && !Strings.CS
                .equals(getWertebereichPackage(shortApplicationName, wertebereich), quelle)) {
            imports.add(JavaImport.builder()
                    .from(getWertebereichPackage(shortApplicationName, wertebereich) + "."
                            + wertebereich.getNameCapitalized() + "Enum")
                    .build());
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.Enumerated")
                    .from("jakarta.persistence.EnumType")
                    .build());

        }
    }

    protected GeneratedFileBuilder klasseGenErzeugen(final LowCodeModel modell,
                                                     final String packageFolder, final String typ, final String dateiname) {
        return GeneratedFile.builder()
                .folder("src/gen/java/example/" + modell.getAnwendungskuerzel() + "/" + packageFolder
                        + "/" + typ)
                .file(dateiname);
    }

    protected GeneratedFileBuilder klasseErzeugen(final LowCodeModel modell,
                                                  final String packageFolder, final String typ, final String dateiname) {
        return GeneratedFile.builder()
                .folder("src/main/java/example/" + modell.getAnwendungskuerzel() + "/"
                        + packageFolder + "/" + typ)
                .file(dateiname);
    }

    protected GeneratedFileBuilder testklasseErzeugen(final LowCodeModel modell,
                                                      final String packageFolder, final String typ, final String dateiname) {
        return GeneratedFile.builder()
                .folder("src/test/java/example/" + modell.getAnwendungskuerzel() + "/"
                        + packageFolder + "/" + typ)
                .file(dateiname);
    }

    protected static Entitaet getHighestParentEntitaet(Entitaet entitaet, DomainModel modell) {
        Entitaet parentEntitaet = entitaet;
        while (parentEntitaet.getErbtVon() != null) {
            parentEntitaet = modell.getEntitaetByReference(parentEntitaet.getErbtVon());
        }
        return parentEntitaet;
    }

}
