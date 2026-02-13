/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED
 * BY LAW. YOU WILL NOT REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION,
 * TRANSFER, DISTRIBUTION OR MODIFICATION OF PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR
 * WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 07.03.2023 - 16:18:21
 */
package de.zeb.lowcode.generator.domain;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.zeb.lowcode.generator.model.GeneratedFile;
import de.zeb.lowcode.generator.model.JavaImport;
import de.zeb.lowcode.model.domain.*;
import de.zeb.lowcode.model.ui.Maske;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("PMD.UselessParentheses")
public class GeneratorUtils {

    public static void runPrettier(final String basePath) {
        try {
            File parentPath = new File(basePath);
            ProcessBuilder builder = new ProcessBuilder();
            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase()
                    .startsWith("windows");
            if (isWindows) {
                builder.command("cmd.exe", "/c", "npx prettier --write **/*ts*");
            } else {
                builder.command("sh", "-c", "npx prettier --write **/*ts*");
            }
            builder.directory(parentPath);
            System.out.printf("Führe Prettier auf '%s' durch...%n", basePath);
            runProcess(builder);
            System.out.println("Prettier erfolgreich abgeschlossen");
        } catch (InterruptedException | IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static void runProcess(ProcessBuilder builder) throws IOException, InterruptedException {
        Process process = builder.start();
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        }
        int exitCode = process.waitFor();
        assert exitCode == 0;
    }

    /**
     * java -jar J:\source\zebLowCodeGitrepo\zeblowcode\lowcode.generator\lib\lombok-1.18.26.jar
     * delombok src -d J:\source\meinAggregator\Demo\demoDelombok -f pretty
     */
    public static void delombok(final String basePath, final String lombokJarWithPath,
                                final String targetPath) {
        try {
            File parentPath = new File(basePath);
            ProcessBuilder builder = new ProcessBuilder();
            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase()
                    .startsWith("windows");
            String command = "java -jar " + lombokJarWithPath + " delombok src -d " + targetPath
                    + " -f pretty";

            if (isWindows) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }
            builder.directory(parentPath);
            System.out.printf("Führe Delombok auf '%s' durch...%n", basePath);
            runProcess(builder);
        } catch (InterruptedException | IOException ex) {
            throw new IllegalStateException(ex);
        }
        nonNullNachDelombokEntfernenUndZeilenumbruecheKorrigieren(targetPath);
        System.out.println("Delombok erfolgreich abgeschlossen");

    }

