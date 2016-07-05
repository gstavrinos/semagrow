package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.QueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.UnaryTupleOperator;

/**
 * A query plan is a subtree of physical operators that contain the
 * necessary information needed for the evaluation of the tree.
 * <p>
 * A Plan operator is essentially an extension of a {@link UnaryTupleOperator}
 * that wraps a {@link TupleExpr} and has an attached {@link PlanPropertySet}.
 *
 * @see PlanPropertySet
 * @see TupleExpr
 * @see UnaryTupleOperator
 * @author acharal
 */
public class Plan extends UnaryTupleOperator {

    private PlanPropertySet properties;

    public Plan(TupleExpr arg) {
        super(arg);
    }

    public PlanPropertySet getProperties() { return properties; }

    public void setProperties(PlanPropertySet properties) { this.properties = properties; }

    public <X extends Exception> void visit(QueryModelVisitor<X> xQueryModelVisitor) throws X {
        //getArg().visit(xQueryModelVisitor);
        xQueryModelVisitor.meetOther(this);
    }

    public String getSignature()
    {
        StringBuilder sb = new StringBuilder(128);

        sb.append(super.getSignature());
        sb.append("(cost=" + getProperties().getCost().toString());
        sb.append(", card=" + getProperties().getCardinality());
        sb.append(", site=" + getProperties().getSite() +")");
        return sb.toString();
    }

}
