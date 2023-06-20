/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 03.04.2023 - 09:44:25
 */
package de.zeb.lowcode.model.persistenz;


import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;


/**
 * @author dkleine
 *
 */
@Data
@Builder
public class Persistenzmodell {
    @Singular( "tabelle" )
    public final List<DbTabelle> tabellen;
}
