package org.semagrow.plan.queryblock;

import org.semagrow.plan.InterestingProperties;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;

import java.util.Set;

/**
 * Created by angel on 30/6/2016.
 */
public abstract class AbstractQueryBlock implements QueryBlock {

    private InterestingProperties intProps;

    private OutputStrategy duplicateStrategy = OutputStrategy.PRESERVE;

    public OutputStrategy getDuplicateStrategy() { return duplicateStrategy; }

    public void setDuplicateStrategy(OutputStrategy duplicateStrategy) { this.duplicateStrategy = duplicateStrategy; }

    public boolean hasDuplicates() {
        return getDuplicateStrategy() != OutputStrategy.ENFORCE;
    }

    @Override
    public InterestingProperties getIntProps() { return intProps; }

    @Override
    public Set<String> getOutputVariables() { return null; }

    @Override
    public PlanCollection getPlans() { return null; }

    @Override
    public Plan getBestPlan() { return null; }

    @Override
    public <X extends Exception> void visit(QueryBlockVisitor<X> visitor) throws X {
        visitor.meet(this);
    }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X { }

}
