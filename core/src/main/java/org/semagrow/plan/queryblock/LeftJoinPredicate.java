package org.semagrow.plan.queryblock;

import org.eclipse.rdf4j.query.algebra.Compare;
import org.eclipse.rdf4j.query.algebra.ValueExpr;

import java.util.Collection;

/**
 * @author acharal
 */
public class LeftJoinPredicate extends BinaryPredicate {

    private Collection<Quantifier> eel;

    public LeftJoinPredicate(Quantifier.Var from, Quantifier.Var to)
    {
        super(from, to);
        setEEL(getEL());
    }

    public void setEEL(Collection<Quantifier> eel) { this.eel = eel; }

    public Collection<Quantifier> getEEL() { return eel; }

    public ValueExpr asExpr() {
        return new Compare(getFrom(), getTo(), Compare.CompareOp.EQ);
    }

    public void replaceVarWith(Quantifier.Var v1, Quantifier.Var v2) {

        if (getFrom().equals(v1))
            setFrom(v1);

        if (getTo().equals(v2))
            setTo(v2);
    }

}
