/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 16.03.2023 - 15:47:12
 */
package de.zeb.lowcode.model.ui.tabelle;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
public class ZahlTabellenspalte extends AbstractTabellenspalte {

    public final Format format;

    public enum Format {
        DEFAULT, CURRENCY, CURRENCY_EUR, CURRENCY_EUR_MILLION, PERCENTAGE, BASISPUNKT, NONE
    }

}
