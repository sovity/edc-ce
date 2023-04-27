package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An expression or set of expressions that refines a permission, prohibitions, or duty.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "edctype")
public abstract class Constraint {

    public interface Visitor<R> {

        //R visitAndConstraint(AndConstraint constraint);
        //R visitOrConstraint(OrConstraint constraint);
        //R visitXoneConstraint(XoneConstraint constraint);

        R visitAtomicConstraint(AtomicConstraint constraint);

    }

    public abstract <R> R accept(Constraint.Visitor<R> visitor);


}
