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

import de.zeb.lowcode.generator.persistenz.NumberIdEntitaetsfeld;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.domain.EntitaetsfeldMitEntitaet;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("nls")
public class FiJavaPersistenceMappingGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {
        return new ArrayList<>(mapperErzeugen(lcm));
    }

    private List<GeneratedFile> mapperErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            if (entitaet.isPersistenz()) {
                mapperFuerEntitaetErzeugen(modell, results, entitaet);
            }
        }
        return results;
    }

    private void mapperFuerEntitaetErzeugen(final LowCodeModel modell, final List<GeneratedFile> results, final Entitaet entitaet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLineMapper(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseGenErzeugen(modell, packageFolder,
                "mapper", StringUtils.capitalize(entitaet.getName()) + "Mapper.java");

        Set<JavaImport> imports = new HashSet<>();
        if (entitaet.isAbstrakt()) {
            imports.addAll(variablenDefinitionErzeugenAbstrakt(modell.getAnwendungskuerzel(),
                    entitaet, sb, modell.getDomain()));
        } else {
            imports.addAll(variablenDefinitionErzeugen(modell.getAnwendungskuerzel(), entitaet, sb,
                    modell.getDomain()));
        }

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());
    }

    private Set<JavaImport> variablenDefinitionErzeugen(final String shortApplicationName,
                                                        final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "PO")
                .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized())
                .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "." + entitaet.getNameCapitalized()
                        + "Builder")
                .from("java.util.List")
                .from("java.util.Optional")
                .from("java.util.stream.StreamSupport")
                .build());
        String mapToPoStart = "%sPO dbModell = new %sPO();".formatted(entitaet.getNameCapitalized(),
                entitaet.getNameCapitalized());
        String mapFromPoStart = "%sBuilder<?, ?> builder = %s.builder();"
                .formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized());

        StringBuilder mapToPO = new StringBuilder();
        StringBuilder mapFromPO = new StringBuilder();
        StringBuilder mapToPOReference = new StringBuilder();
        StringBuilder mapFromPOReference = new StringBuilder();
        StringBuilder mapKey = new StringBuilder();

        StringBuilder zeilenMapper = new StringBuilder();

        for (Entitaetsfeld feld : entitaet.getFelderMitVererbung(modell)) {
            if (feld.isPersistenz()) {
                boolean feldInReferenz = feld.getZielEntitaet() == null || !feld.isAlsListe();
                if (feld.getZielEntitaet() != null) {
                    Entitaet ziel = modell.getEntitaetByReference(feld.getZielEntitaet());
                    getMapperImport(shortApplicationName, ziel, imports, entitaet);
                    String mapToPoMethod = "mapToPO";
                    String mapFromPoMethod = "mapFromPO";
                    if (ziel.isEigenstaendig()) {
                        mapToPoMethod += "Reference";
                        mapFromPoMethod += "Reference";
                    }
                    if (feld.isAlsTabelle()) {
                        imports.add(JavaImport.builder()
                                .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                                        + feld.getNameCapitalized() + "Zeile")
                                .from(getEntitaetDomainPackage(shortApplicationName, ziel) + "."
                                        + ziel.getNameCapitalized())
                                .from("java.util.ArrayList")
                                .from("java.util.stream.Collectors")
                                .build());
                        append(mapToPO, feldInReferenz ? mapToPOReference : null,
                                """
                                        dbModell.set%s(%sMapper.%s(mapFromAutomationenZeile(domainModell.get%s())));"""
                                        .indent(8)
                                        .formatted(feld.getNameCapitalized(),
                                                ziel.getNameCapitalized(),
                                                mapToPoMethod,
                                                feld.getNameCapitalized()).replace("Automationen", feld.getNameCapitalized())
                                        .replace("Automation", ziel.getNameCapitalized()));
                        append(mapFromPO, feldInReferenz ? mapFromPOReference : null, """
                                builder.%s(mapToAutomationenZeile(%sMapper.%s(dbModell.get%s())));"""
                                .indent(8)
                                .formatted(feld.getNameUncapitalized(), ziel.getNameCapitalized(), mapFromPoMethod,
                                        feld.getNameCapitalized()).replace("Automationen", feld.getNameCapitalized())
                                .replace("Automation", ziel.getNameCapitalized()));

                        appendLn(zeilenMapper,
                                """
                                        
                                        public static List<Automation> mapFromAutomationenZeile(final List<AutomationenZeile> zeilen) {
                                            if (zeilen != null) {
                                                return zeilen.stream()
                                                    .map((e) -> e.getFachobjekt())
                                                    .collect(Collectors.toList());
                                            }
                                            return new ArrayList<>();
                                        }
                                        
                                        public static List<AutomationenZeile> mapToAutomationenZeile(final List<Automation> domainList) {
                                            if (domainList != null) {
                                                return domainList.stream()
                                                    .map((e) -> AutomationenZeile.builder()
                                                            .fachobjekt(e)
                                                            .build())
                                                    .collect(Collectors.toList());
                                            }
                                            return new ArrayList<>();
                                        }
                                        
                                        """
                                        .indent(8)
                                        .replace("Automationen", feld.getNameCapitalized())
                                        .replace("Automation", ziel.getNameCapitalized()));
                    } else {
                        append(mapToPO, feldInReferenz ? mapToPOReference : null, """
                                dbModell.set%s(%sMapper.%s(domainModell.get%s()));""".indent(8)
                                .formatted(feld.getNameCapitalized(), ziel.getNameCapitalized(), mapToPoMethod,
                                        feld.getNameCapitalized()));
                        append(mapFromPO, feldInReferenz ? mapFromPOReference : null, """
                                builder.%s(%sMapper.%s(dbModell.get%s()));""".indent(8)
                                .formatted(feld.getNameUncapitalized(), ziel.getNameCapitalized(), mapFromPoMethod,
                                        feld.getNameCapitalized()));
                    }
                } else {
                    if (feld.isPk() && !(feld instanceof NumberIdEntitaetsfeld)) {
                        append(mapKey, """
                                pk.set%s(domainModell.get%s());""".indent(8)
                                .formatted(feld.getNameCapitalized(), feld.getNameCapitalized()));
                        append(mapFromPO, feldInReferenz ? mapFromPOReference : null, """
                                builder.%s(dbModell.getId().get%s());""".indent(8)
                                .formatted(feld.getNameUncapitalized(), feld.getNameCapitalized()));
                    } else if (feld.alsListe) {
                        var getterDomain = "domainModell.get%s()".formatted(feld.getNameCapitalized());
                        var getterDb = "dbModell.get%s()".formatted(feld.getNameCapitalized());
                        append(mapToPO, feldInReferenz ? mapToPOReference : null, """
                                dbModell.set%s(%s != null ? new ArrayList<>(%s) : null);""".indent(8)
                                .formatted(feld.getNameCapitalized(), getterDomain, getterDomain));
                        append(mapFromPO, feldInReferenz ? mapFromPOReference : null, """
                                builder.%s(%s != null ? new ArrayList<>(%s) : null);""".indent(8)
                                .formatted(feld.getNameUncapitalized(), getterDb, getterDb));
                    } else {
                        if (feld.getWertebereich() != null) {
                            String enumKey = feld.getWertebereich().getNameCapitalized() + "Enum";
                            imports.add(JavaImport.builder()
                                    .from(getWertebereichPackage(shortApplicationName, feld.getWertebereich()) + "."
                                            + feld.getWertebereich().getNameCapitalized() + "Enum")
                                    .build());
                            append(mapToPO, feldInReferenz ? mapToPOReference : null, """
                                    if(domainModell.get%s() == null) {
                                         dbModell.set%s(null);
                                    } else if (%s.UNBEKANNT.equals(domainModell.get%s())) {
                                         throw new IllegalStateException("Unbekannt darf nicht persistiert werden!");
                                    } else {
                                         dbModell.set%s(domainModell.get%s().name());
                                    }
                                    """.indent(8)
                                    .formatted(feld.getNameCapitalized(), feld.getNameCapitalized(), enumKey, feld.getNameCapitalized(), feld.getNameCapitalized(), feld.getNameCapitalized()));
                            append(mapFromPO, feldInReferenz ? mapFromPOReference : null, """
                                    builder.%s(%s.findByName(dbModell.get%s()));""".indent(8)
                                    .formatted(feld.getNameUncapitalized(), enumKey, feld.getNameCapitalized()));
                        } else {
                            append(mapToPO, feldInReferenz ? mapToPOReference : null, """
                                    dbModell.set%s(domainModell.get%s());""".indent(8)
                                    .formatted(feld.getNameCapitalized(), feld.getNameCapitalized()));
                            append(mapFromPO, feldInReferenz ? mapFromPOReference : null, """
                                    builder.%s(dbModell.get%s());""".indent(8)
                                    .formatted(feld.getNameUncapitalized(), feld.getNameCapitalized()));
                        }
                    }
                }
            }
        }

        String pkFelder = entitaet.getFelderMitVererbung(modell)
                .stream()
                .filter(e -> e.isPk() && !(e instanceof NumberIdEntitaetsfeld))
                .map(e -> "%s(domainModell.get%s())".formatted(e.getNameUncapitalized(), e.getNameCapitalized()))
                .collect(Collectors.joining("."));
        if (!pkFelder.isEmpty()) {
            Entitaet parentEntitaet = getHighestParentEntitaet(entitaet, modell);
            imports.add(JavaImport.builder()
                    .from(getPersistenceDomainPackage(shortApplicationName, parentEntitaet) + "."
                            + parentEntitaet.getNameCapitalized() + "PK")
                    .build());
            append(mapToPO, mapToPOReference, """
                    dbModell.setId(%sPK.builder().%s.build());""".indent(8)
                    .formatted(parentEntitaet.getNameCapitalized(), pkFelder));
        }

        if (mapKey.isEmpty()) {
            append(mapKey, """
                    pk.setId(domainModell.getId());""".indent(8));
        }

        appendLn(sb,
                """
                        
                        /**
                         * Generierter Code, bitte keine manuellen Änderungen vornehmen
                         *
                         */
                        public final class %sMapper {
                        
                            public static %sPO mapToPO(%s domainModell){
                                if (domainModell == null) { return null; }
                                %s
                        %s
                                return dbModell;
                            }
                        
                            public static %s mapFromPO(%sPO dbModell){
                                if (dbModell == null) { return null; }
                                %s
                        %s
                                return builder.build();
                            }
                        
                        """.formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                        entitaet.getNameCapitalized(), mapToPoStart, mapToPO.toString(),
                        entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                        mapFromPoStart, mapFromPO.toString()));
        if (entitaet.isEigenstaendig()) {
            appendLn(sb,
                    """
                                public static %sPO mapToPOReference(%sReference domainModell){
                                    if (domainModell == null) { return null; }
                                    %s
                            %s
                                    return dbModell;
                                }
                            
                                public static %sReference mapFromPOReference(%sPO dbModell){
                                    if (dbModell == null) { return null; }
                                    %s
                            %s
                                    return builder.build();
                                }
                            
                            """.formatted(entitaet.getNameCapitalized(),
                            entitaet.getNameCapitalized(), mapToPoStart, mapToPOReference.toString(),
                            entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                            mapFromPoStart, mapFromPOReference.toString()));
        }
        sb.append(zeilenMapper);
        appendCommonMappingMethods(sb, entitaet, modell, mapKey.toString(), imports, shortApplicationName);

        return imports;
    }

    public void append(final StringBuilder sb, final StringBuilder sb2, final String string) {
        String text = GeneratorUtils.normalizeLineBreaks(string);
        sb.append(text);
        if (sb2 != null) {
            sb2.append(text);
        }
    }

    private Set<JavaImport> variablenDefinitionErzeugenAbstrakt(final String shortApplicationName,
                                                                final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("org.hibernate.Hibernate")
                .from("org.hibernate.proxy.HibernateProxy")
                .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "PO")

                .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized())
                .from("java.util.List")
                .from("java.util.Optional")
                .from("java.util.stream.StreamSupport")
                .build());

        StringBuilder mapToPO = new StringBuilder();
        StringBuilder mapFromPO = new StringBuilder();
        mapFromPO.append("""
                %sPO abstractPO = dbModell;
                if (dbModell instanceof HibernateProxy) {
                  abstractPO = (%sPO) Hibernate.unproxy(dbModell);
                }
                """.indent(8).formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized()));
        for (Entitaet erbt : GeneratorUtils.werErbtVonDieserEntitaet(entitaet, modell)) {
            imports.add(JavaImport.builder()
                    .from(getPersistenceDomainPackage(shortApplicationName, erbt) + "."
                            + erbt.getNameCapitalized() + "PO")
                    .from(getEntitaetDomainPackage(shortApplicationName, erbt) + "."
                            + erbt.getNameCapitalized())
                    .build());
            getMapperImport(shortApplicationName, erbt, imports, entitaet);
            String mapToPoMethod = "mapToPO";
            String mapFromPoMethod = "mapFromPO";
            append(mapToPO, """
                    if (domainModell instanceof %s) {
                        return %sMapper.%s((%s)domainModell);
                    }
                    """.indent(8)
                    .formatted(erbt.getNameCapitalized(), erbt.getNameCapitalized(), mapToPoMethod,
                            erbt.getNameCapitalized()));
            append(mapFromPO, """
                    if (abstractPO instanceof %sPO) {
                        return %sMapper.%s((%sPO)abstractPO);
                    }
                    """.indent(8)
                    .formatted(erbt.getNameCapitalized(), erbt.getNameCapitalized(), mapFromPoMethod,
                            erbt.getNameCapitalized()));
        }

        StringBuilder mapKey = new StringBuilder();

        for (Entitaetsfeld feld : entitaet.getFelderMitVererbung(modell)) {
            if (feld.isPersistenz()) {
                if (feld.getZielEntitaet() == null) {
                    if (feld.isPk()) {
                        append(mapKey, """
                                pk.set%s(domainModell.get%s());""".indent(8)
                                .formatted(feld.getNameCapitalized(), feld.getNameCapitalized()));
                    }
                }
            }
        }
        if (mapKey.isEmpty()) {
            append(mapKey, """
                    pk.setId(domainModell.getId());""".indent(8));
        }

        appendLn(sb,
                """
                        
                        /**
                        * Generierter Code, bitte keine manuellen Änderungen vornehmen
                        *
                        */
                        public final class %sMapper {
                        
                            public static %sPO mapToPO(%s domainModell){
                                if (domainModell == null) { return null; }
                        %s
                                throw new UnsupportedOperationException("Der Typ von " + domainModell + " kann nicht gemapped werden.");
                            }
                        
                            public static %s mapFromPO(%sPO dbModell){
                                if (dbModell == null) { return null; }
                        %s
                                throw new UnsupportedOperationException("Der Typ von " + dbModell + " kann nicht gemapped werden.");
                            }
                        
                        """
                        .formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                entitaet.getNameCapitalized(), mapToPO.toString(),
                                entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                mapFromPO.toString()));
        if (entitaet.isEigenstaendig()) {
            appendLn(sb,
                    """
                                public static %sPO mapToPOReference(%sReference domainModell){
                                    if (domainModell == null) { return null; }
                            %s
                                    throw new UnsupportedOperationException("Der Typ von " + domainModell + " kann nicht gemapped werden.");
                                }
                            
                                public static %sReference mapFromPOReference(%sPO dbModell){
                                    if (dbModell == null) { return null; }
                            %s
                                    throw new UnsupportedOperationException("Der Typ von " + dbModell + " kann nicht gemapped werden.");
                                }
                            
                            """
                            .formatted(entitaet.getNameCapitalized(),
                                    entitaet.getNameCapitalized(), mapToPO.toString(),
                                    entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                    mapFromPO.toString()));
        }
        appendCommonMappingMethods(sb, entitaet, modell, mapKey.toString(), imports, shortApplicationName);

        return imports;
    }

    private void appendCommonMappingMethods(final StringBuilder sb, final Entitaet entitaet, DomainModel modell,
                                            final String mapKey, final Set<JavaImport> imports, final String shortApplicationName) {

        imports.add(JavaImport.builder()
                .from("java.util.stream.Collectors")
                .from("java.util.ArrayList")
                .build());
        if (entitaet.isEigenstaendig()) {
            Entitaet parentEntitat = getHighestParentEntitaet(entitaet, modell);

            getPkImport(shortApplicationName, parentEntitat, imports,
                    getPersistenceMapperPackage(shortApplicationName, parentEntitat));
            getModelReferenceImport(shortApplicationName, entitaet, imports);

            List<Entitaetsfeld> numberIDFields = entitaet.getFelder().stream().filter(f -> f instanceof NumberIdEntitaetsfeld).toList();

            if (numberIDFields.isEmpty()) {

                appendLn(sb,
                        """
                                    public static %sPK mapToPK(%sReference domainModell){
                                        %sPK pk = new %sPK();
                                %s
                                        return pk;
                                    }
                                
                                    public static List<%sPK> mapToPKs(final Iterable<? extends %sReference> domainModellList) {
                                        return StreamSupport.stream(domainModellList.spliterator(), false)
                                                .map(e -> mapToPK(e))
                                                .collect(Collectors.toList());
                                    }
                                
                                """
                                .formatted(parentEntitat.getNameCapitalized(), entitaet.getNameCapitalized(),
                                        parentEntitat.getNameCapitalized(), parentEntitat.getNameCapitalized(),
                                        mapKey, parentEntitat.getNameCapitalized(),
                                        entitaet.getNameCapitalized()));
            } else {

                appendLn(sb,
                        """
                                    public static Integer mapToPK(%sReference domainModell){
                                        return domainModell.getId();
                                    }
                                
                                    public static List<Integer> mapToPKs(final Iterable<? extends %sReference> domainModellList) {
                                        return StreamSupport.stream(domainModellList.spliterator(), false)
                                                .map(e -> mapToPK(e))
                                                .collect(Collectors.toList());
                                    }
                                
                                """
                                .formatted(entitaet.getNameCapitalized(),
                                        entitaet.getNameCapitalized()));
            }
            appendLn(sb,
                    """
                            
                                public static List<%sPO> mapToPOReference(List<? extends %sReference> domainModellList){
                                    return domainModellList == null
                                            ? new ArrayList<>()
                                            : domainModellList.stream().map(e->mapToPOReference(e)).collect(Collectors.toList());
                                }
                            
                                public static List<%sReference> mapFromPOReference(List<? extends %sPO> dbModellList){
                                    return dbModellList == null
                                            ? new ArrayList<>()
                                            : dbModellList.stream().map(e->mapFromPOReference(e)).collect(Collectors.toList());
                                }
                            
                                public static List<%sPO> mapToPOReference(final Iterable<? extends %sReference> domainModellList) {
                                    return domainModellList == null
                                            ? new ArrayList<>()
                                            : StreamSupport.stream(domainModellList.spliterator(), false)
                                        .map(e -> mapToPOReference(e))
                                        .collect(Collectors.toList());
                                }
                            
                                public static List<? extends %sReference> mapFromPOReference(final Iterable<? extends %sPO> dbModellList) {
                                    return dbModellList == null
                                            ? new ArrayList<>()
                                            : StreamSupport.stream(dbModellList.spliterator(), false)
                                        .map(e -> mapFromPOReference(e))
                                        .collect(Collectors.toList());
                                }
                            
                                public static Optional<%sReference> mapFromPOReference(final Optional<%sPO> dbModell) {
                                    return dbModell.map(e -> mapFromPOReference(e));
                                }
                            
                                public static Optional<%sPO> mapToPOReference(final Optional<%sReference> domainModell) {
                                    return domainModell.map(e -> mapToPOReference(e));
                                }
                            """
                            .formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                    entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                    entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                    entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                    entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                    entitaet.getNameCapitalized(), entitaet.getNameCapitalized()));
        }
        appendLn(sb,
                """
                        
                            public static List<%sPO> mapToPO(List<? extends %s> domainModellList){
                                return domainModellList == null
                                        ? new ArrayList<>()
                                        : domainModellList.stream().map(e->mapToPO(e)).collect(Collectors.toList());
                            }
                        
                            public static List<%s> mapFromPO(List<? extends %sPO> dbModellList){
                                return dbModellList == null
                                        ? new ArrayList<>()
                                        : dbModellList.stream().map(e->mapFromPO(e)).collect(Collectors.toList());
                            }
                        
                            public static List<%sPO> mapToPO(final Iterable<? extends %s> domainModellList) {
                                return domainModellList == null
                                        ? new ArrayList<>()
                                        : StreamSupport.stream(domainModellList.spliterator(), false)
                                    .map(e -> mapToPO(e))
                                    .collect(Collectors.toList());
                            }
                        
                            public static List<? extends %s> mapFromPO(final Iterable<? extends %sPO> dbModellList) {
                                return dbModellList == null
                                        ? new ArrayList<>()
                                        : StreamSupport.stream(dbModellList.spliterator(), false)
                                    .map(e -> mapFromPO(e))
                                    .collect(Collectors.toList());
                            }
                        
                            public static Optional<%s> mapFromPO(final Optional<%sPO> dbModell) {
                                return dbModell.map(e -> mapFromPO(e));
                            }
                        
                            public static Optional<%sPO> mapToPO(final Optional<%s> domainModell) {
                                return domainModell.map(e -> mapToPO(e));
                            }
                        }
                        """
                        .formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                entitaet.getNameCapitalized(), entitaet.getNameCapitalized()));
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
