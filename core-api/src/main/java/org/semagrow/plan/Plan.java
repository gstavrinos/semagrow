package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.QueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.UnaryTupleOperator;

/**
 * Created by angel on 9/30/14.
 */
public class Plan extends UnaryTupleOperator {

    private PlanProperties properties;

    //public Plan(TupleExpr arg) { super(arg); }

    public Plan(TupleExpr arg) {
        super(arg);

        //properties = SimplePlanProperties.defaultProperties();
    }

    public PlanProperties getProperties() { return properties; }

    public void setProperties(PlanProperties properties) { this.properties = properties; }

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
