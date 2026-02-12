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

import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.GeneratedFile.GeneratedFileBuilder;
import de.zeb.lowcode.generator.model.JavaImport;

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

/**
 * @author dkleine Hier kein Lombok nutzen, wird nicht durch Delombok geschickt
 */
@SuppressWarnings("nls")
public class FiJavaPersistenceRepoGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {

        List<GeneratedFile> result = new ArrayList<>();

        result.addAll(repositoryPoErzeugen(lcm));
        result.addAll(repositoryEditierbarErzeugen(lcm));

        return result;
    }

    private List<GeneratedFile> repositoryEditierbarErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            if (entitaet.isEigenstaendig() && entitaet.isPersistenz()) {
                // Nur eigenständige nicht abstrakte Entitäten bekommen einen Service zum
                // Laden/Speichern
                repositoryGenErzeugen(modell, results, entitaet);
                repositoryServiceImplErzeugen(modell, results, entitaet);
            }
        }
        return results;
    }

    private void repositoryGenErzeugen(final LowCodeModel modell, final List<GeneratedFile> results, final Entitaet entitaet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLineRepository(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseGenErzeugen(modell, packageFolder, "repo",
                StringUtils.capitalize(entitaet.getName()) + "RepositoryGen.java");

        Set<JavaImport> imports = new HashSet<>(repositoryGenInhaltErzeugen(modell.getAnwendungskuerzel(), entitaet, sb, modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private void repositoryServiceImplErzeugen(final LowCodeModel modell, final List<GeneratedFile> results, final Entitaet entitaet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLineRepository(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseErzeugen(modell, packageFolder, "repo",
                StringUtils.capitalize(entitaet.getName()) + "Repository.java");

        Set<JavaImport> imports = new HashSet<>(repositoryImplEditierbarErzeugen(entitaet, sb));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private Set<JavaImport> repositoryGenInhaltErzeugen(final String shortApplicationName,
                                                        final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Set<JavaImport> imports = new HashSet<>();
        Entitaet parentEntitaet = getHighestParentEntitaet(entitaet, modell);
        imports.add(JavaImport.builder()
                .from(getEntitaetDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized())
                .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "PO")
                .from(getPersistenceMapperPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "Mapper")
                .build());
        if (entitaet.isEigenstaendig()) {
            getModelReferenceImport(shortApplicationName, entitaet, imports);
        } else {
            imports.add(JavaImport.builder()
                    .from(getPersistenceDomainPackage(shortApplicationName, parentEntitaet) + "."
                            + parentEntitaet.getNameCapitalized() + "PK")
                    .build());
        }
        appendLn(sb,
                """
                        import java.util.List;
                        import java.util.Optional;
                        import java.util.function.Function;

                        import org.springframework.data.domain.Example;
                        import org.springframework.data.domain.Page;
                        import org.springframework.data.domain.Pageable;
                        import org.springframework.data.domain.Sort;
                        import org.springframework.data.jpa.repository.JpaRepository;
                        import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

                        class DbElementRepositoryGen implements JpaRepository<DbElement, DbElementReference> {

                            private final DbElementPoRepository repo;

                            public DbElementRepositoryGen(final DbElementPoRepository repo) {
                                super();
                                this.repo = repo;
                            }

                            protected DbElementPoRepository getRepo() {
                                return this.repo;
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> List<S> saveAll(final Iterable<S> entities) {
                                return (List<S>) DbElementMapper
                                        .mapFromPO(this.repo.saveAll(DbElementMapper.mapToPO(entities)));
                            }
                            
                            public <S extends DbElement> List<DbElementPO> saveAllWithoutMapping(final Iterable<S> entities) {
                                return this.repo.saveAll(DbElementMapper.mapToPO(entities));
                            }

                            @Override
                            public List<DbElement> findAll() {
                                return DbElementMapper.mapFromPO(this.repo.findAll());
                            }

                            @Override
                            public List<DbElement> findAllById(final Iterable<DbElementReference> ids) {
                                return DbElementMapper.mapFromPO(this.repo.findAllById(DbElementMapper.mapToPKs(ids)));
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> S save(final S entity) {
                                return (S) DbElementMapper.mapFromPO(this.repo.save(DbElementMapper.mapToPO(entity)));
                            }
                            
                            public <S extends DbElement> DbElementPO saveWithoutMapping(final S entity) {
                                return this.repo.save(DbElementMapper.mapToPO(entity));
                            }

                            @Override
                            public Optional<DbElement> findById(final DbElementReference id) {
                                return DbElementMapper.mapFromPO(this.repo.findById(DbElementMapper.mapToPK(id)));
                            }

                            @Override
                            public boolean existsById(final DbElementReference id) {
                                return this.repo.existsById(DbElementMapper.mapToPK(id));
                            }

                            @Override
                            public long count() {
                                return this.repo.count();
                            }

                            @Override
                            public void deleteById(final DbElementReference id) {
                                this.repo.deleteById(DbElementMapper.mapToPK(id));
                            }

                            @Override
                            public void delete(final DbElement entity) {
                                this.repo.delete(DbElementMapper.mapToPO(entity));
                            }

                            @Override
                            public void deleteAllById(final Iterable<? extends DbElementReference> ids) {
                                this.repo.deleteAllById(DbElementMapper.mapToPKs(ids));
                            }

                            @Override
                            public void deleteAll(final Iterable<? extends DbElement> entities) {
                                this.repo.deleteAll(DbElementMapper.mapToPO(entities));
                            }

                            @Override
                            public void deleteAll() {
                                this.repo.deleteAll();
                            }

                            @Override
                            public List<DbElement> findAll(final Sort sort) {
                                return DbElementMapper.mapFromPO(this.repo.findAll(sort));
                            }

                            @Override
                            public Page<DbElement> findAll(final Pageable pageable) {
                                Page<DbElementPO> page = this.repo.findAll(pageable);
                                return page.map(e -> DbElementMapper.mapFromPO(e));
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> Optional<S> findOne(final Example<S> example) {
                                Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                        example.getMatcher());
                                return (Optional<S>) DbElementMapper.mapFromPO(this.repo.findOne(examplePO));
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> Page<S> findAll(final Example<S> example,
                                    final Pageable pageable) {
                                Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                        example.getMatcher());
                                Page<? extends DbElementPO> page = this.repo.findAll(examplePO, pageable);
                                Page<DbElement> mappedPage = page.map(e -> DbElementMapper.mapFromPO(e));
                                return (Page<S>) mappedPage;
                            }

                            @Override
                            public <S extends DbElement> long count(final Example<S> example) {
                                Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                        example.getMatcher());
                                return this.repo.count(examplePO);
                            }

                            @Override
                            public <S extends DbElement> boolean exists(final Example<S> example) {
                                Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                        example.getMatcher());
                                return this.repo.exists(examplePO);
                            }

                            @Override
                            // https://github.com/spring-projects/spring-data-commons/blob/main/src/main/asciidoc/query-by-example.adoc#fluent-api
                            public <S extends DbElement, R> R findBy(final Example<S> example,
                                    final Function<FetchableFluentQuery<S>, R> queryFunction) {
                                // Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                // example.getMatcher());
                                throw new UnsupportedOperationException(
                                        "Ist noch nicht klar wie wir die QueryFunction transformieren ohne die DB Klassen nach außen zu legen. Zudem gibt es keine Alternativ falls wir Dyns nutzen.");
                            }

                            @Override
                            public void flush() {
                                this.repo.flush();
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> S saveAndFlush(final S entity) {
                                return (S) DbElementMapper
                                        .mapFromPO(this.repo.saveAndFlush(DbElementMapper.mapToPO(entity)));
                            }
                            
                            public <S extends DbElement> DbElementPO saveAndFlushWithoutMapping(final S entity) {
                                return this.repo.saveAndFlush(DbElementMapper.mapToPO(entity));
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> List<S> saveAllAndFlush(final Iterable<S> entities) {
                                return (List<S>) DbElementMapper
                                        .mapFromPO(this.repo.saveAllAndFlush(DbElementMapper.mapToPO(entities)));
                            }
                            
                            public <S extends DbElement> List<DbElementPO> saveAllAndFlushWithoutMapping(final Iterable<S> entities) {
                                return this.repo.saveAllAndFlush(DbElementMapper.mapToPO(entities));
                            }

                            @Override
                            public void deleteAllInBatch(final Iterable<DbElement> entities) {
                                this.repo.deleteAllInBatch(DbElementMapper.mapToPO(entities));
                            }

                            @Override
                            public void deleteAllByIdInBatch(final Iterable<DbElementReference> ids) {
                                this.repo.deleteAllByIdInBatch(DbElementMapper.mapToPKs(ids));
                            }

                            @Override
                            public void deleteAllInBatch() {
                                this.repo.deleteAllInBatch();
                            }

                            @Override
                            public DbElement getReferenceById(final DbElementReference id) {
                                return DbElementMapper.mapFromPO(this.repo.getReferenceById(DbElementMapper.mapToPK(id)));
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> List<S> findAll(final Example<S> example) {
                                Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                        example.getMatcher());
                                return (List<S>) DbElementMapper.mapFromPO(this.repo.findAll(examplePO));
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public <S extends DbElement> List<S> findAll(final Example<S> example, final Sort sort) {
                                Example<DbElementPO> examplePO = Example.of(DbElementMapper.mapToPO(example.getProbe()),
                                        example.getMatcher());
                                return (List<S>) DbElementMapper.mapFromPO(this.repo.findAll(examplePO, sort));
                            }

                            @Override
                            public DbElement getOne(final DbElementReference id) {
                                return getReferenceById(id);
                            }

                            @Override
                            public DbElement getById(final DbElementReference id) {
                                return getReferenceById(id);
                            }

                        }

                                                        """
                        .replace("DbElementReference", entitaet.isEigenstaendig() ? entitaet.getNameCapitalized() + "Reference" : parentEntitaet.getNameCapitalized())
                        .replace("DbElement", entitaet.getNameCapitalized()));
        return imports;
    }

    private Set<JavaImport> repositoryImplEditierbarErzeugen(final Entitaet entitaet, final StringBuilder sb) {
        Set<JavaImport> imports = new HashSet<>();

        appendLn(sb, """

                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Service;

                @Service
                public class DbElementRepository extends DbElementRepositoryGen {

                    public DbElementRepository(@Autowired final DbElementPoRepository repo) {
                        super(repo);
                    }

                    // public DbElement findByName(final String name) {
                    //     return DbElementMapper.mapFromPO(getRepo().findByName(name));
                    // }
                }
                """.replace("DbElement", entitaet.getNameCapitalized()));
        return imports;
    }

    private List<GeneratedFile> repositoryPoErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            if (entitaet.isEigenstaendig() && entitaet.isPersistenz()) {
                // Nur eigenständige nicht abstrakte Entitäten bekommen einen Service zum
                // Laden/Speichern
                repositoryPoErzeugen(modell, results, entitaet);
            }
        }
        return results;
    }

    private void repositoryPoErzeugen(final LowCodeModel modell, final List<GeneratedFile> results, final Entitaet entitaet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLineRepository(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseErzeugen(modell, packageFolder, "repo",
                StringUtils.capitalize(entitaet.getName()) + "PoRepository.java");

        Set<JavaImport> imports = new HashSet<>(repositoryInhaltErzeugen(modell.getAnwendungskuerzel(), entitaet, sb,
                modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private Set<JavaImport> repositoryInhaltErzeugen(final String shortApplicationName,
                                                     final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Entitaet parentEntitaet = getHighestParentEntitaet(entitaet, modell);
        Set<JavaImport> imports = new HashSet<>();
        imports.add(JavaImport.builder()
                .from("org.springframework.data.jpa.repository.JpaRepository")
                .from("org.springframework.stereotype.Repository")
                .from(getPersistenceDomainPackage(shortApplicationName, entitaet) + "."
                        + entitaet.getNameCapitalized() + "PO")
                .from(getPersistenceDomainPackage(shortApplicationName, parentEntitaet) + "."
                        + parentEntitaet.getNameCapitalized() + "PK")
                .build());

        appendLn(sb,
                """

                        /**
                        * Die Methoden hier werden anhand des Namens zu DB Queries:
                        * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
                        */
                        @Repository
                        interface %sPoRepository extends JpaRepository<%sPO, %sPK>{
                            //Spring Data generiert die passenden Methodenimplementierungen
                        }
                        """
                        .formatted(entitaet.getNameCapitalized(), entitaet.getNameCapitalized(),
                                parentEntitaet.getNameCapitalized()));
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
