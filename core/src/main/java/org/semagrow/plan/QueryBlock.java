package org.semagrow.plan;

import org.eclipse.rdf4j.query.algebra.QueryModelNode;
import org.eclipse.rdf4j.query.algebra.QueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

import java.util.Set;

/**
 * Created by angel on 30/6/2016.
 */
public class QueryBlock implements TupleExpr {

    private TupleExpr e;

    public QueryBlock(TupleExpr e) { this.e = e; }

    public TupleExpr getRoot() { return e; }

    @Override
    public Set<String> getBindingNames() {
        return e.getBindingNames();
    }

    @Override
    public Set<String> getAssuredBindingNames() {
        return e.getAssuredBindingNames();
    }

    @Override
    public <X extends Exception> void visit(QueryModelVisitor<X> visitor) throws X {
        e.visit(visitor);
    }

    public <X extends Exception> void visit(QueryBlockVisitor<X> visitor) throws X {
        visitor.meet(this);
    }

    @Override
    public <X extends Exception> void visitChildren(QueryModelVisitor<X> visitor) throws X {
        e.visitChildren(visitor);
    }

    public <X extends Exception> void visitChildren(QueryBlockVisitor<X> visitor) throws X {
        e.visit(new QueryBlockVisitorAdapter<X>(visitor));
    }

    @Override
    public QueryModelNode getParentNode() {
        return null;
    }

    @Override
    public void setParentNode(QueryModelNode parent) {

    }

    @Override
    public void replaceChildNode(QueryModelNode current, QueryModelNode replacement) {

    }

    @Override
    public void replaceWith(QueryModelNode replacement) {

    }

    @Override
    public String getSignature() {
        return "QueryBlock";
    }

    @Override
    public TupleExpr clone() {
        return new QueryBlock(e.clone());
    }

    private class QueryBlockVisitorAdapter<X extends Exception> extends AbstractQueryModelVisitor<X> {

        private QueryBlockVisitor<X> blockVisitor;

        public QueryBlockVisitorAdapter(QueryBlockVisitor<X> visitor) {
            blockVisitor = visitor;
        }

        @Override
        public void meetNode(QueryModelNode node) throws X {
            if (node instanceof QueryBlock)  {
                blockVisitor.meet((QueryBlock)node);
            } else
                super.meetNode(node);
        }
    }
}
