package org.semagrow.plan;

/**
 * Created by angel on 30/6/2016.
 */
public interface QueryBlockVisitor<X extends Exception> {

    void meet(QueryBlock b) throws X;

}
