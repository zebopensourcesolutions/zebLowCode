/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 08.03.2023 - 13:14:30
 */
package de.zeb.lowcode.model.ui.maskenelemente;


import de.zeb.lowcode.model.domain.Entitaetsfeld;
import lombok.Builder;
import lombok.Data;


/**
 * @author dkleine
 *
 */
@Data
@Builder
public class CustomComponentProp {

    public final String        name;
    public final Entitaetsfeld mappingFeld;
    public final String        mappingWert;

}
