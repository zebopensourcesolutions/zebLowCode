/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 07.03.2023 - 14:24:00
 */
package de.zeb.lowcode.model.ui;


import de.zeb.lowcode.model.domain.Entitaetsfeld;
import lombok.NonNull;


/**
 * @author dkleine
 *
 */
public interface MaskenelementMitFeldIf extends MaskenelementIf {

    @Override
    default String getLabel() {
        return getFeld().getFachlicherName();
    }

    @NonNull
    Entitaetsfeld getFeld();

    @Override
    default String getName() {
        return getFeld().getName();
    }

    default boolean isOptional() {
        return getFeld().isOptional();
    }

}
