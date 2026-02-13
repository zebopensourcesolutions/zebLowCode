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
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author dkleine
 *
 */
@Data
@Builder
public class Entitaet implements EntitaetreferenzIf {
    public final String name;
    public final String beschreibung;
    @Singular("feld")
    public final List<Entitaetsfeld> felder;
    public final Entitaetreferenz erbtVon;
    @Builder.Default
    public final boolean abstrakt = false;
    @Builder.Default
    public final boolean persistenz = true;
    /**
     * Definiert, ob eine Enitität eigenständig existiert oder nur als Kind einer anderen Entität.
     * <p>
     * Äquivalent zu Aggregate in DDD
     */
    @Builder.Default
    public final boolean eigenstaendig = true;
    public final String paket;
    public final String dbTabellenname;
    @Builder.Default
    public final DbIndex dbIndex = null;

    public List<? extends Entitaetsfeld> getAlleFelderMitZielentitaeten() {
        return getFelder().stream().filter(e -> e.getZielEntitaet() != null).collect(Collectors.toList());
    }

    public Optional<? extends Entitaetsfeld> getFeld(final String name) {
        return getFelder().stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    public List<Entitaetsfeld> getFelderMitVererbung(final DomainModel domain) {
        List<Entitaetsfeld> felder = new ArrayList<>(getFelder());
        if (getErbtVon() != null) {
            Entitaet entitaetByReference = domain.getEntitaetByReference(getErbtVon());
            if (entitaetByReference != null) {
                felder.addAll(entitaetByReference.getFelderMitVererbung(domain));
            }
        }
        return felder;
    }

    public String getDbTabellenname() {
        if (dbTabellenname != null) {
            return dbTabellenname;
        }
        return name;
    }

    public Entitaetreferenz getEntitaetreferenz() {
        return Entitaetreferenz.builder().name(getName()).paket(getPaket()).build();
    }
}
