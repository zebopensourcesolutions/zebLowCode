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


import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@Data
public abstract class Maskenelement implements MaskenelementIf {
    public final String doku;
    public final String tooltip;

    @Override
    public String getNameCapitalized() {
        return StringUtils.capitalize( getName() );
    }

    @Override
    public String getNameUncapitalized() {
        return StringUtils.uncapitalize( getName() );
    }
}
