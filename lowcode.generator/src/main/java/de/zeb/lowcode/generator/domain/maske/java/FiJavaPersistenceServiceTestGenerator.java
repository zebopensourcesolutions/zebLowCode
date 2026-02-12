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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.zeb.lowcode.model.domain.EntitaetsfeldMitEntitaet;
import org.apache.commons.lang3.StringUtils;

import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetsfeld;

/**
 * @author dkleine
 *
 */
@SuppressWarnings("nls")
public class FiJavaPersistenceServiceTestGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {

        List<GeneratedFile> result = new ArrayList<>();

        result.addAll(repositoryGenTestErzeugen(lcm));

        return result;
    }

    private List<GeneratedFile> repositoryGenTestErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();
        List<String> verarbeitet = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            if (entitaet.isEigenstaendig() && !entitaet.isAbstrakt()) {
                // Nur eigenständige nicht abstrakte Entitäten bekommen einen Service zum
                // Laden/Speichern
                repositoryTestErzeugen(modell, results, entitaet, verarbeitet);
            }
        }
        return results;
    }

    private void repositoryTestErzeugen(final LowCodeModel modell,
            final List<GeneratedFile> results, final Entitaet entitaet,
            final List<String> verarbeitet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLinePersistence(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = testklasseErzeugen(modell, packageFolder, "db",
                StringUtils.capitalize(entitaet.getName()) + "RepositoryTest.java");

        Set<JavaImport> imports = new HashSet<>();
        imports.addAll(repositoryInhaltErzeugen(modell.getAnwendungskuerzel(), entitaet, sb,
                modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private Set<JavaImport> repositoryInhaltErzeugen(final String shortApplicationName,
            final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("org.springframework.data.jpa.repository.JpaRepository")
                .from("org.springframework.stereotype.Repository")
                .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "PO")
                .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "PK")
                .build());

        appendLn(sb, """

                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 * Package private Repo für die Persistenz
                 */
                @Repository
                interface %sPoRepository extends JpaRepository<%sPO, %sPK>{
                    //Spring Data generiert die passenden Methoden
                }
                """.formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                entitaet.getNameCapitalized()));
        return imports;
    }

    public List<EntitaetsfeldMitEntitaet> getFelderMitVererbung(final Entitaet entitaet,
                                                                final DomainModel domain) {
        List<EntitaetsfeldMitEntitaet> felder = new ArrayList<>();
        for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
            felder.add(new EntitaetsfeldMitEntitaet(entitaet, entitaetsfeld));
        }
        if (entitaet.getErbtVon() != null) {
            Entitaet entitaetByReference = domain.getEntitaetByReference(entitaet.getErbtVon());
            if (entitaetByReference != null) {
                felder.addAll(getFelderMitVererbung(entitaetByReference, domain));
            }
        }
        return felder;
    }
}
