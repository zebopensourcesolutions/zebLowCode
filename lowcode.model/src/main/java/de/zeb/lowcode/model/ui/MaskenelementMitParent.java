/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 17.03.2023 - 11:47:07
 */
package de.zeb.lowcode.model.ui;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;


/**
 * @author dkleine
 *
 */
@Data
@Builder
public class MaskenelementMitParent {

    public final MaskenelementMitParent parent;
    @NonNull
    public final MaskenelementIf child;

    public String getNamenCapitalized() {
        if (getParent() != null) {
            return getParent().getNamenCapitalized() + StringUtils.capitalize(getChild().getName());
        }
        return StringUtils.capitalize(getChild().getName());
    }

    public String getName(final String trennzeichen) {
        if (getParent() != null) {
            return getParent().getName(trennzeichen) + trennzeichen + getChild().getName();
        }
        return getChild().getName();
    }

    public String getLabel() {
        return getChild().getLabel();
    }

    public String getTooltip() {
        return getChild().getTooltip();
    }
}
