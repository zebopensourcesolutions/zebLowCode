package de.zeb.lowcode.generator.persistenz;

import de.zeb.lowcode.model.domain.Entitaetsfeld;

public class IdEntitaetsfeld extends Entitaetsfeld {
    protected IdEntitaetsfeld(EntitaetsfeldBuilder<IdEntitaetsfeld, ?> b) {
        super(b);
    }

    public static EntitaetsfeldBuilder<?, ?> builder() {
        return new IdEntitaetsfeld.IdEntitaetsfeldBuilderImpl();
    }

    private static final class IdEntitaetsfeldBuilderImpl extends
            EntitaetsfeldBuilder<IdEntitaetsfeld, IdEntitaetsfeld.IdEntitaetsfeldBuilderImpl> {
        private IdEntitaetsfeldBuilderImpl() {
        }

        protected IdEntitaetsfeld.IdEntitaetsfeldBuilderImpl self() {
            return this;
        }

        public IdEntitaetsfeld build() {
            return new IdEntitaetsfeld(this);
        }
    }
}
