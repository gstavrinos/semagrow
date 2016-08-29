package org.semagrow.plan;

/**
 * Traverses an AST and extracts {@link InterestingProperties}
 * for each physical {@link org.eclipse.rdf4j.query.algebra.TupleExpr}
 * @author acharal
 * @since 2.0
 */
public class InterestingPropertiesVisitor {

    // distinct indicates a grouping on the whole row  (if grouped then a reduce operator can be used)
    // group by indicates a grouping on the grouping columns (if grouped no hashing is needed)
    // order by indicates an ordering
    // merge join indicates an ordering on the join columns.

}