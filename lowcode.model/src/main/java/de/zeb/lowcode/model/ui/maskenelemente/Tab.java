/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 14:59:45
 */
package de.zeb.lowcode.model.ui.maskenelemente;


import java.util.List;

import de.zeb.lowcode.model.ui.ChildrenAware;
import de.zeb.lowcode.model.ui.Maskenelement;
import de.zeb.lowcode.model.ui.MaskenelementIf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@Data
@EqualsAndHashCode( callSuper = true )
public class Tab extends Maskenelement implements ChildrenAware {

    @NonNull
    public final String                name;

    @NonNull
    @Singular( "maskenelement" )
    public final List<MaskenelementIf> maskenelemente;

    @NonNull
    public final String                label;

    /** {@inheritDoc} */
    @Override
    public List<? extends MaskenelementIf> getKinder() {
        return getMaskenelemente();
    }

}
