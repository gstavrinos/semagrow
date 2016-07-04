package org.semagrow.plan;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.algebra.QueryRoot;

/**
 * Created by angel on 30/6/2016.
 */
public interface QueryPlanner {


    Plan compile(QueryRoot query, Dataset dataset, BindingSet bindings);

}
