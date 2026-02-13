package de.zeb.lowcode.generator.plantuml;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.FileOutputStream;
import java.io.IOException;

public class PlantumlUtils {

    public static void ausgabeAlsPlantUml(final String modell, final String zieldateiname) {
        final SourceStringReader reader = new SourceStringReader(modell);
        try (FileOutputStream os = new FileOutputStream(zieldateiname + ".svg")) {
            reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
