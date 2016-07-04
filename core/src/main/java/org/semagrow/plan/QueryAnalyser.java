package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.*;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by angel on 24/6/2016.
 */
public class QueryAnalyser extends AbstractQueryModelVisitor<RuntimeException> {

    private Map<String, ValueExpr> extensions = new HashMap<>();

    private boolean is_reduced = false;

    private boolean is_distinct = false;

    private List<OrderElem> orderList = new ArrayList<>();

    private boolean has_limit;

    private long limit;

    private boolean has_offset;

    private long offset;

    private List<ProjectionElem> projections = new ArrayList<>();

    public QueryAnalyser(TupleExpr expr) {
        expr.visit(this);
    }

    public boolean hasLimit() { return has_limit; }

    public boolean hasOffset() { return has_offset; }

    public long getLimit() { return limit; }

    public long getOffset() { return offset; }

    public boolean isDistinct() { return is_distinct; }

    public boolean isReduced() { return is_reduced; }

    public List<OrderElem> getOrdering() { return orderList; }

    public Map<String, ValueExpr> getExtensions() { return extensions; }

    public List<ProjectionElem> getProjection() { return projections; }

    @Override
    public void meet(Distinct distinct) {
        is_distinct = true;
    }

    @Override
    public void meet(Reduced reduced) {
        is_reduced = true;
    }

    @Override
    public void meet(Slice slice) {

        has_limit = slice.hasLimit();

        if (hasLimit())
            limit = slice.getLimit();

        has_offset = slice.hasOffset();

        if (hasOffset())
            offset = slice.getOffset();
    }

    @Override
    public void meet(OrderElem orderElem) {
        orderList.add(orderElem);
    }

    @Override
    public void meet(ProjectionElemList projectionElemList){
        projections.addAll(projectionElemList.getElements());
    }

    @Override
    public void meet(ExtensionElem extensionElem) {
        extensions.put(extensionElem.getName(), extensionElem.getExpr());
    }

}
