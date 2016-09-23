package org.semagrow.plan.queryblock;

import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;

import java.util.Set;

/**
 * Created by angel on 7/9/2016.
 */
public class MinusBlock extends AbstractQueryBlock {

    private QueryBlock left;

    private QueryBlock right;

    public MinusBlock(QueryBlock left, QueryBlock right) {
        assert left != null && right != null;
        this.left = left;
        this.right = right;
    }

    public QueryBlock getLeft() { return left; }

    public QueryBlock getRight() { return right; }

    @Override
    public Set<String> getOutputVariables() { return left.getOutputVariables(); }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X {
        for (QueryBlock b : new QueryBlock[]{left, right})
            b.visit(visitor);
    }

    public boolean hasDuplicates() { return false; }

    public PlanCollection getPlans() { return null; }

    public Plan getBestPlan() { return null; }

}
