package org.semagrow.plan.querygraph;

import org.eclipse.rdf4j.query.algebra.LeftJoin;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.semagrow.estimator.CardinalityEstimatorResolver;
import org.semagrow.estimator.CostEstimatorResolver;
import org.semagrow.local.LocalSite;
import org.semagrow.plan.Plan;
import org.semagrow.plan.SimplePlanGenerator;
import org.semagrow.selector.SourceSelector;

import java.util.*;

/**
 * Created by angel on 23/6/2016.
 */
public class QueryGraphPlanGenerator extends SimplePlanGenerator {

    private QueryGraphDecomposerContext ctx;


    private org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( this.getClass() );


    public QueryGraphPlanGenerator(QueryGraphDecomposerContext ctx,
                                   SourceSelector selector,
                                   CostEstimatorResolver costEstimatorResolver,
                                   CardinalityEstimatorResolver cardinalityEstimatorResolver)
    {
        super(ctx, selector, costEstimatorResolver, cardinalityEstimatorResolver);
        this.ctx = ctx;
    }

    @Override
    public Collection<Plan> joinPlans(Collection<Plan> p1, Collection<Plan> p2)
    {
        Collection<QueryPredicate> preds = getValidPredicatesFor(p1, p2);

        Collection<Plan> plans = new LinkedList<Plan>();

        for (QueryPredicate p : preds) {
            plans.addAll(combineWith(p1, p2, p));
        }

        return plans;

    }


    private Collection<QueryPredicate> getValidPredicatesFor(Collection<Plan> p1, Collection<Plan> p2) {

        if (p1.isEmpty() || p2.isEmpty())
            return Collections.emptyList();

        Set<TupleExpr> r1 = p1.iterator().next().getKey();
        Set<TupleExpr> r2 = p2.iterator().next().getKey();

        Collection<QueryPredicate> predicates = new LinkedList<>();

        Collection<QueryEdge> edges = ctx.getQueryGraph().getOutgoingEdges(r1);
        for (QueryEdge e : edges) {
            if (r2.contains(e.getTo())) {
                QueryPredicate p = e.getPredicate();
                Set<TupleExpr> r = new HashSet<>(r1);
                r.addAll(r2);
                if (p.canBeApplied(r))
                    predicates.add(p);
            }
        }
        return predicates;
    }


    public Collection<Plan> combineWith(Collection<Plan> p1, Collection<Plan> p2, QueryPredicate pred)
    {
        if (pred instanceof JoinPredicate)
            return combineWith(p1,p2, (JoinPredicate)pred);
        else if (pred instanceof LeftJoinPredicate)
            return combineWith(p1,p2, (LeftJoinPredicate)pred);
        else
            return Collections.emptyList();
    }


    public Collection<Plan> combineWith(Collection<Plan> p1, Collection<Plan> p2, JoinPredicate pred)
    {
        Collection<Plan> plans =  super.joinPlans(p1,p2);
        plans.addAll(super.joinPlans(p2,p1));
        return plans;
    }

    public Collection<Plan> combineWith(Collection<Plan> p1, Collection<Plan> p2, LeftJoinPredicate pred)
    {
        Collection<Plan> plans =  new LinkedList<Plan>();

        for (Plan pp1 : p1) {
            for (Plan pp2 : p2) {
                Set<TupleExpr> k = getKey(pp1.getKey(), pp2.getKey());

                if (pp1.getProperties().getSite().isRemote() &&
                        pp2.getProperties().getSite().isRemote() &&
                        pp1.getProperties().getSite().equals(pp2.getProperties().getSite())) {


                    Plan ppp = create(k, new LeftJoin(pp1, pp2));
                    logger.debug("Plan added {}", ppp);
                    plans.add(ppp);
                }
                plans.add(create(k, new LeftJoin(enforce(pp1, LocalSite.getInstance()), enforce(pp2, LocalSite.getInstance()))));
            }
        }
        return plans;
    }

}
