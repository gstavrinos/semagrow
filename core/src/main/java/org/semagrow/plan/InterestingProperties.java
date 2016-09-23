package org.semagrow.plan;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author acharal
 */
public class InterestingProperties implements Cloneable {

    private Set<RequestedStructureProperties> dataProps = new HashSet<>();

    @Override
    public InterestingProperties clone() {
        InterestingProperties newIntProps = new InterestingProperties();
        newIntProps.dataProps.addAll(this.dataProps);
        return newIntProps;
    }

    public void addStructureProperties(RequestedStructureProperties props) {
        dataProps.add(props);
    }

    public void addInterestingProperties(InterestingProperties props) {
        dataProps.addAll(props.dataProps);
    }


    public Set<RequestedStructureProperties> getStructureProperties() {
        return dataProps;
    }

    public void dropTrivials() {
        Iterator<RequestedStructureProperties> it = dataProps.iterator();
        while (it.hasNext()) {
            RequestedStructureProperties props = it.next();
            if (props.isTrivial())
                it.remove();
        }
    }

}
