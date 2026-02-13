package de.zeb.lowcode.generator.domain;

import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"PMD.CollapsibleIfStatements", "PMD.UselessParentheses"})
public class ModelCheck {

    /**
     * <a href="https://www.ibm.com/docs/en/i/7.1?topic=reference-sql-limits">Doku</a>
     */
    public static final int MAX_TABELLENNAME_ZEICHEN = 128;
    public static final int MAX_SPALTEN_ZEICHEN = 128;

    public List<String> checkModel(final LowCodeModel lcm) {

        List<String> fehler = new ArrayList<>();

        if (lcm != null) {
            doppelteEntitaetenPruefen(lcm, fehler);
            for (Entitaet entitaet : lcm.getDomain()
                    .getEntitaeten()) {
                dbTabellennamePruefen(fehler, entitaet);
                for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
                    entitaetsfeldPruefen(lcm, fehler, entitaet, entitaetsfeld);
                    dbSpaltePruefen(fehler, entitaetsfeld);
                    wertebereichPruefen(fehler, entitaetsfeld);
                }
                pkPruefen(lcm, fehler, entitaet);
                doppelteFelderPruefen(lcm, fehler, entitaet);
            }
        } else {
            fehler.add("LowCodeModel ist null");
        }
        return fehler;

    }

    private void doppelteFelderPruefen(final LowCodeModel lcm, final List<String> fehler,
                                       final Entitaet entitaet) {
        List<EntitaetsfeldMitEntitaet> felderMitVererbung = getFelderMitVererbung(entitaet,
                lcm.getDomain());
        for (EntitaetsfeldMitEntitaet feld : felderMitVererbung) {
            List<EntitaetsfeldMitEntitaet> felder = getFeld(felderMitVererbung, feld.feld());
            if (felder.size() > 1) {
                String namen = felder.stream()
                        .map(e -> e.entitaet()
                                .getName())
                        .collect(Collectors.joining(", "));
                String meldung = "Das Feld %s kommt mehrfach in der Hierarchie von %s vor: %s"
                        .formatted(feld.feld()
                                .getName(), entitaet.getName(), namen);
                if (!fehler.contains(meldung)) {
                    fehler.add(meldung);
                }
            }
        }
    }

    private List<EntitaetsfeldMitEntitaet> getFeld(
            final List<EntitaetsfeldMitEntitaet> felderMitVererbung, final Entitaetsfeld feld) {
        List<EntitaetsfeldMitEntitaet> result = new ArrayList<>();
        for (EntitaetsfeldMitEntitaet entitaetsfeldMitEntitaet : felderMitVererbung) {
            if (entitaetsfeldMitEntitaet.feld()
                    .getName()
                    .equalsIgnoreCase(feld.getName())) {
                result.add(entitaetsfeldMitEntitaet);
            }
        }
        return result;
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

    private void pkPruefen(final LowCodeModel lcm, final List<String> fehler,
                           final Entitaet entitaet) {
        boolean hatPk = false;
        for (Entitaetsfeld feld : entitaet.getFelderMitVererbung(lcm.getDomain())) {
            if (feld.isPk()) {
                hatPk = true;
                break;
            }
        }
        if (!hatPk) {
            fehler.add("Fehlender Primärschlüssel in %s.%s".formatted(entitaet.getPaket(),
                    entitaet.getName()));
        }
    }

    private void dbTabellennamePruefen(final List<String> fehler, final Entitaet entitaet) {
        if (entitaet.getDbTabellenname() != null) {
            if (entitaet.getDbTabellenname()
                    .length() > MAX_TABELLENNAME_ZEICHEN) {
                fehler.add("Tabellenname darf nur %s Zeichen sein: "
                        .formatted(MAX_TABELLENNAME_ZEICHEN) + entitaet.getDbTabellenname());
            }
        }
    }

    private void dbSpaltePruefen(final List<String> fehler, final Entitaetsfeld entitaetsfeld) {
        if (entitaetsfeld.getDbSpaltenname() != null) {
            if (entitaetsfeld.getDbSpaltenname()
                    .length() > MAX_SPALTEN_ZEICHEN) {
                fehler.add("Spaltenname darf nur %s Zeichen sein: ".formatted(MAX_SPALTEN_ZEICHEN)
                        + entitaetsfeld.getDbSpaltenname());
            }
        }
    }

    private void wertebereichPruefen(final List<String> fehler, final Entitaetsfeld entitaetsfeld) {
        if (entitaetsfeld.getWertebereich() != null) {
            Wertebereich wertebereich = entitaetsfeld.getWertebereich();
            for (WertebereichEintrag wertebereichEintrag : wertebereich.getEintraege()) {
                if (!wertebereichEintrag.getName()
                        .matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
                    fehler.add(
                            "Im Wertebereich %s darf der Name der Option %s nur aus Buchstaben, Unterstrichen und Zahlen bestehen: "
                                    .formatted(wertebereich.getName(),
                                            wertebereichEintrag.getName())
                                    + entitaetsfeld.getDbSpaltenname());
                }
            }
        }
    }

    private void entitaetsfeldPruefen(final LowCodeModel lcm, final List<String> fehler,
                                      final Entitaet entitaet, final Entitaetsfeld entitaetsfeld) {
        if ((entitaetsfeld.getZielEntitaet() != null)) {
            // Prüfen ob Entiät im Domänenmodell hängt
            Entitaet entitaetByReference = lcm.getDomain()
                    .getEntitaetByReference(entitaetsfeld.getZielEntitaet());
            if (entitaetByReference == null) {
                fehler.add("Zielentität muss im Domänenmodell hängen "
                        + entitaetsfeld.getZielEntitaet());
            } else {

                // Selbstreferenz prüfen
                if (getEntitaetClassName(entitaetByReference)
                        .equals(getEntitaetClassName(entitaet))) {
                    if (!entitaetsfeld.isOptional()) {
                        fehler.add("Optional muss true sein: " + entitaetsfeld);
                    }
                }
            }
        }
    }

    private String getEntitaetClassName(final Entitaet entitaet) {
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            return StringUtils.capitalize(entitaet.getPaket()) + "::"
                    + entitaet.getNameCapitalized();
        }
        return entitaet.getName();
    }

    private void doppelteEntitaetenPruefen(final LowCodeModel lcm, final List<String> fehler) {
        Set<String> namen = new HashSet<>();
        Set<String> duplikate = new HashSet<>();
        for (Entitaet entitaet : lcm.getDomain().getEntitaeten()) {
            String key = buildEntitaetKey(entitaet);
            if (!namen.add(key)) {
                duplikate.add(key);
            }
        }
        for (String dup : duplikate) {
            fehler.add("Entität mehrfach vorhanden: " + dup);
        }
    }

    private String buildEntitaetKey(final Entitaet entitaet) {
        String pkg = entitaet.getPaket();
        String name = entitaet.getName();
        String full = (pkg == null || pkg.isBlank() ? "" : pkg + "::") + name;
        return full.toLowerCase(Locale.ROOT);
    }
}
