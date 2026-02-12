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
import de.zeb.lowcode.generator.persistenz.AuditEntitaetsfeld;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dkleine Hier kein Lombok nutzen, wird nicht durch Delombok geschickt
 */
@SuppressWarnings("nls")
public class FiJavaPersistenceListenerGenerator extends AbstractJavaGenerator {

    @Override
    public List<GeneratedFile> prepare(final LowCodeModel lcm) {

        List<GeneratedFile> result = new ArrayList<>();

        result.addAll(persistenceListernerErzeugen(lcm));

        return result;
    }

    private List<GeneratedFile> persistenceListernerErzeugen(final LowCodeModel modell) {
        List<GeneratedFile> results = new ArrayList<>();
        List<String> verarbeitet = new ArrayList<>();

        for (Entitaet entitaet : modell.getDomain()
                .getEntitaeten()) {
            // Nur eigenständige nicht abstrakte Entitäten bekommen einen Service zum
            // Laden/Speichern
            if (entitaet.isPersistenz()) {
                listenerErzeugen(modell, results, entitaet, verarbeitet);

            }
        }
        return results;
    }

    private void listenerErzeugen(final LowCodeModel modell, final List<GeneratedFile> results,
                                  final Entitaet entitaet, final List<String> verarbeitet) {
        StringBuilder sb = new StringBuilder();
        String packageLine = getPackageLinePersistence(modell.getAnwendungskuerzel(), entitaet);
        String packageFolder = "";
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            packageFolder = entitaet.getPaket()
                    .toLowerCase() + "/";
        }
        GeneratedFileBuilder generatedFileBuilder = klasseErzeugen(modell, packageFolder, "db",
                StringUtils.capitalize(entitaet.getName()) + "POListener.java");

        Set<JavaImport> imports = new HashSet<>();
        imports.addAll(
                inhaltErzeugen(modell.getAnwendungskuerzel(), entitaet, sb, modell.getDomain()));

        String content = sb.toString();
        String importStatements = javaImportStatementsErzeugen(imports);

        results.add(generatedFileBuilder.content(packageLine + importStatements + content)
                .build());

    }

    private Set<JavaImport> inhaltErzeugen(final String shortApplicationName,
                                           final Entitaet entitaet, final StringBuilder sb, final DomainModel modell) {
        Set<JavaImport> imports = new HashSet<>();

        // Nur Entitäten mit Audit-Feld protokollieren
        if (entitaet.getFelder().stream().filter(f -> f instanceof AuditEntitaetsfeld).toList().size() == 0) {

            appendLn(sb,
                    """
                            import jakarta.persistence.PrePersist;
                            import jakarta.persistence.PreUpdate;
                            import example.myapp.MYAPPPOListener;
                                                        
                            /**
                             * @PrePersist Executed before the entity manager persist operation is actually executed or cascaded. This call is synchronous with the persist operation.
                             * @PreRemove Executed before the entity manager remove operation is actually executed or cascaded. This call is synchronous with the remove operation.
                             * @PostPersist Executed after the entity manager persist operation is actually executed or cascaded. This call is invoked after the database INSERT is executed.
                             * @PostRemove Executed after the entity manager remove operation is actually executed or cascaded. This call is synchronous with the remove operation.
                             * @PreUpdate Executed before the database UPDATE operation.
                             * @PostUpdate Executed after the database UPDATE operation.
                             * @PostLoad Executed after an entity has been loaded into the current persistence context or an entity has been refreshed.
                             */
                            public class MyappBasisElementPOListener extends MYAPPPOListener {
                                                        
                                @PreUpdate
                                public void update(MyappBasisElementPO element) {
                                    super.update(element);
                                }
                                                        
                                @PrePersist
                                public void createForTheFirstTime(MyappBasisElementPO element) {
                                    super.createForTheFirstTime(element);
                                }

                            }
                                            """
                            .replace("MyappBasisElementPO", entitaet.getNameCapitalized() + "PO"));

        } else {

            appendLn(sb,
                    """
                            import jakarta.persistence.PostLoad;
                            import jakarta.persistence.PostPersist;
                            import jakarta.persistence.PostRemove;
                            import jakarta.persistence.PostUpdate;
                            import jakarta.persistence.PreUpdate;
                            import jakarta.persistence.PrePersist;
                            import org.springframework.beans.factory.annotation.Autowired;
                            import org.springframework.stereotype.Component;
                            import example.myapp.MYAPPPOListener;
                            import example.myapp.persistenz.protokollierung.ProtokollService;

                            /**
                             * @PrePersist Executed before the entity manager persist operation is actually executed or cascaded. This call is synchronous with the persist operation.
                             * @PreRemove Executed before the entity manager remove operation is actually executed or cascaded. This call is synchronous with the remove operation.
                             * @PostPersist Executed after the entity manager persist operation is actually executed or cascaded. This call is invoked after the database INSERT is executed.
                             * @PostRemove Executed after the entity manager remove operation is actually executed or cascaded. This call is synchronous with the remove operation.
                             * @PreUpdate Executed before the database UPDATE operation.
                             * @PostUpdate Executed after the database UPDATE operation.
                             * @PostLoad Executed after an entity has been loaded into the current persistence context or an entity has been refreshed.
                             */
                            @Component 
                            public class MyappBasisElementPOListener extends MYAPPPOListener {

                                @Autowired
                                public MyappBasisElementPOListener(ProtokollService protokollService) {
                                    super(protokollService);
                                }
                                
                                @PreUpdate
                                public void update(MyappBasisElementPO element) {
                                    super.update(element);
                                }
                                                        
                                @PrePersist
                                public void createForTheFirstTime(MyappBasisElementPO element) {
                                    super.createForTheFirstTime(element);
                                }
                                                    
                                @PostLoad
                                public void postLoad(MyappBasisElementPO element) {
                                    element.setOldValues(super.postLoad(element));
                                }
                                                    
                                @PostPersist
                                public void postPersist(MyappBasisElementPO element) {
                                    super.postPersist(element);
                                }
                                                    
                                @PostUpdate
                                public void postUpdate(MyappBasisElementPO element) {
                                    super.postUpdate(element);
                                }
                                                    
                                @PostRemove
                                public void postRemove(MyappBasisElementPO element) {
                                    super.postRemove(element);
                                }

                            }
                                            """
                            .replace("MyappBasisElementPO", entitaet.getNameCapitalized() + "PO"));
        }
        return imports;
    }

}
