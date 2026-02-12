/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 17.03.2023 - 11:06:31
 */
package de.zeb.lowcode.model.domain;


import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.lang3.Strings;


/**
 * @author dkleine
 *
 */
@Builder
@Data
public class DomainModel {
    @Singular( "entitaet" )
    public final List<Entitaet> entitaeten;

    public Entitaet getEntitaetByReference( final Entitaetreferenz referenz ) {
        for ( Entitaet entitaet : getEntitaeten() ) {
            if ( entitaet.getName().equals( referenz.getName() ) ) {
                if ( Strings.CS.equals( entitaet.getPaket(), referenz.getPaket() ) ) {
                    return entitaet;
                }
            }
        }
        return null;
    }
}
