package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.*;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.semagrow.estimator.CardinalityEstimatorResolver;
import org.semagrow.estimator.CostEstimatorResolver;
import org.semagrow.selector.Site;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by angel on 24/6/2016.
 */
public class SimplePlanFactory implements PlanFactory {

    private final CostEstimatorResolver costEstimatorResolver;
    private final CardinalityEstimatorResolver cardinalityEstimatorResolver;

    public SimplePlanFactory(CostEstimatorResolver costEstimatorResolver,
                             CardinalityEstimatorResolver cardinalityEstimatorResolver)
    {
        this.costEstimatorResolver = costEstimatorResolver;
        this.cardinalityEstimatorResolver = cardinalityEstimatorResolver;
    }


    public Plan create(TupleExpr expr) {
        return create(expr, SimplePlanProperties.defaultProperties());
    }

    public Plan create(TupleExpr id, TupleExpr expr) {
        return create(id, expr, SimplePlanProperties.defaultProperties());
    }


    public Plan create(TupleExpr expr, PlanProperties initialProps) {
        return create(getId(expr), expr, initialProps);
    }

    public Plan create(TupleExpr id, TupleExpr expr, PlanProperties initialProps) {
        return create(Collections.singleton(id), expr, initialProps);
    }

    protected Plan create(Set<TupleExpr> planId, TupleExpr expr, PlanProperties initialProps)
    {
        PlanProperties props = PlanPropertiesUpdater.process(expr, initialProps);

        Site s = props.getSite();

        cardinalityEstimatorResolver.resolve(s)
                .ifPresent(ce -> props.setCardinality(ce.getCardinality(expr)));

        costEstimatorResolver.resolve(s)
                .ifPresent(ce -> props.setCost(ce.getCost(expr)));

        Plan p = new Plan(planId, expr);
        p.setProperties(props);

        return p;
    }

    protected Set<TupleExpr> getId(TupleExpr expr) {
        Set<TupleExpr> id = new HashSet<>();
        expr.visit(new AbstractQueryModelVisitor<RuntimeException>() {

            @Override
            public void meet(StatementPattern node) throws RuntimeException {
                id.add(node);
            }

            @Override
            public void meet(BindingSetAssignment node) throws RuntimeException {
                id.add(node);
            }

            @Override
            public void meetOther(QueryModelNode node) {
                if (node instanceof Plan) {
                    id.addAll(((Plan)node).getKey());
                }
            }
        });

        return id;
    }

}
