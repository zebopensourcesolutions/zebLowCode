/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 16.03.2023 - 15:45:06
 */
package de.zeb.lowcode.model.ui.tabelle;


import de.zeb.lowcode.model.ui.MaskenelementMitFeld;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@Data
@EqualsAndHashCode( callSuper = true )
public abstract class AbstractTabellenspalte extends MaskenelementMitFeld implements Tabellenspalte {

    // In Pixel
    @Builder.Default
    public final Integer minWidth = 200;
    // In Pixel
    public final Integer maxWidth;

    @Builder.Default
    public final boolean visible  = true;

    @Builder.Default
    public final boolean editable = true;

    @Builder.Default
    public final boolean sortable = true;

    @Builder.Default
    public final boolean rowDrag  = false;

    @Override
    public String getName() {
        return getFeld().getName();
    }
}
