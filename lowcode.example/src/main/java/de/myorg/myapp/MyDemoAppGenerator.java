package de.myorg.myapp;



import de.myorg.myapp.parts.Bericht;
import de.myorg.myapp.parts.Protokollierung;
import de.myorg.myapp.parts.Querschnitt;
import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.generator.domain.ModelCheck;
import de.zeb.lowcode.generator.domain.maske.java.*;
import de.zeb.lowcode.generator.domain.maske.typescript.FiTypescriptDomainModelGenerator;
import de.zeb.lowcode.generator.plantuml.PlantumlDomainGenerator;
import de.zeb.lowcode.generator.plantuml.PlantumlUtils;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.DomainModel.DomainModelBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
public class MyDemoAppGenerator {


    private static final List<ModelPart> modellTeile = Arrays.asList(
            new Querschnitt(),
            new Bericht(),
            new Protokollierung()
    );

    public static void main(final String[] args) {
        LowCodeModel lcm = getLowcodeModel();

        List<String> checkModel = new ModelCheck().checkModel(lcm);
        if (!checkModel.isEmpty()) {
            for (String fehler : checkModel) {
                System.err.println(fehler);
            }
            throw new IllegalStateException("Fehler im Modell, es wird nichts generiert");
        }


        PlantumlUtils.ausgabeAlsPlantUml(new PlantumlDomainGenerator().generateModel(lcm), getHome() + File.separator + "domainModell");

        GeneratorUtils.deleteFolder(getTypescriptZiel() + File.separator + "domain" + File.separator + "generated");
        GeneratorUtils.deleteFolder(getJavaZiel() + File.separator + "src");
        GeneratorUtils.deleteFolder(getJavaPersistenceZiel() + File.separator + "src");

        GeneratorUtils.writeFiles(new FiTypescriptDomainModelGenerator().prepare(lcm),
                getTypescriptZiel());
        GeneratorUtils.writeFiles(new FiJavaDomainGenerator().prepare(lcm),
                getJavaPersistenceZiel());
        GeneratorUtils.writeFiles(new FiJavaPersistenceGenerator().prepare(lcm),
                getJavaPersistenceZiel());
        GeneratorUtils.writeFiles(new FiJavaPersistencePkGenerator().prepare(lcm),
                getJavaPersistenceZiel());
        GeneratorUtils.writeFiles(new FiJavaPersistenceListenerGenerator().prepare(lcm),
                getJavaPersistenceZiel());
        GeneratorUtils.writeFiles(new FiJavaPersistenceRepoGenerator().prepare(lcm),
                getJavaPersistenceZiel());
        GeneratorUtils.writeFiles(new FiJavaPersistenceMappingGenerator().prepare(lcm),
                getJavaPersistenceZiel());
        GeneratorUtils.writeFiles(new FiJavaDomainTabelleModellGenerator().prepare(lcm),
                getJavaPersistenceZiel());

        GeneratorUtils.writeFiles(new FiJavaTabelleModellGenerator().prepare(lcm),
                getJavaZiel());
        GeneratorUtils.writeFiles(new FiJavaMaskenModellGenerator().prepare(lcm),
                getJavaZiel());
        GeneratorUtils.writeFiles(new FiJavaMaskeGenerator().prepare(lcm), getJavaZiel());
        GeneratorUtils.writeFiles(new FiJavaTabellenserviceGenerator().prepare(lcm),
                getJavaZiel());

        /*
         * Fall Lombok nicht genutzt wird, muss delombok ausgeführt werden, damit die generierten Dateien kompiliert werden können
         * if (!isWindows) { runCommand("mvn", "delombok:delombok"); } else { runCommand("cmd.exe", "/c", "mvn delombok:delombok"); }
         */

    }

    public static LowCodeModel getLowcodeModel() {
        DomainModelBuilder domainModelBuilder = DomainModel.builder();
        modellTeile.forEach(e -> e.buildDomainModel(domainModelBuilder));
        return LowCodeModel.builder()
                .anwendungskuerzel("myapp")
                .domain(domainModelBuilder.build())
                .build();
    }

    public static String getHome() {
        try {
            Path homePath = Path.of("./lowcode.example/target/demo");
            Files.createDirectories(homePath);
            return homePath.toFile().getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getTypescriptZiel() {
        return getHome() + File.separator + "myapp-aggr" + File.separator + "myapp-cpl-web"
                + File.separator + "src" + File.separator + "main" + File.separator + "typescript"
                + File.separator + "myapp" + File.separator + "src";
    }

    public static String getJavaPersistenceZiel() {
        return getHome() + File.separator + "myapp-modell-aggr" + File.separator
                + "myapp-persistenz-MitLombok";
    }

    public static String getJavaZiel() {
        return getHome() + File.separator + File.separator + "myapp-modell-aggr"
                + File.separator + "myapp-ui-MitLombok";
    }


}
