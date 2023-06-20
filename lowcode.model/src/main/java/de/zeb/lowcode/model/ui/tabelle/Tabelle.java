/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 14:59:45
 */
package de.zeb.lowcode.model.ui.tabelle;


import java.util.List;

import de.zeb.lowcode.model.ui.MaskenEntitaetsfeld;
import de.zeb.lowcode.model.ui.MaskenelementMitFeld;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@Data
@EqualsAndHashCode( callSuper = true )
public class Tabelle extends MaskenelementMitFeld {

    @Singular( "spalte" )
    public final List<Tabellenspalte>      spalten;

    /*
     * Parameter müssen Felder aus der Masken-Entität sein Die Inhalte werden beim editieren, löschen, hinzufügen und
     * Validieren an das Backend mitgegeben
     */
    @Singular( "parameterFeld" )
    public final List<MaskenEntitaetsfeld> parameterFelder;

    /*
     * Diese Felder werden beim Abrufen der Tabellendefinition genutzt. Ändert sich ein Wert hier drin so wird die
     * gesamte Tabelle neu gerendert
     */
    @Singular( "tabellendefinitionParameterFeld" )
    public final List<MaskenEntitaetsfeld> tabellendefinitionParameterFelder;

    @Builder.Default
    public final boolean                   favoritenAktiv = false;
    @Builder.Default
    public final boolean                   anlegenButton  = true;
    @Builder.Default
    public final boolean                   loeschenButton = true;

}
