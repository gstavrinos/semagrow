package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.TupleExpr;

/**
 * Created by angel on 24/6/2016.
 */
public interface PlanFactory {

    Plan create(TupleExpr id, TupleExpr expr);

    Plan create(TupleExpr expr);

    /**
     * Create a plan from an expression and a set of initial properties
     * @param expr
     * @param props
     * @return
     */
    Plan create(TupleExpr id, TupleExpr expr, PlanProperties props);

    Plan create(TupleExpr expr, PlanProperties props);

}
