package org.semagrow.plan;

import org.semagrow.plan.operators.BindJoin;
import org.semagrow.plan.operators.HashJoin;
import org.semagrow.plan.operators.SourceQuery;
import org.semagrow.local.LocalSite;
import org.eclipse.rdf4j.query.algebra.*;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link PlanVisitor} that traverses the execution plan tree and updates
 * the properties derived by the operator specifications
 * @author acharal
 */
public class PlanPropertiesUpdater extends AbstractPlanVisitor<RuntimeException> {

    private PlanProperties properties = PlanProperties.defaultProperties();

    static public PlanProperties process(TupleExpr expr) {
        return process(expr, PlanProperties.defaultProperties());
    }

    static public PlanProperties process(TupleExpr expr, PlanProperties initialProperties) {

        PlanPropertiesUpdater updater  = new PlanPropertiesUpdater();
        updater.properties = initialProperties;

        expr.visit(updater);

        if (updater.properties != null)
            return updater.properties;
        else
            return initialProperties;
    }


    public void meet(Order order) throws RuntimeException  {
        //Ordering o = new Ordering(order.getElements());
        //order.getArg().visit(this);
        //properties.setOrdering(o);
    }

    @Override
    public void meet(Group group) throws RuntimeException {
        group.getArg().visit(this);

        // compute ordering
    }

    @Override
    public void meet(Distinct distinct) throws RuntimeException {
        distinct.getArg().visit(this);
    }

    @Override
    public void meet(Projection projection) throws RuntimeException {
        projection.getArg().visit(this);

        //compute ordering
        //Ordering o = this.properties.getOrdering();
        Set<String> sourceNames = new HashSet<String>();

        for (ProjectionElem elem: projection.getProjectionElemList().getElements()) {
            sourceNames.add(elem.getSourceName());
        }

        //Ordering o2 = o.filter(sourceNames);
        //this.properties.setOrdering(o2);
    }

    @Override
    public void meet(Filter filter) throws RuntimeException  {
        filter.getArg().visit(this);
    }

    @Override
    public void meet(BindJoin join) throws RuntimeException  {
        join.getLeftArg().visit(this);
        PlanProperties leftProperties = this.properties;
    }

    @Override
    public void meet(HashJoin join) throws RuntimeException  {

        join.getLeftArg().visit(this);
        PlanProperties leftProperties = this.properties;

        join.getRightArg().visit(this);

        //this.properties;
    }

    @Override
    public void meet(Join join) throws RuntimeException {
        join.getLeftArg().visit(this);
    }

    @Override
    public void meet(SourceQuery query) throws RuntimeException  {
        query.getArg().visit(this);
        this.properties.setSite(LocalSite.getInstance());
    }

    @Override
    public void meet(Union union) throws RuntimeException {
        union.getLeftArg().visit(this);
        union.getRightArg().visit(this);
    }

    @Override
    public void meet(Plan plan) throws RuntimeException {
        this.properties = plan.getProperties().clone();
    }

}