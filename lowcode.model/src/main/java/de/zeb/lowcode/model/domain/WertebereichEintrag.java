/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 16:10:26
 */
package de.zeb.lowcode.model.domain;


import lombok.Data;
import lombok.NonNull;


/**
 * @author dkleine
 *
 */
@Data
public class WertebereichEintrag {
    @NonNull
    public final String name;
    @NonNull
    public final String value;
    @NonNull
    public final String label;

    public WertebereichEintrag( final String nameAndValue, final String label ) {
        this.name = nameAndValue;
        this.value = nameAndValue;
        this.label = label;
    }

    public WertebereichEintrag( final String name, final String value, final String label ) {
        this.name = name;
        this.value = value;
        this.label = label;
    }

}
