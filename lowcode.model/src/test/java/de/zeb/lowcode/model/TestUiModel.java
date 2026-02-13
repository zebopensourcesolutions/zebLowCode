/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 14:26:10
 */
package de.zeb.lowcode.model;


import com.google.gson.GsonBuilder;
import de.zeb.lowcode.model.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author dkleine
 *
 */
@SuppressWarnings("nls")
class TestUiModel {

    @Test
    void testModell() {
        LowCodeModel ui = domainErzeugen();
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(ui);
        assertTrue(json.length() > 100);
        System.out.println(json);
    }

    /**
     * @return
     */
    private LowCodeModel domainErzeugen() {

        Wertebereich turnusWb = Wertebereich.builder()
                .name("turnus")
                .eintrag(new WertebereichEintrag("M", "Monatlich"))
                .eintrag(new WertebereichEintrag("Q", "Quartärlich"))
                .eintrag(new WertebereichEintrag("J", "Jährlich"))
                .build();
        Wertebereich offsettWb = Wertebereich.builder()
                .name("offsett")
                .eintrag(new WertebereichEintrag("U1", "1", "1"))
                .eintrag(new WertebereichEintrag("U2", "2", "2"))
                .eintrag(new WertebereichEintrag("U3", "3", "3"))
                .eintrag(new WertebereichEintrag("U4", "4", "4"))
                .eintrag(new WertebereichEintrag("U5", "5", "5"))
                .eintrag(new WertebereichEintrag("U6", "6", "6"))
                .eintrag(new WertebereichEintrag("U7", "7", "7"))
                .eintrag(new WertebereichEintrag("U8", "8", "8"))
                .eintrag(new WertebereichEintrag("U9", "9", "9"))
                .eintrag(new WertebereichEintrag("U10", "10", "10"))
                .build();
        Entitaetsfeld feldAktiv = Entitaetsfeld.builder()
                .fachlicherName("Nachverarbeitung aktiv")
                .name("aktiv")
                .beschreibung("Definiert ob die Nachtverarbeitung ausgeführt werden soll.")
                .datenTyp(Datentyp.BOOLEAN)
                .build();
        Entitaetsfeld feldRegelturnus = Entitaetsfeld.builder()
                .fachlicherName("Regelturnus")
                .name("turnus")
                .beschreibung("Definiert den Regelturnus, als den Turnus in dem eine Nachtverarbeitung ausgeführt werden soll (Monatlich, Quartärlich etc.).")
                .wertebereich(turnusWb)
                .build();
        Entitaetsfeld feldUltimo = Entitaetsfeld.builder()
                .fachlicherName("Verarbeitungstag: Ultimo+")
                .name("ultimo")
                .optional(true)
                .beschreibung("Bankarbeitstage nach Ultimo")
                .wertebereich(offsettWb)
                .build();
        Entitaet e1 = Entitaet.builder()
                .name("NachtverarbeitungPar")
                .feld(feldAktiv)
                .feld(feldRegelturnus)
                .feld(feldUltimo)
                .build();
        DomainModel domain = DomainModel.builder()
                .entitaet(e1) //alle Entitäten bekannt machen
                .build();
        LowCodeModel lcm = LowCodeModel.builder()
                .domain(domain)
                .anwendungskuerzel("zeb")
                .build();
        // @formatter:on
        return lcm;
    }

}
