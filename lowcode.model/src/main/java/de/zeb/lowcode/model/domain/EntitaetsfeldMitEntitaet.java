package de.zeb.lowcode.model.domain;

public class EntitaetsfeldMitEntitaet {

    public Entitaetsfeld getFeld() {
        return this.feld;
    }

    public Entitaet getEntitaet() {
        return this.entitaet;
    }

    private final Entitaetsfeld feld;
    private final Entitaet      entitaet;

    public EntitaetsfeldMitEntitaet(final Entitaet entitaet, final Entitaetsfeld feld) {
        super();
        this.feld = feld;
        this.entitaet = entitaet;
    }
}
