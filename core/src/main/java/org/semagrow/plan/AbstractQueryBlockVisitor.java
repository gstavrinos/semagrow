package org.semagrow.plan;

/**
 * Created by angel on 30/6/2016.
 */
public class AbstractQueryBlockVisitor<X extends Exception> implements QueryBlockVisitor<X> {

    @Override
    public void meet(QueryBlock b) throws X {
        meetBlock(b);
    }


    protected void meetBlock(QueryBlock b) throws X {
        b.visitChildren(this);
    }

}
