package org.semagrow.plan.queryblock;

import org.semagrow.plan.InterestingProperties;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;

import java.util.Set;

/**
 * Created by angel on 31/8/2016.
 */
public interface QueryBlock {

    Set<String> getOutputVariables();

    InterestingProperties getIntProps();

    PlanCollection getPlans();

    Plan getBestPlan();

    <X extends Exception> void visit(QueryBlockVisitor<X> visitor) throws X;

    <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X;

    boolean hasDuplicates();

    OutputStrategy getDuplicateStrategy();

    void setDuplicateStrategy(OutputStrategy duplicateStrategy);
}
