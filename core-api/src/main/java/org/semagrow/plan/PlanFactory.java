package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.TupleExpr;

/**
 * Created by angel on 24/6/2016.
 */
public interface PlanFactory {

    Plan create(TupleExpr physicalExpr);

    Plan create(TupleExpr physicalExpr, PlanProperties props);

}
