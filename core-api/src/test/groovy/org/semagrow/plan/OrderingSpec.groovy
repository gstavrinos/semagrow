package org.semagrow.plan

import spock.lang.Specification

/**
 * Created by angel on 29/8/2016.
 */
class OrderingSpec extends Specification {

    def "Should throw exception when Order.NONE is used" () {
        given : def ordering = new Ordering()
        when  : ordering.appendOrdering("x", Order.NONE)
        then  : thrown(IllegalArgumentException)
    }

    def "Append an Order to an empty Ordering" () {
        given : def ordering = new Ordering()
        when  : ordering.appendOrdering("x", order)
        then  : ordering.equals(singletonOrdering("x", order))
        where : order << [Order.ASCENDING, Order.DESCENDING, Order.ANY]
    }

    def "Every Ordering is covered by itself" () {
        expect :
            ordering.isCoveredBy(ordering)
            ordering.isCoveredBy(ordering.clone())
        where :
            ordering << [new Ordering(),
                         singletonOrdering("x", Order.ASCENDING),
                         singletonOrdering("x", Order.DESCENDING),
                         singletonOrdering("x", Order.ANY)]
    }

    def "Empty Ordering is covered by any Ordering" () {
        given  : def emptyOrdering = new Ordering()
        expect :
            emptyOrdering.isCoveredBy(ordering)
        where :
            ordering << [singletonOrdering("x", Order.ASCENDING),
                        singletonOrdering("x", Order.DESCENDING),
                        singletonOrdering("x", Order.ANY)]
    }

    def "Cloned Orderings should be equals" () {
        expect : ordering.equals(ordering.clone())
        where  : ordering << [singletonOrdering("x", Order.ASCENDING),
                              singletonOrdering("x", Order.DESCENDING),
                              singletonOrdering("x", Order.ANY)]
    }

    def singletonOrdering(String var, Order o) {
        def ord = new Ordering()
        ord.appendOrdering(var, o)
        ord
    }

}
