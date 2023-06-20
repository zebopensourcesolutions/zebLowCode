/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 09.03.2023 - 07:32:50
 */
package de.zeb.lowcode.model.ui;


import de.zeb.lowcode.model.domain.Entitaetsfeld;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@Data
@EqualsAndHashCode( callSuper = true )
@SuperBuilder
public class MaskenEntitaetsfeld extends Entitaetsfeld {
    @Builder.Default
    public final boolean alsTabelle = false;

}
