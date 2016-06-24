package org.semagrow.plan;

import org.semagrow.local.LocalSite;
import org.semagrow.plan.operators.SourceQuery;
import org.semagrow.estimator.CardinalityEstimatorResolver;
import org.semagrow.plan.util.BindingSetAssignmentCollector;
import org.semagrow.plan.util.FilterCollector;
import org.semagrow.estimator.CostEstimatorResolver;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.algebra.*;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.algebra.helpers.VarNameCollector;
import org.semagrow.selector.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * The default implementation of the @{link PlanGenerator}
 * @author Angelos Charalambidis
 */
public class SimplePlanGenerator implements PlanGenerator, PlanGenerationContext {

    private SourceSelector sourceSelector;
    private CostEstimatorResolver costEstimatorResolver;
    private CardinalityEstimatorResolver cardinalityEstimatorResolver;

    private DecomposerContext ctx;

    protected Collection<JoinImplGenerator> joinImplGenerators;

    public SimplePlanGenerator(DecomposerContext ctx,
                               SourceSelector selector,
                               CostEstimatorResolver costEstimatorResolver,
                               CardinalityEstimatorResolver cardinalityEstimatorResolver)
    {
        this.ctx = ctx;
        this.sourceSelector = selector;
        this.costEstimatorResolver = costEstimatorResolver;
        this.cardinalityEstimatorResolver = cardinalityEstimatorResolver;

        this.joinImplGenerators = new LinkedList<JoinImplGenerator>();
        this.joinImplGenerators.add(new BindJoinGenerator());
        this.joinImplGenerators.add(new RemoteJoinGenerator());
        //this.joinImplGenerators.add(new HashJoinGenerator());
        //this.joinImplGenerators.add(new MergeJoinGenerator());
    }


    @Override
    public Collection<Plan> accessPlans(TupleExpr expr, BindingSet bindings, Dataset dataset)
    {

        Collection<Plan> plans = new LinkedList<Plan>();


        List<BindingSetAssignment> assignments = BindingSetAssignmentCollector.process(expr);

        for (BindingSetAssignment a : assignments) {
            Set<TupleExpr> exprLabel =  new HashSet<TupleExpr>();
            exprLabel.add(a);
            Plan p = create(exprLabel, a);
            plans.add(p);
        }


        // extract the statement patterns
        List<StatementPattern> statementPatterns = StatementPatternCollector.process(expr);

        // extract the filter conditions of the query

        for (StatementPattern pattern : statementPatterns) {

            // get sources for each pattern
            Collection<SourceMetadata> sources = getSources(pattern, dataset, bindings);

            // apply filters that can be applied to the statementpattern
            TupleExpr e = pattern;

            Set<TupleExpr> exprLabel =  new HashSet<TupleExpr>();
            exprLabel.add(e);

            List<Plan> sourcePlans = new LinkedList<Plan>();

            if (sources.isEmpty()) {
                plans.add(create(exprLabel, new EmptySet()));

            } else {

                for (SourceMetadata sourceMetadata : sources) {
                    //URI source = sourceMetadata.getSites().get(0);
                    //Plan p1 = createPlan(exprLabel, sourceMetadata.target(), source, ctx);
                    // FIXME: Don't use always the first source.
                    Plan p1 = createPlan(exprLabel, sourceMetadata.target().clone(), sourceMetadata);
                    sourcePlans.add(p1);
                }

                Plan p = createUnionPlan(sourcePlans);
                plans.add(p);
            }
        }

        return plans;
    }

    protected Collection<SourceMetadata> getSources(StatementPattern pattern, Dataset dataset, BindingSet bindings) {
        return sourceSelector.getSources(pattern,dataset,bindings);
    }

