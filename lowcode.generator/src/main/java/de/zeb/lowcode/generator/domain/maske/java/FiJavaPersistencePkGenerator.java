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
import de.zeb.lowcode.generator.persistenz.IdEntitaetsfeld;
import de.zeb.lowcode.generator.persistenz.NumberIdEntitaetsfeld;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.Datentyp;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetreferenz;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("nls")
public class FiJavaPersistencePkGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {
        return new ArrayList<>(modellPKsErzeugen(lcm));
    }

    private List<GeneratedFile> modellPKsErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            if (entitaet.isPersistenz()) {
                List<Entitaetsfeld> entitaetsfelds = entitaet.getFelder().stream().filter(f -> f instanceof NumberIdEntitaetsfeld).toList();

                if (entitaetsfelds.isEmpty()) {
                    modelPKsFuerEntitaetErzeugen(modell, results, entitaet);
                }
            }
        }
        return results;
    }

    private void modelPKsFuerEntitaetErzeugen(final LowCodeModel modell, final List<GeneratedFile> results, final Entitaet entitaet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLinePersistence(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        if (entitaet.getErbtVon() != null) {
            Entitaet entitaetZuPruefen = entitaet;
            while (entitaetZuPruefen != null) {
                boolean hasPkFields = entitaetZuPruefen.getFelder()
                        .stream()
                        .anyMatch(Entitaetsfeld::isPk);
                Entitaetreferenz parentEntitaet = entitaetZuPruefen.getErbtVon();
                if (hasPkFields && parentEntitaet != null) {
                    throw new IllegalStateException("%s darf nicht eigene PK-Felder definieren und erben".formatted(entitaetZuPruefen));
                }
                if (parentEntitaet != null) {
                    entitaetZuPruefen = modell.getDomain().getEntitaetByReference(parentEntitaet);
                } else {
                    entitaetZuPruefen = null;
                }
            }
            // Kein PK erzeugen, es wird der Eltern-PK verwendet.
            return;
        }
        GeneratedFileBuilder generatedFileBuilder = klasseGenErzeugen(modell, packageFolder, "db",
                StringUtils.capitalize(entitaet.getName()) + "PK.java");
        Set<JavaImport> imports = new HashSet<>(variablenPKDefinitionErzeugen(modell.getAnwendungskuerzel(), entitaet, sb, modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private Set<JavaImport> variablenPKDefinitionErzeugen(final String shortApplicationName,
                                                          final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        List<Entitaetsfeld> felder = new ArrayList<>(entitaet.getFelder());
        Set<JavaImport> imports = new HashSet<>(List.of(
                JavaImport.builder()
                        .from("java.io.Serializable")
                        .build(),
                JavaImport.builder()
                        .from("jakarta.persistence.Embeddable")
                        .build()
        ));

        StringBuilder felderBuilder = new StringBuilder();
        String packageLinePersistence = getPackageLinePersistence(shortApplicationName, entitaet);

        for (Entitaetsfeld feld : felder) {
            if (feld.isPersistenz() && feld.isPk()) {
                getWertebereichImport(shortApplicationName, imports, feld.getWertebereich(),
                        packageLinePersistence);

                if (feld.getZielEntitaet() != null) {
                    Entitaet ziel = modell.getEntitaetByReference(feld.getZielEntitaet());
                    if (!feld.isAlsListe()) {
                        getModelImport(shortApplicationName, ziel, imports, entitaet);
                        append(felderBuilder, """
                                private %sPO %s;""".indent(4)
                                .formatted(feld.getZielEntitaet()
                                        .getName(), feld.getName()));
                    }
                } else {
                    String typ = GeneratorUtils.getJavaType(feld, imports, modell, false);
                    if (feld.getWertebereich() != null) {
                        typ = feld.getWertebereich()
                                .getNameCapitalized() + "Enum";
                    }
                    if (!feld.isAlsListe()) {
                        if (feld.getDatenTyp()
                                .equals(Datentyp.ID)) {
                            if (!feld.isFachlichEindeutig()) {
                                if (feld instanceof IdEntitaetsfeld) {
                                    imports.add(JavaImport.builder()
                                            .from("jakarta.persistence.GeneratedValue")
                                            .from("org.hibernate.annotations.GenericGenerator")
                                            .from("example.myapp.MyappIdGenerator")
                                            .build());
                                    append(felderBuilder,
                                            """
                                                                @GenericGenerator(name = "MYAPP_ID", type = MyappIdGenerator.class)
                                                                @GeneratedValue(generator = "MYAPP_ID")
                                                    """);
                                } else {
                                    imports.add(JavaImport.builder()
                                            .from("jakarta.persistence.GeneratedValue")
                                            .from("org.hibernate.annotations.UuidGenerator")
                                            .from("org.hibernate.annotations.UuidGenerator.Style")
                                            .build());
                                    append(felderBuilder, """
                                                    @GeneratedValue
                                                    @UuidGenerator(style = Style.TIME)
                                            """);
                                }
                            }
                        }
                        imports.add(JavaImport.builder()
                                .from("jakarta.persistence.Column")
                                .build());
                        if (feld.getWertebereich() != null) {
                            append(felderBuilder, """
                                    @Enumerated(EnumType.STRING) 
                                    """);
                        }
                        append(felderBuilder, """
                                    @Column(name = "%s")
                                """.formatted(feld.getDbSpaltenname()));

                        append(felderBuilder, """
                                private %s %s;""".indent(4)
                                .formatted(typ, feld.getName()));
                    }
                }
            }
        }
        imports.add(JavaImport.builder()
                .from("lombok.Builder")
                .from("lombok.Getter")
                .from("lombok.Setter")
                .from("lombok.ToString")
                .build());

        appendLn(sb, """

                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                @Getter
                @Setter
                @ToString
                @Builder
                """);


        imports.add(JavaImport.builder()
                .from("lombok.AccessLevel")
                .from("lombok.AllArgsConstructor")
                .from("lombok.NoArgsConstructor")
                .build());

        appendLn(sb, """
                @NoArgsConstructor
                @AllArgsConstructor(access = AccessLevel.PROTECTED)
                """);

        appendLn(sb, """
                @Embeddable
                public class %sPK implements Serializable {

                private static final long serialVersionUID = 1L;
                """.formatted(entitaet.getNameCapitalized()));
        sb.append(felderBuilder);

        FiJavaDomainGenerator.generateHashcodeEquals(felder.stream().filter(f -> f.isPk() && f.isPersistenz()).collect(Collectors.toList()), sb, imports, entitaet.getNameCapitalized() + "PK");

        appendLn(sb, """
                }
                """);
        return imports;
    }
}
