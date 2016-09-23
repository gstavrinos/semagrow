package org.semagrow.plan.queryblock;

import org.semagrow.plan.InterestingProperties;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;
import org.semagrow.plan.StructureProperties;

import java.util.Set;

/**
 * @author acharal
 * @since 2.0
 */
public interface QueryBlock {

    Set<String> getOutputVariables();

    InterestingProperties getIntProps();

    void setIntProps(InterestingProperties intProps);

    PlanCollection getPlans();

    Plan getBestPlan();

    <X extends Exception> void visit(QueryBlockVisitor<X> visitor) throws X;

    <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X;

    boolean hasDuplicates();

    OutputStrategy getDuplicateStrategy();

    void setDuplicateStrategy(OutputStrategy duplicateStrategy);

    StructureProperties getOutputProperties();

}
