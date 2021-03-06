package eu.semagrow.commons.voidinfer.config;

import eu.semagrow.commons.voidinfer.VOID.VOIDInferencer;
import eu.semagrow.commons.voidinfer.VOID.VOIDInferencer;
import org.openrdf.sail.Sail;
import org.openrdf.sail.config.SailConfigException;
import org.openrdf.sail.config.SailFactory;
import org.openrdf.sail.config.SailImplConfig;

/**
 * Created by angel on 5/29/14.
 */
public class VOIDInferencerFactory implements SailFactory {

    public static final String SAIL_TYPE = "semagrow:VOIDInferencer";

    public String getSailType() { return SAIL_TYPE; }

    public SailImplConfig getConfig() {
        return new VOIDInferencerConfig();
    }

    public Sail getSail(SailImplConfig sailImplConfig) throws SailConfigException {
        return new VOIDInferencer();
    }
}
