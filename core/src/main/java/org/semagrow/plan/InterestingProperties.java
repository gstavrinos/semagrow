package org.semagrow.plan;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author acharal
 */
public class InterestingProperties implements Cloneable {

    private Set<RequestedDataProperties> dataProps = new HashSet<>();

    private Set<RequestedGlobalDataProperties> globalProps = new HashSet<>();

    @Override
    public InterestingProperties clone() {
        InterestingProperties newIntProps = new InterestingProperties();
        newIntProps.dataProps.addAll(this.dataProps);
        newIntProps.globalProps.addAll(this.globalProps);
        return newIntProps;
    }

    public void addStructureProperties(RequestedDataProperties props) {
        dataProps.add(props);
    }

    public void addGlobalProperties(RequestedGlobalDataProperties props) { globalProps.add(props); }

    public void addInterestingProperties(InterestingProperties props) {
        dataProps.addAll(props.dataProps);
        globalProps.addAll(props.globalProps);
    }


    public Set<RequestedDataProperties> getStructureProperties() {
        return dataProps;
    }

    public Set<RequestedGlobalDataProperties> getGlobalProperties() { return globalProps; }

    public void dropTrivials() {
        {
            Iterator<RequestedDataProperties> it = dataProps.iterator();
            while (it.hasNext()) {
                RequestedDataProperties props = it.next();
                if (props.isTrivial())
                    it.remove();
            }
        }

        {
            Iterator<RequestedGlobalDataProperties> it = globalProps.iterator();
            while (it.hasNext()) {
                RequestedGlobalDataProperties props = it.next();
                if (props.isTrivial())
                    it.remove();
            }
        }
    }

}
