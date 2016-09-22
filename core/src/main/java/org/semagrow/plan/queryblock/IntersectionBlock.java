package org.semagrow.plan.queryblock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by angel on 7/9/2016.
 */
public class IntersectionBlock extends AbstractQueryBlock {

    private Collection<QueryBlock> blocks;

    public IntersectionBlock(QueryBlock...block) {
        blocks = Collections.emptyList();
        this.addAll(Arrays.asList(block));
    }

    public Set<String> getOutputVariables() {

        return blocks.stream()
                .flatMap(b -> b.getOutputVariables().stream())
                .distinct()
                .collect(Collectors.toSet());
    }

    public void add(QueryBlock b) { blocks.add(b); }

    public void addAll(Collection<QueryBlock> bs) { blocks.addAll(bs); }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X {
        for (QueryBlock b : blocks)
            b.visit(visitor);
    }

    public boolean hasDuplicates() {
        if (getDuplicateStrategy() == OutputStrategy.PRESERVE) {
            // if intersection preserves the duplicates then
            // the only case that there are no duplicates is when
            // all its constituent blocks do not have duplicates.
            return !blocks.stream().noneMatch(b -> b.hasDuplicates());
        } else {
            return true;
        }
    }

}
