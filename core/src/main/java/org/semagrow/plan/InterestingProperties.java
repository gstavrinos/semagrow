package org.semagrow.plan;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author acharal
 */
public class InterestingProperties implements Cloneable {

    private Collection<RequestedStructureProperties> dataProps = new ArrayList<>();

    @Override
    public Object clone() {
        InterestingProperties newIntProps = new InterestingProperties();
        newIntProps.dataProps.addAll(this.dataProps);
        return newIntProps;
    }

}
