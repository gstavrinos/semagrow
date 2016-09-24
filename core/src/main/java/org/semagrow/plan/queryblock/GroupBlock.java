package org.semagrow.plan.queryblock;

import com.google.common.collect.Sets;
import org.eclipse.rdf4j.query.algebra.AggregateOperator;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;

import java.util.*;

/**
 * Created by angel on 6/9/2016.
 */
public class GroupBlock extends AbstractQueryBlock {

    private Set<String> groupByVariables;

    private Map<String, AggregateOperator> aggregations;

    private QueryBlock sourceBlock;

    public GroupBlock(QueryBlock arg, Collection<String> groups) {
        assert arg != null;
        this.sourceBlock = arg;

        this.groupByVariables = new HashSet<>();
        this.aggregations = new HashMap<>();
        this.groupByVariables.addAll(groups);

        setDuplicateStrategy(OutputStrategy.ENFORCE);
    }

    public void addAggregation(String val, AggregateOperator op){
        aggregations.put(val, op);
    }

    public Set<String> getOutputVariables() {
        return Sets.union(groupByVariables, aggregations.keySet());
    }

    public Set<String> getGroupedVariables() { return groupByVariables; }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X {
        sourceBlock.visit(visitor);
    }

    public boolean hasDuplicates() { return false; }

    public PlanCollection getPlans() { return null; }

    public Plan getBestPlan() { return null; }
}
