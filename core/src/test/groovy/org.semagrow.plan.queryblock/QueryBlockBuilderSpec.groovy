package org.semagrow.plan.queryblock

import spock.lang.Specification;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParserFactory;

/**
 * Created by angel on 12/9/2016.
 */
class QueryBlockBuilderSpec extends Specification {

    def "single pattern block" () {
        setup :
            def visitor = new SelectMergeVisitor()
        when :
            //def queryStr = "SELECT ?s { ?s ?p ?o . ?s ?p2 ?o2 . ?s ?p3 ?o3 . } GROUP BY ?s ORDER BY ASC(?s)"
            def queryStr = "SELECT ?s1 ?c { {SELECT ((?s + ?o) AS ?c) { ?s ?p ?o }} . ?s1 ?p1 ?c }"
            def block = QueryBlockBuilder.build(parse(queryStr))
            block.visit(visitor);
        then :
            block instanceof SelectBlock
            ((SelectBlock)block).getOutputVariables().size() == 2
            ((SelectBlock)block).getOutputVariables().containsAll(Arrays.asList("s1", "c"))
    }

    def parse(queryStr) {
        def factory = new SPARQLParserFactory()
        def parser  = factory.getParser()
        def parsedQuery = parser.parseQuery(queryStr, "http://test")
        parsedQuery.getTupleExpr()
    }
}
