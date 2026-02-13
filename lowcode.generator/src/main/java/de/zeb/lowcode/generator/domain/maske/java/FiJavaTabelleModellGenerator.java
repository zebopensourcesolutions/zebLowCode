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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dkleine
 */
@SuppressWarnings("nls")
public class FiJavaTabelleModellGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {
        return new ArrayList<>(modellErzeugen(lcm));
    }

    private List<GeneratedFile> modellErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();
        if (modell.getUi() != null) {
            for (Maske<?> maske : modell.getUi()
                    .getMasken()) {
                if (!GeneratorUtils.entityForMaskIsPartOfDomain(maske, modell.getDomain())) {
                    for (Entitaetsfeld entitaetsfeld : maske.getEntitaet()
                            .getFelderMitVererbung(modell.getDomain())) {
                        if (entitaetsfeld.isAlsTabelle()) {
                            modelFuerTabellenfeldErzeugen(modell, results, maske, entitaetsfeld);
                        }
                    }
                }
            }
        }
        return results;
    }

    private void modelFuerTabellenfeldErzeugen(final LowCodeModel modell,
                                               final List<GeneratedFile> results, final Maske<?> maske, final Entitaetsfeld feld) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLine(maske.getName(), modell.getAnwendungskuerzel());
        GeneratedFileBuilder generatedFileBuilder = GeneratedFile.builder()
                .folder("src/gen/java/example/" + modell.getAnwendungskuerzel() + "/"
                        + maske.getName()
                        .toLowerCase()
                        + "/model/")
                .file(StringUtils.capitalize(feld.getName()) + "Zeile.java");
        Set<JavaImport> imports = new HashSet<>(variablenDefinitionErzeugen(modell.getAnwendungskuerzel(), feld, sb,
                modell.getDomain(), maske.getEntitaet()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());
    }

    private String getPackageLine(final String maskenName, final String shortApplicationName) {
        return "package example." + shortApplicationName + "." + maskenName + ".model"
                + ";" + LINE_SEPARATOR + LINE_SEPARATOR;
    }

    private Set<JavaImport> variablenDefinitionErzeugen(final String shortApplicationName,
                                                        final Entitaetsfeld feld, final StringBuilder sb, final DomainModel domain,
                                                        final Entitaet entitaet) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .from("lombok.AllArgsConstructor")
                .from("lombok.NonNull")
                .from("lombok.Builder")
                .from("example.myframework.api.business.table.TableRow")
                .from("java.util.UUID")
                .from("lombok.extern.jackson.Jacksonized")
                .build());
        Entitaet zielentitaet = domain.getEntitaetByReference(feld.getZielEntitaet());
        getModelImport(shortApplicationName, zielentitaet, imports, entitaet);

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
                public class %sZeile implements TableRow<%s> {
                    private static final long serialVersionUID = 1L;
                    @Builder.Default
                    private String            id = UUID.randomUUID().toString();
                    @NonNull
                    private %s                fachobjekt;
                    @Builder.Default
                    private Boolean           informatorisch = false;
                    @Builder.Default
                    private Boolean           readonly = false;
                    @Builder.Default
                    private Boolean           zeileObenFixiert = false;
                    @Builder.Default
                    private Boolean           zeileUntenFixiert = false;
                """.formatted(feld.getNameCapitalized(), zielentitaet.getNameCapitalized(),
                zielentitaet.getNameCapitalized()));

        FiJavaDomainGenerator.generateHashcodeEquals(List.of(Entitaetsfeld.builder().name("id").build()), sb, imports, feld.getNameCapitalized() + "Zeile");
        appendLn(sb, "}");
        return imports;
    }

}
