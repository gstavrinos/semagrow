package org.semagrow.plan;

import org.semagrow.plan.util.FilterCollector;
import org.eclipse.rdf4j.query.algebra.*;

import java.util.Collection;


/**
 * Decomposer Context
 * 
 * <p>Holds solution modifiers (such as LIMIT) and FILTER statements
 * that form the context within which query decomposition operates.
 * </p> 
 *  
 * @author Angelos Charalambidis
 * @author Stasinos Konstantopoulos
 */

public class DecomposerContext
{
	public static final long LIMIT_NONE = -2;

    public static final long LIMIT_OVERFLOW = -1;

	private Ordering ordering;

    private Collection<ValueExpr> filters;

    private long limit = DecomposerContext.LIMIT_NONE;

	protected DecomposerContext( TupleExpr expr )
	{
        this.filters = FilterCollector.process( expr );
	}

    public Ordering getOrdering() { return this.ordering; }

    public Collection<ValueExpr> getFilters() { return this.filters; }

    /**
     * Gets the value of the LIMIT modifier
     * @return long the value of the LIMIT modifier, if any; -2 if there is no LIMIT modifier; -1 if the LIMIT modifier overflows long
     */

    public long getLimit() { return this.limit; }

    /**
     * Checks if the query has a LIMIT modifier
     * @return boolean
     */

    public boolean hasLimit() { return this.limit > -2; }

}
