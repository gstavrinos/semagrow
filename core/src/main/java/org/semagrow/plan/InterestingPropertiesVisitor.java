package org.semagrow.plan;

import org.semagrow.plan.queryblock.*;

import java.util.Optional;

/**
 * Traverses the QueryBlock AST and extracts {@link InterestingProperties}
 * for each physical {@link org.eclipse.rdf4j.query.algebra.TupleExpr}
 * @author acharal
 * @since 2.0
 */
public class InterestingPropertiesVisitor extends AbstractQueryBlockVisitor<RuntimeException> {

    @Override
    public void meet(SelectBlock b) {

        InterestingProperties intProps;

        Optional<Ordering> maybeOrdering = b.getOrdering();

        if (maybeOrdering.isPresent()) {
            // we must satisfy an ordering requirement
        }

        if (b.getDuplicateStrategy() == OutputStrategy.ENFORCE) {
            // we might be interested in grouping
        }

        // associate interesting properties.
        //block.setIntProps(intProps);

        for (Quantifier q : b.getQuantifiers()) {
            // push input requirements to children QueryBlocks
            q.getBlock().visit(this);
        }

        super.meet(b);
    }

    @Override
    public void meet(GroupBlock b) {

    }

    @Override
    public void meet(UnionBlock b) {

    }

}