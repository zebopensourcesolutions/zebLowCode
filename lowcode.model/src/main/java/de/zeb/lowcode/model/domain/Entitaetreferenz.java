/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 09.03.2023 - 07:32:13
 */
package de.zeb.lowcode.model.domain;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


/**
 * @author dkleine
 *
 */
@Data
@Builder
public class Entitaetreferenz implements EntitaetreferenzIf {
    @NonNull
    public final String name;
    public final String paket;
    @Builder.Default
    public final FetchType fetchType = FetchType.DEFAULT;
    @Builder.Default
    public final CascadeType cascadeType = CascadeType.DEFAULT;

}
