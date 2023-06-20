/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 06.03.2023 - 12:28:01
 */
package de.zeb.lowcode.model;


import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;


/**
 * @author dkleine
 *
 */
@Builder
@Data
public class TypescriptImport {
    public final String       defaultType;
    @Singular( "type" )
    public final List<String> types;
    public final String       from;

}
