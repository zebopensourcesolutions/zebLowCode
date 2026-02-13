/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 14:13:29
 */
package de.zeb.lowcode.model.ui;


import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@Data
public class UiModel<MASKEN_TYP extends Maske<?>> {
    public final String beschreibung;
    @Singular("maske")
    public final List<MASKEN_TYP> masken;

}
