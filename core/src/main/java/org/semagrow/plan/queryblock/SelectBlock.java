package org.semagrow.plan.queryblock;

import org.eclipse.rdf4j.query.algebra.ValueExpr;
import org.semagrow.plan.Ordering;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by angel on 6/9/2016.
 */
public class SelectBlock extends AbstractQueryBlock {

    // the name of the variables that will be appear in the output
    // this is a mapping between the source name of the variable and the target name
    private Map<String, ValueExpr> outputVars;

    // graph of queryblocks connected with Predicates
    private Collection<Predicate> predicates;

    private Collection<Quantifier> quantifiers;

    private Optional<Ordering> ordering = Optional.empty();

    private OutputStrategy orderingStrategy = OutputStrategy.PERMIT;

    private Optional<Long> limit = Optional.empty();

    private Optional<Long> offset = Optional.empty();

    public SelectBlock() {
        outputVars = new HashMap<>();
        predicates = new LinkedList<>();
        quantifiers = new LinkedList<>();
    }

    public Set<String> getOutputVariables() { return outputVars.keySet(); }

    public Optional<ValueExpr> getLocal(String name) {
        return Optional.ofNullable(outputVars.get(name));
    }

    public Map<String,ValueExpr> getLocals() { return outputVars; }

    public boolean hasDuplicates() {

        // if it is enforced by this block then the output do not contain any duplicates.
        if (getDuplicateStrategy() == OutputStrategy.ENFORCE) {
            return false;
        } else if (getDuplicateStrategy() == OutputStrategy.PRESERVE) {

            // if it preserved the strategy of the constituent blocks then
            // check whether there is a single EACH quantifier and that
            // quantifier has not duplicates. In that case, this block has
            // also no duplicates.

            Collection<Quantifier> qs = getQuantifiers().stream().filter(q -> q.isFrom()).collect(Collectors.toList());

            if (qs.size() == 1)
                return !qs.stream().noneMatch(q -> q.getBlock().hasDuplicates());
        }

        // in any other case we cannot guarantee that there will be no duplicates.
        return true;
    }

    public Optional<Ordering> getOrdering() { return this.ordering; }

    public void setOrdering(Ordering o) { this.ordering = Optional.of(o); }

    public boolean hasOrdering() { return ordering.isPresent(); }

    public void setLimit(Long l) { limit = Optional.of(l); }

    public void setOffset(Long l) { offset = Optional.of(l); }

    @Override
    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X {
        for (Quantifier q : quantifiers)
            q.getBlock().visit(visitor);
    }

    public Quantifier addFromBlock(QueryBlock block) {
        Quantifier q = new Quantifier(block, Quantifier.Quantification.EACH);
        quantifiers.add(q);
        q.setParent(this);
        return q;
    }

    public Quantifier addExistentialBlock(QueryBlock block) {
        Quantifier q = new Quantifier(block, Quantifier.Quantification.ANY);
        quantifiers.add(q);
        q.setParent(this);
        return q;
    }

    public Quantifier addUniversalBlock(QueryBlock block) {
        Quantifier q = new Quantifier(block, Quantifier.Quantification.ALL);
        quantifiers.add(q);
        q.setParent(this);
        return q;
    }

    public void removeQuantifier(Quantifier q) {
        if (q.getParent().equals(this))
            this.quantifiers.remove(q);
    }

    public void moveQuantifier(Quantifier q) {
        if (!q.getParent().equals(this)) {
            SelectBlock b = (SelectBlock)q.getParent();
            b.quantifiers.remove(q);
            q.setParent(this);
            this.quantifiers.add(q);
        }
    }

    public Collection<Quantifier> getQuantifiers() { return quantifiers; }

    public Predicate addPredicate(Predicate p) {
        predicates.add(p);
        return p;
    }

    public void removePredicate(Predicate p) {
        predicates.remove(p);
    }

    public void movePredicate(Predicate p) {
        this.predicates.add(p);
    }

    public Collection<Predicate> getPredicates() { return predicates; }

    public Collection<Predicate> getPredicates(Quantifier q) {
        return predicates.stream()
                .filter(p -> p.getEL().contains(q))
                .collect(Collectors.toList());
    }

    public void addProjection(String var, ValueExpr expr) {
        if (var == null)
            throw new IllegalArgumentException("Quantified variable " + var.toString() + " not a member of the block");

        outputVars.put(var, expr);
    }

}
