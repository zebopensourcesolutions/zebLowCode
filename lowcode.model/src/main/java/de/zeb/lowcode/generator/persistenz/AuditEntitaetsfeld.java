package de.zeb.lowcode.generator.persistenz;

import de.zeb.lowcode.model.domain.Entitaetsfeld;

public class AuditEntitaetsfeld extends Entitaetsfeld {
    protected AuditEntitaetsfeld(EntitaetsfeldBuilder<AuditEntitaetsfeld, ?> b) {
        super(b);
    }

    public static EntitaetsfeldBuilder<?, ?> builder() {
        return new AuditEntitaetsfeld.AuditEntitaetsfeldBuilderImpl();
    }


    private static final class AuditEntitaetsfeldBuilderImpl extends EntitaetsfeldBuilder<AuditEntitaetsfeld, AuditEntitaetsfeld.AuditEntitaetsfeldBuilderImpl> {
        private AuditEntitaetsfeldBuilderImpl() {
        }

        protected AuditEntitaetsfeld.AuditEntitaetsfeldBuilderImpl self() {
            return this;
        }

        public AuditEntitaetsfeld build() {
            return new AuditEntitaetsfeld(this);
        }
    }
}
