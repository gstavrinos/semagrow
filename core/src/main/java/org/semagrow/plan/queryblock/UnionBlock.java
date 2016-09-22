package org.semagrow.plan.queryblock;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by angel on 6/9/2016.
 */
public class UnionBlock extends AbstractQueryBlock {

    private Collection<QueryBlock> blocks;

    public UnionBlock(QueryBlock...block) {
        blocks = Collections.emptyList();
        this.addAll(Arrays.asList(block));
    }

    public UnionBlock(Collection<QueryBlock> blocks) {
        this();
        this.addAll(blocks);
    }

    @Override
    public Set<String> getOutputVariables() {
        return blocks.stream()
                .flatMap(b -> b.getOutputVariables().stream())
                .distinct()
                .collect(Collectors.toSet());
    }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X {
        for (QueryBlock b : blocks)
            visitor.meet(b);
    }

    public Collection<QueryBlock> getBlocks() { return blocks; }

    public void add(QueryBlock b) { blocks.add(b); }

    public void addAll(Collection<QueryBlock> bs) { blocks.addAll(bs); }

    public boolean hasDuplicates() { return true; }

}
