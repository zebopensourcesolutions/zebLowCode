/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 06.03.2023 - 10:56:52
 */
package de.zeb.lowcode.generator.model;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


/**
 * @author dkleine
 *
 */
@Builder
@Data
public class GeneratedFile {

    @NonNull
    public final String folder;
    @NonNull
    public final String file;
    public final String content;

    public boolean isGenerated() {
        return this.folder.contains("/generated") || this.folder.contains("/gen/"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getRelativePath() {
        return this.folder + "/" + this.file; //$NON-NLS-1$
    }
}
