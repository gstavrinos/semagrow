package org.semagrow.plan;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.algebra.*;
import org.eclipse.rdf4j.query.algebra.QueryRoot;
import org.eclipse.rdf4j.query.algebra.evaluation.QueryOptimizer;
import org.eclipse.rdf4j.query.algebra.evaluation.impl.*;
import org.eclipse.rdf4j.query.algebra.evaluation.util.QueryOptimizerList;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

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
        QueryBlock blockRoot = blockify(query, dataset, bindings);

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

    protected QueryBlock blockify(QueryRoot expr, Dataset dataset, BindingSet bindings) {
        QueryBlockCreator queryBlockifier = new QueryBlockCreator();
        try {
            expr.visit(queryBlockifier);
        } catch (Exception e) {
            throw new AssertionError("Unable to perform blockification for the expression " + expr);
        }
        return queryBlockifier.currentBlock;
    }

    // BGPBlock+LeftJoin, Union, Intersection, Difference,
    //
    class QueryBlockCreator extends AbstractQueryModelVisitor<Exception> {

        private TupleExpr currentRoot;
        private QueryBlock currentBlock;

        @Override
        public void meet(QueryRoot q) {
            currentBlock = new QueryBlock(q);
            currentRoot = q;
        }

        @Override
        public void meet(Join j) throws Exception {

            TupleExpr oldRoot = currentRoot;

            if (currentRoot instanceof Join
                    || currentRoot instanceof LeftJoin
                    || currentRoot instanceof Filter)
            {
                super.meet(j);
            } else {
                currentRoot = j;
                super.meet(j);
                QueryBlock b = new QueryBlock(j);
                j.replaceWith(b);
            }
            currentRoot = oldRoot;
        }

        @Override
        public void meet(LeftJoin j) throws Exception {

            TupleExpr oldRoot = currentRoot;

            if (currentRoot instanceof Join
                    || currentRoot instanceof LeftJoin
                    || currentRoot instanceof Filter)
            {
                super.meet(j);
            } else {
                currentRoot = j;
                super.meet(j);
                QueryBlock b = new QueryBlock(j);
                j.replaceWith(b);
            }
            currentRoot = oldRoot;
        }

        @Override
        public void meet(Filter f) throws Exception {
            TupleExpr oldRoot = currentRoot;

            if (currentRoot instanceof Join
                    || currentRoot instanceof LeftJoin
                    || currentRoot instanceof Filter)
            {
                super.meet(f);
            } else {
                currentRoot = f;
                super.meet(f);
                QueryBlock b = new QueryBlock(f);
                f.replaceWith(b);
            }
            currentRoot = oldRoot;
        }

        @Override
        public void meet(Group g) throws Exception {
            TupleExpr oldRoot = currentRoot;

            if (currentRoot instanceof Group)
            {
                super.meet(g);
            } else {
                currentRoot = g;
                super.meet(g);
                QueryBlock b = new QueryBlock(g);
                g.replaceWith(b);
            }
            currentRoot = oldRoot;
        }


        @Override
        public void meet(Union u) throws Exception {
            TupleExpr oldRoot = currentRoot;

            if (currentRoot instanceof Union)
            {
                super.meet(u);
            } else {
                currentRoot = u;
                super.meet(u);
                QueryBlock b = new QueryBlock(u);
                u.replaceWith(b);
            }
            currentRoot = oldRoot;
        }

        @Override
        public void meet(Intersection i) throws Exception {
            TupleExpr oldRoot = currentRoot;
            currentRoot = i;
            super.meet(i);
            QueryBlock b = new QueryBlock(i);
            i.replaceWith(b);
            currentRoot = oldRoot;
        }

        @Override
        public void meet(Difference d) throws Exception {
            TupleExpr oldRoot = currentRoot;
            currentRoot = d;
            super.meet(d);
            QueryBlock b = new QueryBlock(d);
            d.replaceWith(b);
            currentRoot = oldRoot;
        }

        @Override
        protected void meetSubQueryValueOperator(SubQueryValueOperator o) {
            TupleExpr e = o.getSubQuery();
            TupleExpr oldRoot = currentRoot;
            currentRoot = e;
            QueryBlock b = new QueryBlock(e);
            o.replaceChildNode(e, b);
            currentRoot = oldRoot;
        }

        @Override
        public void meet(Projection p) throws Exception {
            super.meet(p);
        }

        @Override
        public void meet(Extension e) throws Exception {
            super.meet(e);
        }

    }

}
