/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 09.03.2023 - 07:32:13
 */
package de.zeb.lowcode.model.ui;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@Data
@SuperBuilder
public class MaskenEntitaet {

    public final String                    name;
    public final String                    beschreibung;

    @Singular( "maskenfeld" )
    public final List<MaskenEntitaetsfeld> maskenfelder;

    public List<MaskenEntitaetsfeld> getAlleFelderMitZielentitaeten() {
        return getMaskenfelder().stream().filter( e -> e.getZielEntitaet() != null ).collect( Collectors.toList() );
    }

    public String getNameCapitalized() {
        return StringUtils.capitalize( getName() );
    }

    public String getNameUncapitalized() {
        return StringUtils.uncapitalize( getName() );
    }

    public Optional<MaskenEntitaetsfeld> getFeld( final String name ) {
        return getMaskenfelder().stream().filter( e -> e.getName().equals( name ) ).findFirst();
    }
}
