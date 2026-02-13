/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 23.06.2023 - 13:12:16
 */
package de.zeb.lowcode.model.domain;


import org.apache.commons.lang3.StringUtils;


/**
 * @author dkleine
 *
 */
public interface EntitaetreferenzIf {

    String getName();

    String getPaket();

    default String getNameCapitalized() {
        return StringUtils.capitalize(getName());
    }

    default String getNameUncapitalized() {
        return StringUtils.uncapitalize(getName());
    }

}
