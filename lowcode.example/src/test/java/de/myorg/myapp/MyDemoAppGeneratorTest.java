package de.myorg.myapp;

import de.zeb.lowcode.generator.domain.ModelCheck;
import de.zeb.lowcode.generator.plantuml.PlantumlDomainGenerator;
import de.zeb.lowcode.generator.plantuml.PlantumlUtils;
import de.zeb.lowcode.model.LowCodeModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MyDemoAppGeneratorTest {

    @Test
    void modelIstValid() {
        LowCodeModel model = MyDemoAppGenerator.getLowcodeModel();
        List<String> fehler = new ModelCheck().checkModel(model);
        assertTrue(fehler.isEmpty(), () -> "ModelCheck-Fehler: " + fehler);
    }

    @Test
    void plantumlWirdErzeugt() throws IOException {
        LowCodeModel model = MyDemoAppGenerator.getLowcodeModel();
        String plantuml = new PlantumlDomainGenerator().generateModel(model);

        Path tmpDir = Files.createTempDirectory("plantuml-test");
        Path ziel = tmpDir.resolve("domainModell");
        PlantumlUtils.ausgabeAlsPlantUml(plantuml, ziel.toString());

        boolean hatDatei;
        try (var stream = Files.list(tmpDir)) {
            hatDatei = stream.findAny().isPresent();
        }
        assertTrue(hatDatei, () -> "Keine PlantUML-Datei in " + tmpDir);
    }
}

