package org.semagrow.plan;

import org.semagrow.plan.queryblock.*;

import java.util.Collection;
import java.util.Optional;

/**
 * Traverses the QueryBlock AST and extracts {@link InterestingProperties}
 * for each physical {@link org.eclipse.rdf4j.query.algebra.TupleExpr}
 * @author acharal
 * @since 2.0
 */
public class InterestingPropertiesVisitor extends AbstractQueryBlockVisitor<RuntimeException> {

    private InterestingProperties intProps = new InterestingProperties();

    @Override
    public void meet(SelectBlock b) {

        InterestingProperties props = coverWithOutput(b, intProps);

        b.setIntProps(props);

        InterestingProperties inputProps = props.clone();

        if (b.getDuplicateStrategy() == OutputStrategy.ENFORCE) {
            inputProps.addStructureProperties(RequestedStructureProperties.forGrouping(b.getOutputVariables()));
        }

        for (Quantifier q : b.getQuantifiers()) {
            intProps = homogenize(inputProps, q.getBlock().getOutputVariables());

            // determine interesting orderings arising from merge-joins

            q.getBlock().visit(this);
        }
    }

    @Override
    public void meet(GroupBlock b) {

        InterestingProperties props = coverWithOutput(b, intProps);

        b.setIntProps(props);

        InterestingProperties inputProps = props.clone();
        inputProps.addStructureProperties(RequestedStructureProperties.forGrouping(b.getGroupedVariables()));

        intProps = homogenize(inputProps, b.getOutputVariables());

        super.meet(b);
    }

    @Override
    public void meet(UnionBlock b) {
        b.setIntProps(intProps);
        super.meet(b);
    }

    @Override
    public void meet(IntersectionBlock b) {
        b.setIntProps(intProps);
        super.meet(b);
    }

    @Override
    public void meet(MinusBlock b) {
        b.setIntProps(intProps);
        super.meet(b);
    }

    protected InterestingProperties coverWithOutput(QueryBlock b, InterestingProperties intProps) {

        InterestingProperties props = new InterestingProperties();
        StructureProperties outputReqs = b.getOutputProperties();
        props.addStructureProperties(outputReqs.asRequestedProperties());

        for (RequestedStructureProperties reqProps : intProps.getStructureProperties()) {
            if (!reqProps.isCoveredBy(outputReqs)) {
                props.addStructureProperties(reqProps);
            }
        }
        props.dropTrivials();

        return props;
    }

    protected InterestingProperties homogenize(InterestingProperties props, Collection<String> variables) {
        return props;
    }
}