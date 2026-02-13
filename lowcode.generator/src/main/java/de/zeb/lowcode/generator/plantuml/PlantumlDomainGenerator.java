package de.zeb.lowcode.generator.plantuml;

import de.zeb.lowcode.generator.domain.GeneratorUtils;
import de.zeb.lowcode.model.LowCodeModel;
import de.zeb.lowcode.model.domain.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class PlantumlDomainGenerator extends AbstractPlantumlGenerator {

    private static final int MAX_ENUM_EINTRAEGE = 8;

    @Override
    public String generateModel(final LowCodeModel lcm) {
        final StringBuilder sb = new StringBuilder();
        createPlantumlGeader(sb);

        Set<String> verarbeiteteEntitaeten = new HashSet<>();
        Set<String> verarbeiteteWertebereiche = new HashSet<>();
        for (Entitaet entitaet : lcm.getDomain()
                .getEntitaeten()) {
            entitaetAusgeben(entitaet, sb, verarbeiteteEntitaeten, lcm.getDomain());
            if (entitaet.getErbtVon() != null) {
                entitaetAusgeben(lcm.getDomain()
                                .getEntitaetByReference(entitaet.getErbtVon()), sb, verarbeiteteEntitaeten,
                        lcm.getDomain());
                erbtVonBeziehung(entitaet, lcm.getDomain()
                        .getEntitaetByReference(entitaet.getErbtVon()), sb);
            }
            wertebereicheGenerieren(sb, verarbeiteteWertebereiche, entitaet);
            for (Entitaetsfeld entitaetsfeld : entitaet.getAlleFelderMitZielentitaeten()) {
                if (entitaetsfeld.getZielEntitaet() != null) {
                    Entitaet ziel = lcm.getDomain()
                            .getEntitaetByReference(entitaetsfeld.getZielEntitaet());
                    if (ziel.isEigenstaendig()
                            || !StringUtils.equals(ziel.getPaket(), entitaet.getPaket())) {
                        // Wenn das Ziel in einem anderen Paket liegt, dann soll keine Komposition
                        // vorliegen, da sonst die Abhängigkeit zwischen den Paketen zu groß wäre
                        aggregationBeziehung(entitaetsfeld, entitaet, sb, lcm.getDomain());
                    } else {
                        kompositionBeziehung(entitaetsfeld, entitaet, sb, lcm.getDomain());
                    }
                }
            }
        }

        sb.append("@enduml");
        return sb.toString();
    }

    /**
     * @param sb
     * @param verarbeiteteWertebereiche
     * @param entitaet
     */
    private void wertebereicheGenerieren(final StringBuilder sb,
                                         final Set<String> verarbeiteteWertebereiche, final Entitaet entitaet) {
        for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
            if (entitaetsfeld.getWertebereich() != null) {
                wertebereichEnumErzeugen(entitaetsfeld.getWertebereich(), sb,
                        verarbeiteteWertebereiche);
            }
        }
    }

    private void wertebereichEnumErzeugen(final Wertebereich wertebereich, final StringBuilder sb,
                                          final Set<String> verarbeiteteWertebereiche) {
        if (!verarbeiteteWertebereiche.contains(getWertebereichClassName(wertebereich))) {
            sb.append("""
                    
                    class %s{
                    """.formatted(getWertebereichClassName(wertebereich)));

            int counter = 0;
            for (WertebereichEintrag eintrag : wertebereich.getEintraege()) {
                if (counter++ < MAX_ENUM_EINTRAEGE) {
                    sb.append("""
                                %s %s
                            """.formatted(eintrag.getName()
                            .toUpperCase(), eintrag.getLabel()));
                }
            }
            if (counter > MAX_ENUM_EINTRAEGE) {
                sb.append("""
                            ... %s weitere Einträge
                        """.formatted(counter - MAX_ENUM_EINTRAEGE));
            }
            sb.append("""
                    }
                    
                    """);
            verarbeiteteWertebereiche.add(getWertebereichClassName(wertebereich));
        }
    }

    private void erbtVonBeziehung(final Entitaet entitaet, final Entitaet parent,
                                  final StringBuilder sb) {
        sb.append("""
                %s <|-- %s
                """.formatted(getEntitaetClassName(parent), getEntitaetClassName(entitaet)));
    }

    private void kompositionBeziehung(final Entitaetsfeld feld, final Entitaet parent,
                                      final StringBuilder sb, final DomainModel domain) {
        String kardinalitaet = getKardinaltitaet(feld);
        sb.append("""
                %s *-- %s%s
                """.formatted(getEntitaetClassName(parent), kardinalitaet,
                getEntitaetClassName(domain.getEntitaetByReference(feld.getZielEntitaet()))));
    }

    private String getKardinaltitaet(final Entitaetsfeld feld) {
        String kardinalitaet = "";
        if (feld.isOptional() && feld.isAlsListe()) {
            kardinalitaet = "\"0..*\" ";
        } else if (feld.isOptional() && !feld.isAlsListe()) {
            kardinalitaet = "\"0..1\" ";
        } else if (!feld.isOptional() && !feld.isAlsListe()) {
            kardinalitaet = "\"1\" ";
        } else if (!feld.isOptional() && feld.isAlsListe()) {
            kardinalitaet = "\"1..*\" ";
        }
        return kardinalitaet;
    }

    private void aggregationBeziehung(final Entitaetsfeld feld, final Entitaet parent,
                                      final StringBuilder sb, final DomainModel domain) {
        String kardinalitaet = getKardinaltitaet(feld);
        sb.append("""
                %s o-- %s%s
                """.formatted(getEntitaetClassName(parent), kardinalitaet,
                getEntitaetClassName(domain.getEntitaetByReference(feld.getZielEntitaet()))));
    }

    private String getEntitaetClassName(final Entitaet entitaet) {
        if (!StringUtils.isEmpty(entitaet.getPaket())) {
            return StringUtils.capitalize(entitaet.getPaket()) + "::"
                    + entitaet.getNameCapitalized();
        }
        return entitaet.getName();
    }

    private String getWertebereichClassName(final Wertebereich wb) {
        if (!StringUtils.isEmpty(wb.getPaket())) {
            return StringUtils.capitalize(wb.getPaket()) + "::" + wb.getNameCapitalized() + "Enum";
        }
        return wb.getNameCapitalized() + "Enum";
    }

    private void entitaetAusgeben(final Entitaet entitaet, final StringBuilder sb,
                                  final Set<String> verarbeiteteEntitaeten, final DomainModel domain) {
        if (!verarbeiteteEntitaeten.contains(getEntitaetClassName(entitaet))) {
            sb.append("""
                    
                    %sclass %s{
                    """.formatted(entitaet.isAbstrakt() ? "abstract " : "",
                    getEntitaetClassName(entitaet)));

            for (Entitaetsfeld entitaetsfeld : entitaet.getFelder()) {
                sb.append("""
                        %s%s: %s
                        """.formatted(entitaetsfeld.isPk() ? "{static} " : "",
                        entitaetsfeld.getName(),
                        GeneratorUtils.getJavaType(entitaetsfeld, new HashSet<>(), domain, false)));
            }
            sb.append("""
                    }
                    
                    """);
            verarbeiteteEntitaeten.add(getEntitaetClassName(entitaet));
        }
    }

}
