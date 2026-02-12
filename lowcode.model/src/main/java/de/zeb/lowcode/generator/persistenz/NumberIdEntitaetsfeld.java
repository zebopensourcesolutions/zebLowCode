package de.zeb.lowcode.generator.persistenz;

import de.zeb.lowcode.model.domain.Entitaetsfeld;

public class NumberIdEntitaetsfeld extends Entitaetsfeld {
    protected NumberIdEntitaetsfeld(EntitaetsfeldBuilder<NumberIdEntitaetsfeld, ?> b) {
        super(b);
    }

    public static EntitaetsfeldBuilder<?, ?> builder() {
        return new NumberIdEntitaetsfeld.NumberIdEntitaetsfeldBuilderImpl();
    }

    private static final class NumberIdEntitaetsfeldBuilderImpl extends
            EntitaetsfeldBuilder<NumberIdEntitaetsfeld, NumberIdEntitaetsfeld.NumberIdEntitaetsfeldBuilderImpl> {
        private NumberIdEntitaetsfeldBuilderImpl() {
        }

        protected NumberIdEntitaetsfeld.NumberIdEntitaetsfeldBuilderImpl self() {
            return this;
        }

        public NumberIdEntitaetsfeld build() {
            return new NumberIdEntitaetsfeld(this);
        }
    }
}
