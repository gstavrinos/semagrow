package eu.semagrow.core.impl.evaluation;

import eu.semagrow.core.evaluation.FederatedEvaluationStrategy;
import eu.semagrow.core.evaluation.FederatedQueryEvaluationSession;
import eu.semagrow.core.evaluation.QueryExecutor;
import eu.semagrow.core.impl.evaluation.interceptors.InterceptingQueryExecutor;
import eu.semagrow.core.impl.evaluation.interceptors.QueryExecutionInterceptor;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by angel on 7/1/14.
 */
public abstract class FederatedQueryEvaluationSessionImplBase
    extends QueryEvaluationSessionImplBase
    implements FederatedQueryEvaluationSession {


    @Override
    public FederatedEvaluationStrategy getEvaluationStrategy() {
        FederatedEvaluationStrategy strategy = (FederatedEvaluationStrategy)super.getEvaluationStrategy();
        strategy.setQueryExecutor(getQueryExecutor());
        return strategy;
    }


    @Override
    protected abstract FederatedEvaluationStrategy getEvaluationStrategyInternal();

    protected abstract QueryExecutor getQueryExecutorInternal();

    public QueryExecutor getQueryExecutor() {
        QueryExecutor executor = getQueryExecutorInternal();
        attachQueryExecutorInterceptors(executor);
        return executor;
    }

    protected void attachQueryExecutorInterceptors(QueryExecutor executor) {
        if (executor instanceof InterceptingQueryExecutor) {
            attachQueryExecutorInterceptors((InterceptingQueryExecutor)executor);
        }
    }

    protected void attachQueryExecutorInterceptors(InterceptingQueryExecutor executor) {
        Collection<QueryExecutionInterceptor> interceptors = getQueryExecutorInterceptors();

        for (QueryExecutionInterceptor interceptor : interceptors) {
            interceptor.setQueryEvaluationSession(this);
            executor.addEvaluationInterceptor(interceptor);
        }
    }

    protected Collection<QueryExecutionInterceptor> getQueryExecutorInterceptors() {
        return new LinkedList<QueryExecutionInterceptor>();
    }
}