    @Override
    public Collection<Plan> joinPlans(Collection<Plan> plan1, Collection<Plan> plan2)
    {
        return joinImplGenerators.stream()
                .flatMap(gen ->
                        plan1.stream()
                                .flatMap( p1 ->
                                        plan2.stream().flatMap(p2 ->
                                        {
                                            Set<TupleExpr> s = getKey(p1.getKey(), p2.getKey());
                                            return gen.generate(p1,p2,this).stream()
                                                    .map(j -> create(s,j));
                                        })
                                ))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Plan> finalizePlans(Collection<Plan> plans, PlanProperties properties)
    {
        return plans.stream()
                .map(p -> enforce(p, LocalSite.getInstance()))
                .collect(Collectors.toList());
    }

    @Override
    public Plan enforce(Plan p, Site site) {
        Site s1 = p.getProperties().getSite();
        if (s1.isRemote()) {
            ///// FIXME
            TupleExpr expr = s1.getCapabilities().enforceSite(p);
            if (expr instanceof EmptySet) {
                return create(p.getKey(), new EmptySet());
            }
            if (expr instanceof Filter) {
                TupleExpr newexpr = new Filter(
                        new SourceQuery(((Filter) expr).getArg(), s1),
                        ((Filter) expr).getCondition()
                );
                return create(p.getKey(), newexpr);
            }
            else {
                return create(p.getKey(), new SourceQuery(expr, s1));
            }
            //return create(p.getKey(), new SourceQuery(p, s1));
        } else {
            return p;
        }
    }

    @Override
    public Plan enforce(Plan p, Ordering ordering) {
        if (p.getProperties().getOrdering().isCoverOf(ordering)) {
            return p;
        } else {
            return create(p.getKey(), new Order(p, ordering.getOrderElements()));
        }
    }

    public Plan applyRemainingFilters(Plan e, Collection<ValueExpr> conditions) {
        Collection<ValueExpr> filtersApplied = FilterCollector.process(e);
        Collection<ValueExpr> remainingFilters = getRelevantFiltersConditions(e, conditions);
        remainingFilters.removeAll(filtersApplied);

        SiteCapabilities srcCap = e.getProperties().getSite().getCapabilities();

        Collection<ValueExpr> legitFilters = new LinkedList<>();

        for (ValueExpr cond : remainingFilters) {
            if (srcCap.acceptsFilter(e, cond))
                legitFilters.add(cond);
        }

        return applyFilters(e, legitFilters);
    }

    public Collection<ValueExpr> getRelevantFiltersConditions(Plan e, Collection<ValueExpr> filterConditions)
    {
        Set<String> variables = VarNameCollector.process(e);
        Collection<ValueExpr> relevantConditions = new LinkedList<ValueExpr>();

        for (ValueExpr condition : filterConditions) {
            Set<String> conditionVariables = VarNameCollector.process(condition);
            if (variables.containsAll(conditionVariables))
                relevantConditions.add(condition);
        }

        return relevantConditions;
    }

    public Plan applyFilters(Plan e, Collection<ValueExpr> conditions) {

        if (conditions.isEmpty()) {
            return e;
        }
        TupleExpr expr = e.clone();

        for (ValueExpr condition : conditions)
            expr = new Filter(expr, condition);

        Plan p = new Plan(e.getKey(), expr);
        p.setProperties(SimplePlanProperties.defaultProperties());
        return p;
        //return createPlan(e.getKey(), expr);
    }

    public Plan createUnionPlan(List<Plan> plans)
    {
        Site s = LocalSite.getInstance();
        Optional<Plan> unionedPlan = plans.stream()
                .reduce( (p1,p2)-> create(p2.getKey(), new Union(enforce(p1,s), enforce(p2,s))));

        if (unionedPlan.isPresent())
            return unionedPlan.get();
        else
            throw new AssertionError("the list of plans is empty in createUnionPlan");
    }


    protected Set<TupleExpr> getKey(Set<TupleExpr> id1, Set<TupleExpr> id2) {
        Set<TupleExpr> s = new HashSet<TupleExpr>(id1);
        s.addAll(id2);
        return s;
    }


    public Plan create(Set<TupleExpr> id, TupleExpr e) {

        /*
        TupleExpr e1 = PlanUtil.applyRemainingFilters(e.clone(), ctx.getFilters());

        Plan p = Plan.create(id, e1);

        p.getProperties().setCost(costEstimator.getCost(e, p.getProperties().getSite()));
        p.getProperties().setCardinality(cardinalityEstimator.getCardinality(e, p.getProperties().getSite().getURI()));
        */
        return createPlan(id, e);
        //return p;
    }


    /**
     * Update the properties of a plan
     * @param plan
     */
    protected Plan updatePlan(Plan plan)
    {
        //TupleExpr innerExpr = plan.getArg();

        updatePlanProperties(plan);

        // apply filters that can be applied
        Plan e = applyRemainingFilters(plan, ctx.getFilters());

        updatePlanProperties(e);

        return e;
    }

    protected void updatePlanProperties(Plan plan)
    {
        TupleExpr e = plan.getArg();

        PlanProperties properties = PlanPropertiesUpdater.process(e, plan.getProperties());

        plan.setProperties(properties);

        // update cardinality and cost properties
        cardinalityEstimatorResolver.resolve(plan.getProperties().getSite()).ifPresent(cardinalityEstimator -> {
            properties.setCardinality(cardinalityEstimator.getCardinality(e));
        });

        costEstimatorResolver.resolve(plan.getProperties().getSite()).ifPresent(costEstimator -> {
            properties.setCost(costEstimator.getCost(e));
        });



        plan.setProperties(properties);

    }


    protected Plan createPlan(Set<TupleExpr> planId, TupleExpr innerExpr)
    {
        Plan p = new Plan(planId, innerExpr);
        p.setProperties(SimplePlanProperties.defaultProperties());
        //updatePlanProperties(p);
        return updatePlan(p);
    }

    protected Plan createPlan(Set<TupleExpr> planId, TupleExpr innerExpr, SourceMetadata metadata)
    {
        Site site = metadata.getSites().iterator().next();

        Plan p = new Plan(planId, innerExpr);
        p.setProperties(SimplePlanProperties.defaultProperties());

        p.getProperties().setSite(site);
        updatePlan(p);

        return p;
    }
}