    public static void nonNullNachDelombokEntfernenUndZeilenumbruecheKorrigieren(
            final String targetPath) {
        System.out.println("@NonNull entfernen...");
        Iterator<File> iterateFiles = FileUtils.iterateFiles(new File(targetPath),
                List.of("java")
                        .toArray(new String[1]),
                true);
        iterateFiles.forEachRemaining(e -> {
            try {
                String content = FileUtils.readFileToString(e, StandardCharsets.UTF_8);
                content = normalizeLineBreaks(content);

                content = content
                        .replace("import lombok.NonNull;" + "\r\n", "");
                content = content.replace("@NonNull", "");
                if (content.contains("import lombok.NonNull")) {
                    throw new IllegalStateException("In der Datei " + e + " existiert noch ein NunNull");
                }
                FileUtils.writeStringToFile(e, content, StandardCharsets.UTF_8);
                System.out.printf("Zeilenumbrüche korrigiert und @NonNull aus %s entfernt%n", e.getName());
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

    public static void googleCodeStyleAnwenden(
            final String targetPath) {
        System.out.println("Code formatieren...");
        Iterator<File> iterateFiles = FileUtils.iterateFiles(new File(targetPath),
                List.of("java")
                        .toArray(new String[1]),
                true);
        iterateFiles.forEachRemaining(e -> {
            try {
                String content = FileUtils.readFileToString(e, StandardCharsets.UTF_8);
                String formattedSource = new Formatter().formatSourceAndFixImports(content);
                FileUtils.writeStringToFile(e, formattedSource, StandardCharsets.UTF_8);
                System.out.printf("Code formatiert in %s%n", e.getName());
            } catch (FormatterException | IOException ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

    public static String normalizeLineBreaks(String content) {
        if (content != null) {
            // Zeilenumbrüche zwischen den Systemen muss Git regeln
            content = content.replaceAll("\r\n", "##WINDOWS_LINE_BREAK##");
            content = content.replaceAll("\n", "##UNIX_LINE_BREAK##");
            content = content.replaceAll("\r", "\r\n");
            content = content.replaceAll("##WINDOWS_LINE_BREAK##", "\r\n");
            content = content.replaceAll("##UNIX_LINE_BREAK##", "\r\n");
        }
        return content;
    }

    public static void deleteFolder(final String basePath) {
        File ordner = new File(basePath);
        if (ordner.exists()) {
            System.out.println("Lösche Ordner " + ordner.getAbsolutePath());
            FileUtils.deleteQuietly(ordner);
        }
    }

    public static void writeFiles(final Collection<GeneratedFile> files, final String basePath) {
        File lombokConfig = new File("myapp-modell-aggr/myapp-model/lombok.config");
        File lombokTarget = new File(basePath, "lombok.config");
        if (lombokTarget.exists()) {
            try {
                FileUtils.copyFile(lombokConfig, lombokTarget);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (GeneratedFile generatedFile : files) {
            try {
                File parentPath = new File(basePath + "/" + generatedFile.getFolder());
                System.out.println("Lege Ordner an: " + parentPath.getAbsolutePath());
                if (!parentPath.exists()) {
                    FileUtils.createParentDirectories(parentPath);
                }
                File target = new File(parentPath, generatedFile.getFile());
                if (!target.exists() || generatedFile.isGenerated()) {
                    System.out.println(
                            "Schreibe nach " + target.toPath() + " " + generatedFile.getContent()
                                    .length() + " Zeichen.");
                    FileUtils.writeStringToFile(target,
                            normalizeLineBreaks(generatedFile.getContent()),
                            StandardCharsets.UTF_8);
                } else {
                    System.err.println(
                            "Datei " + target.toPath() + " existiert und wird nicht aktualisiert.");
                }
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    public static String getJavaType(final Entitaetsfeld feld, final Set<JavaImport> imports,
                                     final DomainModel domain, boolean transientNonSerializableTypes) {
        String typ;
        if (feld.getWertebereich() != null) {
            typ = feld.getWertebereich()
                    .getNameCapitalized() + "Enum";
        } else if (feld.getZielEntitaet() != null) {
            typ = domain.getEntitaetByReference(feld.getZielEntitaet())
                    .getNameCapitalized();
        } else {
            switch (feld.getDatenTyp()) {
                case ID:
                    imports.add(JavaImport.builder()
                            .from("java.util.UUID")
                            .build());
                    typ = "UUID";
                    break;
                case TEXT_JN:
                case BOOLEAN:
                    typ = "Boolean";
                    break;
                case DATUM:
                    imports.add(JavaImport.builder()
                            .from("java.time.LocalDate")
                            .build());
                    typ = "LocalDate";
                    break;
                case ZAHL:
                case PROZENTZAHL:
                case GELD_BETRAG:
                    imports.add(JavaImport.builder()
                            .from("java.math.BigDecimal")
                            .build());
                    typ = "BigDecimal";
                    break;
                case VERSION:
                case GANZZAHL:
                case BASISPUNKT:
                    typ = "Integer";
                    break;
                case GANZZAHL_ERWEITERT:
                    typ = "Long";
                    break;
                case ERSTELLT_VON:
                case MODIFIZIERT_VON:
                case URL:
                case TEXT:
                    typ = "String";
                    break;
                case ZEITPUNKT_LETZTE_AENDERUNG:
                case ZEITPUNKT_ERSTELLUNG:
                case ZEITSTEMPEL:
                    imports.add(JavaImport.builder()
                            .from("java.time.LocalDateTime")
                            .build());
                    typ = "LocalDateTime";
                    break;
                case BINARY:
                    imports.add(JavaImport.builder()
                            .from("java.sql.Blob")
                            .build());
                    if (transientNonSerializableTypes) {
                        typ = "transient Blob";
                    } else {
                        typ = "Blob";
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Der Typ " + feld.getDatenTyp() + " wurde noch nicht implementiert.");
            }
        }
        if (feld.isAlsListe()) {
            imports.add(JavaImport.builder()
                    .from("java.util.List")
                    .build());
            return "List<" + typ + ">";
        }
        return typ;
    }

    public static String getJavaGetterMethodName(final Entitaetsfeld feld) {
        if (feld.getDatenTyp() == Datentyp.BOOLEAN) {
            return "is" + feld.getNameCapitalized();
        }
        return "get" + feld.getNameCapitalized();
    }

    public static String getTypescriptType(final Wertebereich wb) {
        return wb.getNameCapitalized() + "Enum";
    }

    public static String getTypescriptType(final Entitaetsfeld feld) {
        if (feld.getWertebereich() != null) {
            return StringUtils.capitalize(feld.getName()) + "Enum";
        }
        return switch (feld.getDatenTyp()) {
            case GELD_BETRAG, DATUM, BASISPUNKT, URL, PROZENTZAHL, TEXT, ID, ZAHL, ZEITSTEMPEL,
                 ZEITPUNKT_LETZTE_AENDERUNG, ZEITPUNKT_ERSTELLUNG, MODIFIZIERT_VON, ERSTELLT_VON, BINARY -> "string";
            case BOOLEAN, TEXT_JN -> "boolean";
            case GANZZAHL, GANZZAHL_ERWEITERT, VERSION -> "number";
            case ENTITAET -> feld.getName();
        };
    }

    /**
     * Gibt true zurück wenn die Entität der Maske nicht schon im Domänenmodell enthalten ist.
     */
    public static boolean entityForMaskIsPartOfDomain(final Maske<?> maske,
                                                      final DomainModel domain) {
        Entitaet entitaetByReference = null;
        if (maske.getEntitaet()
                .getName() != null && maske.getEntitaet()
                .getPaket() != null) {
            entitaetByReference = domain.getEntitaetByReference(Entitaetreferenz.builder()
                    .name(maske.getEntitaet()
                            .getName())
                    .paket(maske.getEntitaet()
                            .getPaket())
                    .build());
        }
        return entitaetByReference != null;
    }

    public static boolean entityIsPartOfDomain(final Entitaetreferenz entitaetreferenz,
                                               final DomainModel domain) {
        Entitaet entitaetByReference = null;
        if (entitaetreferenz != null && entitaetreferenz
                .getPaket() != null) {
            entitaetByReference = domain.getEntitaetByReference(Entitaetreferenz.builder()
                    .name(entitaetreferenz
                            .getName())
                    .paket(entitaetreferenz
                            .getPaket())
                    .build());
        }
        return entitaetByReference != null;
    }


    public static List<Entitaet> werErbtVonDieserEntitaet(final Entitaet entitaet,
                                                          final DomainModel modell) {
        List<Entitaet> result = new ArrayList<>();
        for (Entitaet e : modell.getEntitaeten()) {
            if ((e.getErbtVon() != null) && (Strings.CS.equals(e.getErbtVon()
                    .getName(), entitaet.getName()) && Strings.CS.equals(
                    e.getErbtVon()
                            .getPaket(),
                    entitaet.getPaket()))) {
                result.add(e);
                result.addAll(werErbtVonDieserEntitaet(e, modell));
            }
        }
        return result;
    }

}
