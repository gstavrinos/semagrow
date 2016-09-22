package org.semagrow.plan;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.algebra.*;
import org.eclipse.rdf4j.query.algebra.QueryRoot;
import org.eclipse.rdf4j.query.algebra.evaluation.QueryOptimizer;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.*;
import org.eclipse.rdf4j.query.algebra.evaluation.util.QueryOptimizerList;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.semagrow.plan.queryblock.AbstractQueryBlock;

/**
 * The default implementation of a {@link QueryPlanner}
 * @author acharal
 * @since 2.0
 */
public class SimpleQueryPlanner implements QueryPlanner {

    @Override
    public Plan compile(QueryRoot query, Dataset dataset, BindingSet bindings) {

        // transformations on logical query.
        rewrite(query.getArg(), dataset, bindings);

        // split query to queryblocks.
        //AbstractQueryBlock blockRoot = blockify(query, dataset, bindings);

        // infer interesting properties for each query block.

        // traverse Blocks and compile them bottom-up.

        return null;
    }

    /**
     * Rewrites the logical expression into a logically-equivalent simpler ``canonical'' expression.
     * @param expr The expression subject to transformation. It will be substituted by the equivalent expression
     *             and therefore must have a parent.
     * @param dataset
     * @param bindings possible bindings for some of the variables in the expression.
     */
    protected void rewrite(TupleExpr expr, Dataset dataset, BindingSet bindings) {

        assert expr.getParentNode() != null;

        QueryOptimizer queryOptimizer =  new QueryOptimizerList(
                new BindingAssigner(),                  // substitute variables with constants if in the given bindingset
                new CompareOptimizer(),                 // substitute Compare with SameTerm if possible
                new SameTermFilterOptimizer(),          // rename variables or replace with constants if filtered with SameTerm
                new ConjunctiveConstraintSplitter(),    // splits Filters And to consecutive applications
                new DisjunctiveConstraintOptimizer(),   // split Filters Or to Union
                new QueryModelNormalizer()              // remove emptysets, singletonsets, transform to DNF (union before joins)
        );

        queryOptimizer.optimize(expr, dataset, bindings);
    }

}
