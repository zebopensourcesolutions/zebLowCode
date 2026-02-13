package de.zeb.lowcode.generator.plantuml;

import de.zeb.lowcode.model.LowCodeModel;

public abstract class AbstractPlantumlGenerator {

    public abstract String generateModel(LowCodeModel lcm);

    protected void createPlantumlGeader(final StringBuilder sb) {
        sb.append("""
                @startuml
                !pragma layout smetana
                
                skinparam class {
                    BackgroundColor<< Anwendungskontext >> AliceBluen
                    BackgroundColor<< Parameterkontext >> WhiteSmoke
                    BackgroundColor<< Regelverarbeitungskontext >> GreenYellow
                    BackgroundColor<< Verarbeitungskontext >> Gold
                }
                skinparam groupInheritance 3
                
                set namespaceSeparator ::
                
                """);
    }
}
