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

import de.zeb.lowcode.generator.domain.AbstractGenerator;
import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.ui.Maske;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public class FiJavaMaskenModellGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {
        return new ArrayList<>(modellErzeugen(lcm));
    }

    private List<GeneratedFile> modellErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();
        if (modell.getUi()!=null) {
            for (Maske<?> maske : modell.getUi()
                    .getMasken()) {
                if (!GeneratorUtils.entityForMaskIsPartOfDomain(maske, modell.getDomain())) {
                    // Maskenmodell nur anlegen wenn es keine Domänenentität ist
                    modelFuerMaskeErzeugen(modell, results, maske);
                }
            }
        }
        return results;
    }

    private void modelFuerMaskeErzeugen(final LowCodeModel modell,
                                        final List<GeneratedFile> results, final Maske<?> maske) {
        StringBuilder sb = new StringBuilder();
        String packageLine = "package example." + modell.getAnwendungskuerzel() + "."
                + maske.getName().toLowerCase() + ".model" + ";" + AbstractGenerator.LINE_SEPARATOR
                + AbstractGenerator.LINE_SEPARATOR;
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + modell.getAnwendungskuerzel() + "/"
                        + maske.getName().toLowerCase() + "/model/")
                .file(maske.getNameCapitalized() + "Model.java");
        Set<JavaImport> imports = new HashSet<>(variablenDefinitionErzeugen(modell.getAnwendungskuerzel(), maske,
                maske.getEntitaet(), sb, modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());
    }

    private Set<JavaImport> variablenDefinitionErzeugen(final String shortApplicationName,
                                                        final Maske<?> maske, final Entitaet entitaet, final StringBuilder sb,
                                                        final DomainModel domain) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.AllArgsConstructor")
                .from("lombok.Builder")
                .from("java.io.Serializable")
                .from("lombok.extern.jackson.Jacksonized")
                .build());
        appendLn(sb, """

                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @Getter
                @Setter
                @ToString
                @AllArgsConstructor
                @Jacksonized
                @Builder
                public class %sModel implements Serializable {
                    private static final long serialVersionUID = 1L;
                """.formatted(maske.getNameCapitalized()));

        for (Entitaetsfeld feld : entitaet.getFelder()) {
            maskenfeldZeileErzeugen(shortApplicationName, sb, imports, feld, domain);
        }
        FiJavaDomainGenerator.generateHashcodeEquals(entitaet.getFelderMitVererbung(domain), sb, imports, maske.getNameCapitalized() + "Model");


        appendLn(sb, """
                }
                """);

        return imports;
    }

}
