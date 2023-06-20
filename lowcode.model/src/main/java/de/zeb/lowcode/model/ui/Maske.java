/*
 * COPYRIGHT:
 *
 * TITLE TO THE CODE REMAIN WITH ZEB/INFORMATION.TECHNOLOGY. THE CODE IS COPYRIGHTED AND PROTECTED BY LAW. YOU WILL NOT
 * REMOVE ANY COPYRIGHT NOTICE FROM THE CODE. REASSEMBLING, RECOMPILATION, TRANSFER, DISTRIBUTION OR MODIFICATION OF
 * PART OR ALL OF THE CODE IN ANY FORM WITHOUT THE PRIOR WRITTEN PERMISSION OF ZEB/INFORMATION.TECHNOLOGY IS PROHIBITED.
 *
 * created: 02.03.2023 - 14:40:24
 */
package de.zeb.lowcode.model.ui;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.zeb.lowcode.model.domain.DomainModel;
import de.zeb.lowcode.model.domain.Entitaet;
import de.zeb.lowcode.model.domain.Entitaet.EntitaetBuilder;
import de.zeb.lowcode.model.domain.Entitaetsfeld;
import de.zeb.lowcode.model.ui.tabelle.Tabelle;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;


/**
 * @author dkleine
 *
 */
@SuperBuilder
@Data
public abstract class Maske<MASKEN_ELEMENT_TYP extends MaskenelementIf> {
    @NonNull
    public final String                    titel;
    @NonNull
    // Name ohne Leerzeichen, muss als Variable taugen
    public final String                    name;
    @NonNull
    public final String                    url;
    @Singular( "urlParameter" )
    public final List<String>              urlParameter;

    public final String                    doku;
    @Singular( "pfad" )
    public final List<String>              pfade;
    @Singular( "element" )
    public final List<MASKEN_ELEMENT_TYP>  elemente;

    @NonNull
    public final MaskenEntitaet            entitaet;

    // Parameter müssen Felder aus der hier gesetzten Entität sein
    @Singular( "parameterFeld" )
    public final List<MaskenEntitaetsfeld> parameterFelder;

    public String getNameCapitalized() {
        return StringUtils.capitalize( getName() );
    }

    public String getNameUncapitalized() {
        return StringUtils.uncapitalize( getName() );
    }

    public List<MaskenelementMitFeldIf> getMaskenelementeMitFeld() {
        List<MaskenelementMitFeldIf> ergebnis = new ArrayList<>();
        for ( MaskenelementIf element : this.elemente ) {
            if ( element instanceof MaskenelementMitFeldIf ) {
                ergebnis.add( (MaskenelementMitFeldIf) element );
            }
        }

        return ergebnis;

    }

    /**
     * Gibt alle Maskenelemente mit einem Feld zurück, auch Kinder von Elementen.
     *
     * @return
     */
    public List<MaskenelementMitParent> getMaskenelementeRekursivMitFeldBeiZuEinsRelation() {
        return rekursivMaskenelementeMitZuEinsFeldern( null, getElemente() );
    }

    /**
     * Gibt alle Maskenelemente mit einem Feld zurück, auch Kinder von Elementen.
     *
     * @return
     */
    public List<Tabelle> getTabellen() {
        List<Tabelle> tabellen = new ArrayList<>();
        for ( MaskenelementMitParent maskenelementMitParent : rekursivMaskenelementeMitZuNFeldern( null,
                getElemente() ) ) {
            if ( maskenelementMitParent.getChild() instanceof Tabelle ) {
                tabellen.add( (Tabelle) maskenelementMitParent.getChild() );
            }
        }
        return tabellen;
    }

    /**
     * Gibt alle Maskenelemente mit und ohne Feld zurück, auch Kinder von Elementen.
     *
     * @return
     */
    public List<MaskenelementMitParent> getMaskenelementeRekursiv() {
        return rekursivMaskenelementeErmitteln( getElemente() );
    }

    public Entitaet getMaskenModell() {
        EntitaetBuilder eb = Entitaet.builder().name( this.getName() );
        for ( MaskenelementMitFeldIf maskenFeld : getMaskenelementeMitFeld() ) {
            eb.feld( maskenFeld.getFeld() );
        }

        return eb.build();
    }

