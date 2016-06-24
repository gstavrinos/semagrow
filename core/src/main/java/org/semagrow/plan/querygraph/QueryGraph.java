package org.semagrow.plan.querygraph;

import org.eclipse.rdf4j.query.algebra.TupleExpr;

import java.util.*;

/**
 * Created by angel on 12/5/2015.
 */
public class QueryGraph
{
    private Collection<QueryEdge> edges = new LinkedList<QueryEdge>();

    private Collection<TupleExpr> vertices = new LinkedList<TupleExpr>();

    public QueryGraph() { }

    public Collection<TupleExpr> getVertices() { return vertices; }

    public Collection<QueryEdge> getEdges() { return edges; }

    public Collection<QueryEdge> getOutgoingEdges(TupleExpr v) {
        Collection<QueryEdge> outgoingEdges = new LinkedList<QueryEdge>();
        for (QueryEdge e : getEdges()) {
            if (e.getFrom().equals(v))
                outgoingEdges.add(e);
        }
        return outgoingEdges;
    }

    public Collection<QueryEdge> getOutgoingEdges(Collection<TupleExpr> v)
    {
        Collection<QueryEdge> outgoingEdges = new LinkedList<>();

        for (TupleExpr e : v)
        {
            outgoingEdges.addAll(getOutgoingEdges(e));
        }
        return outgoingEdges;
    }

    public void addEdge(TupleExpr v1, TupleExpr v2, QueryPredicate pred)
    {
        QueryEdge e = new QueryEdge(v1,v2,pred);
        edges.add(e);
    }

    public void addVertex(TupleExpr expr) { this.vertices.add(expr); }

    /*
    public Collection<QueryGraph> getConnectedComponents() {

    }*/

    /*
    public Collection<QueryPredicate> getFilters(){ }
    */

    /* solution modifiers

    public Optional<Ordering> getOrdering() { }

    public Optional<Grouping> getGrouping() { }

    public Optional<Long> getLimit() { }

    public Optional<Long> getOffset() { }

    public Set<String> getProjection() { }

    */

}