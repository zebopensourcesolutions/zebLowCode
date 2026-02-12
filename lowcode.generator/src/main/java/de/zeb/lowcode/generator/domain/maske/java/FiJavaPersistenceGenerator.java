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
import de.zeb.lowcode.generator.domain.NameUtils;
import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.generator.persistenz.AuditEntitaetsfeld;
import de.zeb.lowcode.generator.persistenz.IdEntitaetsfeld;
import de.zeb.lowcode.generator.persistenz.NumberIdEntitaetsfeld;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("nls")
public class FiJavaPersistenceGenerator extends AbstractJavaGenerator {

    private static String getCascadeFetchString(CascadeType cascadeDefault, FetchType fetchDefault, Set<JavaImport> imports, Entitaetsfeld feld) {
        String cascadeString = "";
        if (feld.getZielEntitaet().getCascadeType() != null) {
            if (!CascadeType.DEFAULT.equals(feld.getZielEntitaet().getCascadeType())) {
                imports.add(JavaImport.builder()
                        .from("jakarta.persistence.CascadeType")
                        .build());
                cascadeString += "cascade = CascadeType." + feld.getZielEntitaet().getCascadeType().name();
            } else if (cascadeDefault != null && !CascadeType.DEFAULT.equals(cascadeDefault)) {
                imports.add(JavaImport.builder()
                        .from("jakarta.persistence.CascadeType")
                        .build());
                cascadeString += "cascade = CascadeType." + cascadeDefault.name();
            }
        }
        String fetchString = "";
        if (!FetchType.DEFAULT.equals(feld.getZielEntitaet().getFetchType())) {
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.FetchType")
                    .build());
            fetchString += "fetch = FetchType." + feld.getZielEntitaet().getFetchType().name();
        } else if (!FetchType.DEFAULT.equals(fetchDefault)) {
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.FetchType")
                    .build());
            fetchString += "fetch = FetchType." + fetchDefault.name();
        }
        if (!cascadeString.isEmpty() && !fetchString.isEmpty()) {
            return cascadeString + ", " + fetchString;
        } else if (!cascadeString.isEmpty()) {
            return cascadeString;
        }
        return fetchString;
    }

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {
        return new ArrayList<>(modellErzeugen(lcm));
    }

    private List<GeneratedFile> modellErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            if (entitaet.isPersistenz()) {
                modelFuerEntitaetErzeugen(modell, results, entitaet);
            }
        }
        return results;
    }

    private void modelFuerEntitaetErzeugen(final LowCodeModel modell, final List<GeneratedFile> results, final Entitaet entitaet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLinePersistence(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }

        GeneratedFileBuilder generatedFileBuilder = klasseGenErzeugen(modell, packageFolder, "db",
                StringUtils.capitalize(entitaet.getName()) + "PO.java");

        Set<JavaImport> imports = new HashSet<>(variablenDefinitionErzeugen(modell.getAnwendungskuerzel(), entitaet, sb, modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());
    }

    private String getSpaltenname(final Entitaetsfeld entitaetsfeld) {
        String name = entitaetsfeld.getDbSpaltenname();
        if (StringUtils.isEmpty(name)) {
            name = NameUtils.camelToUnderscoreUpperCase(entitaetsfeld.getName());
        }
        return name;
    }

    private String getTabellenname(final String shortApplicationName, final Entitaet entitaet) {
        String name = entitaet.getDbTabellenname();
        if (StringUtils.isEmpty(name)) {
            if (StringUtils.isEmpty(shortApplicationName)) {
                name = NameUtils.camelToUnderscoreUpperCase(entitaet.getName());
            } else {
                name = NameUtils.camelToUnderscoreUpperCase(shortApplicationName) + "_"
                        + NameUtils.camelToUnderscoreUpperCase(entitaet.getName());
            }
        }
        return name;
    }

    private Set<JavaImport> variablenDefinitionErzeugen(final String shortApplicationName,
                                                        final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("lombok.ToString")
                .build());
        String erbtVonText = "";
        if (entitaet.getErbtVon() != null) {
            Entitaet erbtVonEntitaet = modell.getEntitaetByReference(entitaet.getErbtVon());
            getPOImport(shortApplicationName, erbtVonEntitaet, imports, entitaet);
            erbtVonText = "extends " + modell.getEntitaetByReference(entitaet.getErbtVon())
                    .getNameCapitalized() + "PO";
            if (erbtVonEntitaet.isPersistenz()) {
                imports.add(JavaImport.builder()
                        .from("jakarta.persistence.DiscriminatorValue")
                        .build());
                appendLn(sb, """
                        @DiscriminatorValue("%s")
                        """.formatted(entitaet.getName()));
            }
        }

        appendLn(sb, """
                
                /**
                 * Generierter Code, bitte keine manuellen Änderungen vornehmen
                 *
                 */
                """);
        if (entitaet.isPersistenz()) {

            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.Entity")
                    .from("jakarta.persistence.EntityListeners")
                    .from("jakarta.persistence.ExcludeSuperclassListeners")
                    .build());
            appendLn(sb, """
                    @Entity(name="%s")
                    @EntityListeners(%sPOListener.class)
                    @ExcludeSuperclassListeners
                    """.formatted(entitaet.getNameCapitalized(),
                    entitaet.getNameCapitalized()));
            // Tabellennamen nur dann mappen wenn ich nicht Teil einer Vererbungshierarchie bin
            if (entitaet.getErbtVon() == null) {
                imports.add(JavaImport.builder()
                        .from("jakarta.persistence.Table")
                        .build());
                appendLn(sb, """
                        @Table(name = "%s"%s%s)
                        """.formatted(getTabellenname(shortApplicationName, entitaet), getUniqueConstraint(imports, entitaet), getIndizes(imports, entitaet)));
            }
            if (istRootEinerVererbungshierarchie(entitaet, modell)) {
                imports.add(JavaImport.builder()
                        .from("jakarta.persistence.Inheritance")
                        .from("jakarta.persistence.InheritanceType")
                        .from("jakarta.persistence.DiscriminatorColumn")
                        .build());
                appendLn(sb, """
                        @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
                        @DiscriminatorColumn(name = "ELEM_DIFF")
                        """);
            }
        } else {
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.Entity")
                    .build());
            appendLn(sb, """
                    @Entity(name="%s")
                    """.formatted(entitaet.getNameCapitalized()));
        }
        if (entitaet.isAbstrakt()) {
            appendLn(sb, """
                    @ToString
                    public abstract class %sPO %s{
                    """.formatted(entitaet.getNameCapitalized(), erbtVonText));
        } else {
            appendLn(sb, """
                    @ToString
                    public class %sPO %s {
                    """.formatted(entitaet.getNameCapitalized(), erbtVonText));
        }

        // IDs nicht mehr generieren wenn ich Teil einer Vererbungshierarchie bin
        if (entitaet.getErbtVon() == null) {
            sb.append(idErzeugen(entitaet, imports, modell));
        }

        String packageLinePersistence = getPackageLinePersistence(shortApplicationName, entitaet);

        Entitaet topSuper = getTopSuper(entitaet, modell);

        Optional<Entitaetsfeld> entPK = topSuper.getFelder().stream().filter(Entitaetsfeld::isPk).findFirst();
        String pkSpaltenname = null;
        if (entPK.isPresent() && entPK.get().getDbSpaltenname() != null) {
            pkSpaltenname = entPK.get().getDbSpaltenname();
        }


        //Alle PK Felder werden in einer Embedded Klasse als zusammengesetzter Schlüssel zusammengefasst
        //In der PO Klasse sind die unter getId() / setId() erreichbar
        List<Entitaetsfeld> pkFelder = entitaet.getFelder().stream().filter(e -> e.isPk() && !(e instanceof NumberIdEntitaetsfeld)).toList();
        if (!pkFelder.isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("lombok.Getter")
                    .from("lombok.Setter")
                    .from("jakarta.persistence.EmbeddedId")
                    .build());
            append(sb, """
                    @EmbeddedId
                    @Getter @Setter private %sPK id;""".indent(4)
                    .formatted(entitaet.getNameCapitalized()));

        }

        List<Entitaetsfeld> numberPKFelder = entitaet.getFelder().stream().filter(e -> e.isPk() && (e instanceof NumberIdEntitaetsfeld)).toList();
        if (!numberPKFelder.isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("lombok.Getter")
                    .from("lombok.Setter")
                    .from("jakarta.persistence.GeneratedValue")
                    .from("jakarta.persistence.GenerationType")
                    .from("jakarta.persistence.Id")
                    .build());
            append(sb, """
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    @Column(name = "%s")
                    @Getter @Setter private Integer id;""".indent(4)
                    .formatted(numberPKFelder.get(0).getDbSpaltenname()));

        }


        for (Entitaetsfeld feld : entitaet.getFelder()) {
            if (feld.isPersistenz() && !feld.isPk()) {
                getWertebereichImport(shortApplicationName, imports, feld.getWertebereich(),
                        packageLinePersistence);

                if (feld.getAnzahlZeichen() != null) {
                    imports.add(JavaImport.builder()
                            .from("jakarta.persistence.Column")
                            .from("jakarta.validation.constraints.Size")
                            .build());

                    if (!StringUtils.isEmpty(feld.getDbSpaltenname())) {
                        imports.add(JavaImport.builder()
                                .from("jakarta.persistence.Column")
                                .build());
                        append(sb, """
                                @Column(length = %s, name = "%s")
                                """.formatted(feld.getAnzahlZeichen(), getSpaltenname(feld)));
                    } else {

                        append(sb, """
                                @Column(length = %s)""".formatted(feld.getAnzahlZeichen())
                                .indent(4));
                    }
                    append(sb,
                            """
                                    @Size(max = %s, message = "Das Feld '%s' darf höchstens %s Zeichen aufweisen.")"""
                                    .formatted(feld.getAnzahlZeichen(), feld.getFachlicherName(),
                                            feld.getAnzahlZeichen())
                                    .indent(4));

                }
                if ((feld.getAnzahlZeichen() != null
                        && Datentyp.TEXT.equals(feld.getDatenTyp())
                        && feld.getAnzahlZeichen() > 32739)
                        | Datentyp.BINARY.equals(feld.getDatenTyp())) {
                    imports.add(JavaImport.builder()
                            .from("jakarta.persistence.Lob")
                            .build());
                    append(sb, """
                            @Lob"""
                            .indent(4));
                }
                if (Datentyp.VERSION.equals(feld.getDatenTyp())) {
                    imports.add(JavaImport.builder()
                            .from("jakarta.persistence.Version")
                            .build());
                    append(sb, """
                            @Version"""
                            .indent(4));
                }
                if (!feld.isOptional() && !feld.isPk()) {
                    imports.add(JavaImport.builder()
                            .from("jakarta.validation.constraints.NotNull")
                            .build());
                    append(sb, """
                            @NotNull"""
                            .indent(4));
                }

                if (feld instanceof AuditEntitaetsfeld) {
                    imports.add(JavaImport.builder()
                            .from("example.myapp.MYAPPAudit")
                            .build());
                    append(sb, """
                            @MYAPPAudit"""
                            .indent(4));
                }
                if (feld instanceof IdEntitaetsfeld) {
                    imports.add(JavaImport.builder()
                            .from("example.myapp.MYAPP_UUID")
                            .build());
                    append(sb, """
                            @MYAPP_UUID""".indent(4));
                }

                if (feld.getZielEntitaet() != null) {
                    Entitaet ziel = modell.getEntitaetByReference(feld.getZielEntitaet());

                    getPOImport(shortApplicationName, ziel, imports, entitaet);

                    Optional<Entitaetsfeld> zielPK = getTopSuper(ziel, modell).getFelder().stream().filter(Entitaetsfeld::isPk).findFirst();
                    String zielDbSpaltenname = feld.getDbSpaltenname();
                    if (zielPK.isPresent()) {
                        zielDbSpaltenname = zielPK.get().getDbSpaltenname();
                    }

                    if (feld.isAlsListe()) {
                        imports.add(JavaImport.builder()
                                .from("java.util.List")
                                .from("lombok.Getter")
                                .from("lombok.Setter")
                                .from("java.util.ArrayList")
                                .build());
                        if (!ziel.isEigenstaendig()) {
                            String cascadeFetchString = getCascadeFetchString(CascadeType.ALL, FetchType.DEFAULT, imports, feld);
                            if (!cascadeFetchString.isEmpty()) {
                                cascadeFetchString += ", orphanRemoval = true";
                            }
                            imports.add(JavaImport.builder()
                                    .from("jakarta.persistence.OneToMany")
                                    .from("jakarta.persistence.JoinColumn")
                                    .build());
                            append(sb, """
                                    @ToString.Exclude
                                    @OneToMany(%s)"""
                                    .indent(4)
                                    .formatted(cascadeFetchString));
                            append(sb, "@JoinColumn(name=\"" + pkSpaltenname.toUpperCase() + "\")");
                        } else {
                            String joinTableSuffix = "";
                            if (feld.getName().equals("mailccadressaten")) {
                                joinTableSuffix = "_KPIE";
                            }

                            imports.add(JavaImport.builder()
                                    .from("jakarta.persistence.ManyToMany")
                                    .from("jakarta.persistence.JoinTable")
                                    .from("jakarta.persistence.JoinColumn")
                                    .build());
                            append(sb, """
                                    @ToString.Exclude
                                    @ManyToMany(%s)""".indent(4).formatted(getCascadeFetchString(CascadeType.DEFAULT, FetchType.LAZY, imports, feld)));
                            append(sb, "@JoinTable(name=\"" + entitaet.getDbTabellenname().toUpperCase() + "_" + ziel.getDbTabellenname().toUpperCase().replace("MYDB_", "") + joinTableSuffix + "\"," +
                                    "joinColumns = @JoinColumn(name=\"" + feld.getFachlicherName() + "_" + zielDbSpaltenname + "\"), " +
                                    "inverseJoinColumns = @JoinColumn(name = \"" + feld.getDbSpaltenname() + "_" + zielDbSpaltenname + "\"))");

                        }

                        append(sb,
                                """
                                        @Getter @Setter private List<%sPO> %s = new ArrayList<>();"""
                                        .indent(4)
                                        .formatted(feld.getZielEntitaet()
                                                .getName(), feld.getName()));
                    } else {
                        if (!ziel.isEigenstaendig()) {
                            imports.add(JavaImport.builder()
                                    .from("jakarta.persistence.ManyToOne")
                                    .from("lombok.Getter")
                                    .from("lombok.Setter")
                                    .build());
                            append(sb, """
                                    @ManyToOne(%s)""".indent(4)
                                    .formatted(getCascadeFetchString(CascadeType.ALL, FetchType.LAZY, imports, feld)));

                        } else {
                            imports.add(JavaImport.builder()
                                    .from("jakarta.persistence.ManyToOne")
                                    .build());
                            append(sb, """
                                    @ToString.Exclude
                                    @ManyToOne(%s)""".indent(4).formatted(getCascadeFetchString(CascadeType.DEFAULT, FetchType.LAZY, imports, feld)));
                        }
                        getPOImport(shortApplicationName, ziel, imports, entitaet);


                        if (!StringUtils.isEmpty(feld.getDbSpaltenname())) {
                            imports.add(JavaImport.builder()
                                    .from("jakarta.persistence.JoinColumn")
                                    .build());
                            append(sb, """
                                    @JoinColumn(name = "%s")
                                    """.formatted(feld.getDbSpaltenname()));
                        }
                        append(sb, """
                                @Getter @Setter private %sPO %s;""".indent(4)
                                .formatted(feld.getZielEntitaet()
                                        .getName(), feld.getName()));
                    }
                } else {
                    String typ = GeneratorUtils.getJavaType(feld, imports, modell, false);
                    if (feld.getWertebereich() != null) {
                        typ = "String";
                    }
                    if (feld.isPk()) {
                        typ = entitaet.getNameCapitalized() + "PK";
                    }
                    getWertebereichImport(shortApplicationName, imports, feld.getWertebereich(),
                            packageLinePersistence);
                    if (feld.isAlsListe()) {

                        String tabellenName = entitaet.getDbTabellenname() + "_" + feld.getDbSpaltenname();
                        imports.add(JavaImport.builder()
                                .from("java.util.List")
                                .from("java.util.ArrayList")
                                .from("lombok.Getter")
                                .from("lombok.Setter")
                                .from("jakarta.persistence.ElementCollection")
                                .from("jakarta.persistence.CollectionTable")
                                .from("jakarta.persistence.JoinColumn")
                                .from("jakarta.persistence.Column")
                                .from("jakarta.persistence.FetchType")
                                .build());
                        append(sb,
                                """
                                        @CollectionTable(name = "%s", joinColumns = @JoinColumn(name = "%s_ID"))
                                        @ElementCollection(fetch = FetchType.LAZY)
                                        @ToString.Exclude
                                        @Column(name="%s")
                                        @Getter @Setter private %s %s = new ArrayList<>();"""
                                        .indent(4)
                                        .formatted(tabellenName, entitaet.getDbTabellenname()
                                                .toUpperCase().replace("MYDB_", ""), feld.getDbSpaltenname(), typ, feld.getName()));
                    } else {
                        imports.add(JavaImport.builder()
                                .from("lombok.Getter")
                                .from("lombok.Setter")
                                .build());
                        //Wenn die Zeichenanzahl gesetzt ist wurde die Column-Annotation schon gesetzt
                        if (!StringUtils.isEmpty(feld.getDbSpaltenname()) && feld.getAnzahlZeichen() == null && !feld.isPk()) {
                            imports.add(JavaImport.builder()
                                    .from("jakarta.persistence.Column")
                                    .build());

                            if (feld.getAnzahlNachkommastellen() != null) {
                                append(sb, """
                                        @Column(name = "%s", precision = 31, scale=%s)
                                        """.formatted(getSpaltenname(feld), feld.getAnzahlNachkommastellen()));
                            } else {
                                append(sb, """
                                        @Column(name = "%s")
                                        """.formatted(getSpaltenname(feld)));
                            }
                        }
                        append(sb, """
                                @Getter @Setter private %s %s;""".indent(4)
                                .formatted(typ, feld.getName()));
                    }
                }
            }
        }

        // Hashcode Equals
        // https://thorben-janssen.com/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/

        appendLn(sb,
                """
                        @Override
                        /**
                         * hashCode darf sich nicht ändern, sobald der PK generiert wurde. Der fixe Hashcode reduziert die Performance, ist aber korrekt.
                         * https://thorben-janssen.com/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/
                         */
                        public int hashCode() {
                            return 42;
                        }
                        """);

        imports.add(JavaImport.builder()
                .from("java.util.Objects")
                .build());
        String equalsString = "Objects.equals(getId(), other.getId())";
        appendLn(sb,
                """
                        @Override
                        /**
                         * Equals Methode darf nur die PKs (auch geerbte) vergleichen
                         * https://thorben-janssen.com/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/
                         */
                        public boolean equals(Object obj) {
                            if (this == obj){
                                return true;
                            }
                            if (obj == null){
                                return false;
                            }
                            if (getClass() != obj.getClass()){
                                return false;
                            }
                            %sPO other = (%sPO) obj;
                            return %s;
                        }
                        """
                        .formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                equalsString));

        // Transient oldValues für das auditing
        if (imports.stream().anyMatch(ja -> ja.getImports().contains("example.myapp.MYAPPAudit"))) {
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.Transient;")
                    .from("java.util.Map;")
                    .build());

            appendLn(sb,
                    """
                             @Transient
                             @ToString.Exclude
                             private Map<String, Object> oldValues;
                            
                             public Map<String, Object> getOldValues() {
                                 return oldValues;
                             }
                            
                             public void setOldValues(Map<String, Object> oldValues) {
                                 this.oldValues = oldValues;
                             }
                            """);
        }

        appendLn(sb, """
                }
                """);
        return imports;
    }

    private String getUniqueConstraint(Set<JavaImport> imports, Entitaet entitaet) {
        //@Table(name = "MYAPP_ABFRAGE_PROTOKOLL_SPERRE", uniqueConstraints = @UniqueConstraint(columnNames = "abfrage_id"))
        List<Entitaetsfeld> eindeutigeFelder = entitaet.getFelder().stream().filter(Entitaetsfeld::isFachlichEindeutig).toList();
        if (!eindeutigeFelder.isEmpty()) {
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.UniqueConstraint")
                    .build());
            String spaltenText = eindeutigeFelder.stream().map(e -> "\"" + getSpaltenname(e) + "\"").collect(Collectors.joining(", "));
            return ",  uniqueConstraints = @UniqueConstraint(columnNames = {%s}, name=\"%s\")".formatted(spaltenText, entitaet.getDbTabellenname() + "_UK");
        }
        return "";
    }

    private String getIndizes(Set<JavaImport> imports, Entitaet entitaet) {
        //@Table(name = "MYAPP_ABFRAGE_PROTOKOLL_SPERRE", uniqueConstraints = @UniqueConstraint(columnNames = "abfrage_id", indexes = {@Index(name = "myappElement", columnList = "BSIS_ELEM_ID")}))
        if (entitaet.getDbIndex() != null) {
            List<Entitaetsfeld> spaltenImIndex = entitaet.getDbIndex().getSpalten();
            if (!spaltenImIndex.isEmpty()) {
                imports.add(JavaImport.builder()
                        .from("jakarta.persistence.Index")
                        .build());
                String spaltenText = spaltenImIndex.stream().map(this::getSpaltenname).collect(Collectors.joining(", "));
                return ", indexes = {@Index(name = \"%s\", columnList = \"%s\")}".formatted(entitaet.getDbIndex().getName(), spaltenText);
            }
        }
        return "";
    }

    private boolean istRootEinerVererbungshierarchie(final Entitaet entitaet,
                                                     final DomainModel modell) {
        return erbtJemandVonDieserEntitaet(entitaet, modell) && (entitaet.getErbtVon() == null);
    }

    private boolean erbtJemandVonDieserEntitaet(final Entitaet entitaet, final DomainModel modell) {
        for (Entitaet e : modell.getEntitaeten()) {
            if ((e.getErbtVon() != null) && (StringUtils.equals(e.getErbtVon()
                    .getName(), entitaet.getName()) && StringUtils.equals(
                    e.getErbtVon()
                            .getPaket(),
                    entitaet.getPaket()))) {
                return true;
            }
        }
        return false;
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

    private String idErzeugen(final Entitaet entitaet, final Set<JavaImport> imports,
                              final DomainModel domain) {
        StringBuilder sb = new StringBuilder();
        boolean fehlenderPk = true;
        for (EntitaetsfeldMitEntitaet ef : getFelderMitVererbung(entitaet, domain)) {
            if (ef.feld()
                    .isPk()) {
                fehlenderPk = false;
            }
        }
        if (fehlenderPk) {
            imports.add(JavaImport.builder()
                    .from("jakarta.persistence.EmbeddedId")
                    .from("lombok.Getter")
                    .build());
            append(sb, """
                        @EmbeddedId
                        @Getter private %sPK                id;
                    """.formatted(entitaet.getNameCapitalized()).indent(4));
        }

        return sb.toString();
    }

    private Entitaet getTopSuper(Entitaet e, DomainModel lcm) {
        if (e.getErbtVon() == null) {
            return e;
        }

        return getTopSuper(lcm.getEntitaeten().stream().filter(e2 -> e2.getName().equals(e.getErbtVon().getName())).findFirst().get(), lcm);
    }

}
