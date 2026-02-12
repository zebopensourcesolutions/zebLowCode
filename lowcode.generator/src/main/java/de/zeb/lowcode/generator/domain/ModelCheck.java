package de.zeb.lowcode.generator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.zeb.lowcode.model.domain.*;
import org.apache.commons.lang3.StringUtils;

import de.zeb.lowcode.model.LowCodeModel;

public class ModelCheck {

    /**
     * https://www.ibm.com/docs/en/i/7.1?topic=reference-sql-limits
     */
    public static final int MAX_TABELLENNAME_ZEICHEN = 128;
    public static final int MAX_SPALTEN_ZEICHEN      = 128;

    public List<String> checkModel(final LowCodeModel lcm) {

        List<String> fehler = new ArrayList<>();
        // Set<String> verarbeiteteEntitaeten = new HashSet<>();
        // Set<String> verarbeiteteWertebereiche = new HashSet<>();

        for (Entitaet entitaet : lcm.getDomain()
                .getEntitaeten()) {
            dbTabellennamePruefen(fehler, entitaet);
            // compositeKeysPruefen(fehler, entitaet, lcm.getDomain());
            for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
                entitaetsfeldPruefen(lcm, fehler, entitaet, entitaetsfeld);
                dbSpaltePruefen(fehler, entitaetsfeld);
                wertebereichPruefen(fehler, entitaetsfeld);
                // listenNichtOptionalPruefen(fehler, entitaet, entitaetsfeld);
            }
            pkPruefen(lcm, fehler, entitaet);
            doppelteFelderPruefen(lcm, fehler, entitaet);
        }
        return fehler;

    }

    /**
     * @param lcm
     * @param fehler
     * @param entitaet
     */
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

    /**
     * @param lcm
     * @param fehler
     * @param entitaet
     */
    private void pkPruefen(final LowCodeModel lcm, final List<String> fehler,
            final Entitaet entitaet) {
        boolean hatPk = false;
        for (Entitaetsfeld feld : entitaet.getFelderMitVererbung(lcm.getDomain())) {
            if (feld.isPk()) {
                hatPk = true;
            }
        }
        if (!hatPk) {
            fehler.add("Fehlender Primärschlüssel in %s.%s".formatted(entitaet.getPaket(),
                    entitaet.getName()));
        }
    }

    /**
     * @param fehler
     * @param entitaet
     */
    private void dbTabellennamePruefen(final List<String> fehler, final Entitaet entitaet) {
        if (entitaet.getDbTabellenname() != null) {
            if (entitaet.getDbTabellenname()
                    .length() > MAX_TABELLENNAME_ZEICHEN) {
                fehler.add("Tabellenname darf nur %s Zeichen sein: "
                        .formatted(MAX_TABELLENNAME_ZEICHEN) + entitaet.getDbTabellenname());
            }
        }
    }

    /**
     * @param fehler
     * @param entitaetsfeld
     */
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

    /**
     * @param lcm
     * @param fehler
     * @param entitaet
     * @param entitaetsfeld
     */
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

}
