package org.semagrow.algebra;

import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by angel on 23/6/2016.
 */
public class TupleExprs {

    public static Set<String> getFreeVariables(TupleExpr expr){
        final Set<String> res = new HashSet<String>();
        expr.visit(new AbstractQueryModelVisitor<RuntimeException>() {

            @Override
            public void meet(Var node)
                    throws RuntimeException {
                // take only real vars, i.e. ignore blank nodes
                if (!node.hasValue() && !node.isAnonymous())
                    res.add(node.getName());
            }
        });
        return res;
    }


}
