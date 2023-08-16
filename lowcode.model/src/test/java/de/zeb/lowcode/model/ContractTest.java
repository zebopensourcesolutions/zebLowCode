/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 31.03.2023 - 11:55:37
 */
package de.zeb.lowcode.model;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.zeb.lowcode.model.domain.Datentyp;
import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetreferenz;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.domain.Wertebereich;
import de.zeb.lowcode.model.domain.WertebereichEintrag;
import de.zeb.lowcode.model.ui.Maske;
import de.zeb.lowcode.model.ui.Maskenelement;
import de.zeb.lowcode.model.ui.MaskenelementMitFeld;
import de.zeb.lowcode.model.ui.MaskenelementMitParent;
import de.zeb.lowcode.model.ui.UiModel;
import de.zeb.lowcode.model.ui.maskenelemente.Checkbox;
import de.zeb.lowcode.model.ui.maskenelemente.CustomComponentProp;
import de.zeb.lowcode.model.ui.maskenelemente.CustomUiComponent;
import de.zeb.lowcode.model.ui.maskenelemente.Dropdown;
import de.zeb.lowcode.model.ui.maskenelemente.Grid;
import de.zeb.lowcode.model.ui.maskenelemente.Gridelement;
import de.zeb.lowcode.model.ui.maskenelemente.MaskeGridItems;
import de.zeb.lowcode.model.ui.maskenelemente.Tab;
import de.zeb.lowcode.model.ui.maskenelemente.Tabs;
import de.zeb.lowcode.model.ui.maskenelemente.UiModelReact;
import de.zeb.lowcode.model.ui.tabelle.AbstractTabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.BooleanTabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.DatumTabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.DatumUhrzeitTabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.Tabelle;
import de.zeb.lowcode.model.ui.tabelle.Tabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.TextTabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.WertebereichTabellenspalte;
import de.zeb.lowcode.model.ui.tabelle.ZahlTabellenspalte;
import de.zeb.lowcode.model.validierung.TextfeldValidierung;
import de.zeb.lowcode.model.validierung.Validierung;
import de.zeb.lowcode.model.validierung.ZahlfeldValidierung;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;


/**
 * @author dkleine
 *
 */
class ContractTest {

    private static List<Class<?>> getModellclasses() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add( LowCodeModel.class );
        classes.add( LowCodeModelElement.class );
        classes.add( TypescriptImport.class );
        classes.add( Datentyp.class );
        classes.add( DomainModel.class );
        classes.add( Entitaet.class );
        classes.add( Entitaetreferenz.class );
        classes.add( Entitaetsfeld.class );
        classes.add( Wertebereich.class );
        classes.add( WertebereichEintrag.class );
        classes.add( Maske.class );
        classes.add( Maskenelement.class );
        classes.add( MaskenelementMitFeld.class );
        classes.add( UiModel.class );
        classes.add( Checkbox.class );
        classes.add( CustomComponentProp.class );
        classes.add( CustomUiComponent.class );
        classes.add( Dropdown.class );
        classes.add( Grid.class );
        classes.add( Gridelement.class );
        classes.add( MaskeGridItems.class );
        classes.add( Tab.class );
        classes.add( Tabs.class );
        classes.add( UiModelReact.class );
        classes.add( AbstractTabellenspalte.class );
        classes.add( BooleanTabellenspalte.class );
        classes.add( DatumTabellenspalte.class );
        classes.add( DatumUhrzeitTabellenspalte.class );
        classes.add( Tabelle.class );
        classes.add( Tabellenspalte.class );
        classes.add( TextTabellenspalte.class );
        classes.add( WertebereichTabellenspalte.class );
        classes.add( ZahlTabellenspalte.class );
        classes.add( TextfeldValidierung.class );
        classes.add( Validierung.class );
        classes.add( ZahlfeldValidierung.class );
        return classes;
    }

    @Test
    public void equalsContract() {
        for ( Class<?> modellClass : getModellclasses() ) {
            EqualsVerifier.forClass( modellClass ).suppress( Warning.STRICT_INHERITANCE ).verify();
        }

        EqualsVerifier.forClass( MaskenelementMitParent.class )
                .withPrefabValues( MaskenelementMitParent.class,
                        MaskenelementMitParent.builder().child( Tab.builder().label( "Test" ).name( "Test" ).build() )
                                .build(),
                        MaskenelementMitParent.builder().child( Tab.builder().label( "Test2" ).name( "Test2" ).build() )
                                .build() )
                .suppress( Warning.STRICT_INHERITANCE ).verify();
    }

}
