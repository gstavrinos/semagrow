package org.semagrow.plan;


import org.eclipse.rdf4j.query.algebra.TupleExpr;

import java.util.Collection;
import java.util.Set;

/**
 * Created by angel on 30/6/2016.
 */
public interface PlanCollection extends Collection<Plan> {

    Set<TupleExpr> getLogicalExpr();

}
