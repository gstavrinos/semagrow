package org.semagrow.plan.queryblock;

import org.semagrow.plan.InterestingProperties;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;
import org.semagrow.plan.DataProperties;

import java.util.Set;

/**
 * @author acharal
 */
public abstract class AbstractQueryBlock implements QueryBlock {

    private InterestingProperties intProps;

    private OutputStrategy duplicateStrategy = OutputStrategy.PRESERVE;

    private DataProperties structureProps = new DataProperties();

    public OutputStrategy getDuplicateStrategy() { return duplicateStrategy; }

    public void setDuplicateStrategy(OutputStrategy duplicateStrategy) {

        // update structural output properties before set the strategy
        if (duplicateStrategy == OutputStrategy.ENFORCE) {
            this.structureProps.addUnique(this.getOutputVariables());
        } else {
            if (getDuplicateStrategy() == OutputStrategy.ENFORCE)
                this.structureProps.removeUnique(this.getOutputVariables());
        }

        this.duplicateStrategy = duplicateStrategy;
    }

    public boolean hasDuplicates() {
        return getDuplicateStrategy() != OutputStrategy.ENFORCE;
    }

    @Override
    public InterestingProperties getInterestingProperties() { return intProps; }

    @Override
    public void setInterestingProperties(InterestingProperties intProps) { this.intProps = intProps; }

    @Override
    public abstract Set<String> getOutputVariables();

    @Override
    public abstract PlanCollection getPlans();

    @Override
    public abstract Plan getBestPlan();

    @Override
    public <X extends Exception> void visit(QueryBlockVisitor<X> visitor) throws X {
        visitor.meet(this);
    }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X { }


    public DataProperties getOutputDataProperties() {
        return structureProps;
    }

    protected void setOutputProperties(DataProperties props) {
        this.structureProps = props;
    }

}
