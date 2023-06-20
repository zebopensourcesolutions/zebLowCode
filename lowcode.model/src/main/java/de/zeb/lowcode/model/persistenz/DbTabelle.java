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

import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaetreferenz;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;


/**
 * @author dkleine
 *
 */
@Data
@Builder
public class DbTabelle {
    public final String               name;
    @Singular( "spalte" )
    public final List<DbTabellenspalte> spalten;
    @Singular( "index" )
    public final List<DbIndex>          indizes;
    public final Entitaetreferenz     entitaet;

    public Entitaet getEntitaet( final DomainModel domain ) {
        return domain.getEntitaetByReference( getEntitaet() );
    }
}
