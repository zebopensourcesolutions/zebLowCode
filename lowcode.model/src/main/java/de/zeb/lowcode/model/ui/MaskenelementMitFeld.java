/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 14:40:24
 */
package de.zeb.lowcode.model.ui;


import de.zeb.lowcode.model.domain.Entitaetsfeld;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@EqualsAndHashCode( callSuper = true )
@Data
public abstract class MaskenelementMitFeld extends Maskenelement implements MaskenelementMitFeldIf {
    @NonNull
    // Hier wird bei "onChange" hineingeschrieben und normalerweise auch draus gelesen
    public final Entitaetsfeld feld;
    public final String        label;

}
