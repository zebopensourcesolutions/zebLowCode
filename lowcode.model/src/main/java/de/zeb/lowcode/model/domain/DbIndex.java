/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 09.03.2023 - 07:32:50
 */
package de.zeb.lowcode.model.domain;


import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@Data
@SuperBuilder
public class DbIndex {
    @NonNull
    public final String           name;
    @Singular( "spalte" )
    public final List<Entitaetsfeld> spalten;

    public String getNameCapitalized() {
        return StringUtils.capitalize( getName() );
    }

    public String getNameUncapitalized() {
        return StringUtils.uncapitalize( getName() );
    }

}
