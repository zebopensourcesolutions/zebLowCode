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


import de.zeb.lowcode.model.domain.*;
import de.zeb.lowcode.model.ui.Maske;
import de.zeb.lowcode.model.ui.maskenelemente.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author dkleine
 *
 */
@SuppressWarnings("nls")
class TestUiGeneratorMitTabs {

    @Test
    void testWriteToFile() {
        LowCodeModel ui = uimodellErzeugen();
        Maske<?> m = ui.getUi().getMasken().get(0);
        assertNotNull(m);
    }

    /**
     * @return
     */
    private LowCodeModel uimodellErzeugen() {

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
        Entitaetsfeld feldId = Entitaetsfeld.builder()
                .fachlicherName("Eindeutige ID")
                .name("id")
                .pk(true)
                .datenTyp(Datentyp.ZEITSTEMPEL)
                .build();
        Entitaetsfeld jobId = Entitaetsfeld.builder()
                .fachlicherName("Job ID")
                .name("jobId")
                .optional(true)
                .datenTyp(Datentyp.ZEITSTEMPEL)
                .build();
        Entitaet e1 = Entitaet.builder()
                .name("NachtverarbeitungPar")
                .feld(feldId)
                .feld(feldAktiv)
                .feld(jobId)
                .feld(feldRegelturnus)
                .feld(feldUltimo)
                .build();
        Checkbox aktiv = Checkbox.builder()
                .feld(feldAktiv)
                .label("Nachverarbeitung aktiv")
                .build();
        Dropdown turnus = Dropdown.builder()
                .feld(feldRegelturnus)
                .label(feldRegelturnus.getFachlicherName())
                .build();
        Dropdown ultimo = Dropdown.builder()
                .feld(feldUltimo)
                .label(feldUltimo.getFachlicherName())
                .tooltip("Bankarbeitstage nach Ultimo")
                .build();
        Tabs tabs = Tabs.builder()
                .name("hauptreiter")
                .tab(Tab.builder().name("tabUltimo").label("Ultimo").maskenelement(ultimo).build())
                .tab(Tab.builder().name("tabTurnus").label("Turnus").maskenelement(turnus).build())
                .build();
        CustomUiComponent jobmonitor = CustomUiComponent.builder()
                .component("JobMonitor")
                .feld(jobId)
                .label(jobId.getFachlicherName())
                .tsImport(TypescriptImport.builder()
                        .from("@myorg/myapp")
                        .type("JobMonitor")
                        .build())
                //Mappe Property "abfrageId" auf "jobId"
                .prop(CustomComponentProp.builder()
                        .name("abfrageId")
                        .mappingFeld(feldId)
                        .build())
                .prop(CustomComponentProp.builder()
                        .name("anzeigeArt")
                        .mappingWert("'compact'")
                        .build())
                .build();
        MaskeGridItems maske1 = MaskeGridItems.builder()
                .titel("DVV Nachtverarbeitung")
                .name("einstellungnachtverarbeitung")
                .url("/einstellungnachtverarbeitung")
                .pfad("01 Grundeinstellungen").pfad("DVV Nachtverarbeitung")
                .entitaet(e1) //alle Entitäten bekannt machen
                .element(aktiv)
                .element(tabs)
                .element(jobmonitor)
                .build();
        UiModelReact ui = UiModelReact.builder()
                .apiPagesFile("J:\\temp\\generated\\api\\index.generated.ts")
                .maske(maske1)
                .build();
        DomainModel domain = DomainModel.builder()
//                .entitaet(  ) //alle Entitäten bekannt machen
                .build();
        LowCodeModel lcm = LowCodeModel.builder()
                .ui(ui)
                .domain(domain)
                .anwendungskuerzel("zeb")
                .build();

        // @formatter:on
        return lcm;
    }
}
