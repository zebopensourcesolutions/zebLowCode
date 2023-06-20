/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 06.03.2023 - 10:09:05
 */
package de.zeb.lowcode.model.ui;


import java.util.Collections;
import java.util.List;

import de.zeb.lowcode.model.domain.WertebereichEintrag;


/**
 * @author dkleine
 *
 */
public interface DropdownValuesAware extends MaskenelementMitFeldIf {

    default List<WertebereichEintrag> getValues() {
        if ( getFeld().getWertebereich() != null ) {
            return getFeld().getWertebereich().getEintraege();
        }
        return Collections.emptyList();
    }
}