    public List<Entitaet> getEntitaetenRekursiv( final DomainModel domain ) {
        List<Entitaet> ergebnis = new ArrayList<>();
        entitaetMitKindern( ergebnis, getMaskenModell(), domain );
        return ergebnis;
    }

    /**
     * @param ergebnis
     */
    private static void entitaetMitKindern( final List<Entitaet> ergebnis, final Entitaet entitaet,
            final DomainModel domain ) {
        ergebnis.add( entitaet );
        for ( Entitaetsfeld entitaetBeziehung : entitaet.getAlleFelderMitZielentitaeten() ) {
            entitaetMitKindern( ergebnis, domain.getEntitaetByReference( entitaetBeziehung.getZielEntitaet() ),
                    domain );
        }
    }

    /**
     * @param ergebnis
     * @return
     */
    public static List<MaskenelementMitParent> rekursivMaskenelementeMitZuEinsFeldern(
            final MaskenelementMitParent parent, final List<? extends MaskenelementIf> elemente ) {
        List<MaskenelementMitParent> ergebnis = new ArrayList<>();
        for ( MaskenelementIf element : elemente ) {
            MaskenelementMitParent currentMaskenelementMitParent = MaskenelementMitParent.builder().parent( parent )
                    .child( element ).build();

            if ( element instanceof MaskenelementMitFeldIf ) {
                if ( !( (MaskenelementMitFeldIf) element ).getFeld().isAlsListe() ) {
                    ergebnis.add( currentMaskenelementMitParent );
                }
            }
            if ( element instanceof ChildrenAware ) {
                ChildrenAware ca = (ChildrenAware) element;
                ergebnis.addAll(
                        rekursivMaskenelementeMitZuEinsFeldern( currentMaskenelementMitParent, ca.getKinder() ) );
            }
        }
        return ergebnis;
    }

    /**
     * @param ergebnis
     * @return
     */
    public static List<MaskenelementMitParent> rekursivMaskenelementeMitZuNFeldern( final MaskenelementMitParent parent,
            final List<? extends MaskenelementIf> elemente ) {
        List<MaskenelementMitParent> ergebnis = new ArrayList<>();
        for ( MaskenelementIf element : elemente ) {
            MaskenelementMitParent currentMaskenelementMitParent = MaskenelementMitParent.builder().parent( parent )
                    .child( element ).build();

            if ( element instanceof MaskenelementMitFeldIf ) {
                if ( ( (MaskenelementMitFeldIf) element ).getFeld().isAlsListe() ) {
                    ergebnis.add( currentMaskenelementMitParent );
                }
            }
            if ( element instanceof ChildrenAware ) {
                ChildrenAware ca = (ChildrenAware) element;
                ergebnis.addAll(
                        rekursivMaskenelementeMitZuEinsFeldern( currentMaskenelementMitParent, ca.getKinder() ) );
            }
        }
        return ergebnis;
    }

    public static List<MaskenelementMitParent> rekursivMaskenelementeErmitteln(
            final List<? extends MaskenelementIf> elemente ) {
        return rekursivMaskenelementeErmitteln( null, elemente );
    }

    /**
     * @param ergebnis
     * @return
     */
    public static List<MaskenelementMitParent> rekursivMaskenelementeErmitteln( final MaskenelementMitParent parent,
            final List<? extends MaskenelementIf> elemente ) {
        List<MaskenelementMitParent> ergebnis = new ArrayList<>();
        for ( MaskenelementIf element : elemente ) {
            MaskenelementMitParent currentMaskenelementMitParent = MaskenelementMitParent.builder().parent( parent )
                    .child( element ).build();
            ergebnis.add( currentMaskenelementMitParent );
            if ( element instanceof ChildrenAware ) {
                ChildrenAware ca = (ChildrenAware) element;
                ergebnis.addAll( rekursivMaskenelementeErmitteln( currentMaskenelementMitParent, ca.getKinder() ) );
            }
        }
        return ergebnis;
    }

}
