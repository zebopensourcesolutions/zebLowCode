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


import org.apache.commons.lang3.StringUtils;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@Data
@SuperBuilder
public class Entitaetsfeld {
    @Builder.Default
    public final boolean          optional          = false;
    @Builder.Default
    public final boolean          pk                = false;
    @Builder.Default
    public final boolean          fachlichEindeutig = false;

    @NonNull
    public final String           name;
    public final String           fachlicherName;
    public final String           beschreibung;
    public final Wertebereich     wertebereich;
    @Builder.Default
    @NonNull
    public final Datentyp         datenTyp          = Datentyp.TEXT;
    @Builder.Default
    public final boolean          alsListe          = false;
    @Builder.Default
    public final boolean          alsTabelle        = false;
    public final Entitaetreferenz zielEntitaet;
    public final String           dbSpaltenname;
    public final Integer          anzahlZeichen;
    public final Integer          anzahlZahlen;
    public final Integer          anzahlNachkommastellen;
    @Builder.Default
    public final boolean          persistenz        = true;
    @Builder.Default
    public final 				  Fetchtyp fetchtyp = Fetchtyp.DEFAULT;

    public String getNameCapitalized() {
        return StringUtils.capitalize( getName() );
    }

    public String getNameUncapitalized() {
        return StringUtils.uncapitalize( getName() );
    }

}
