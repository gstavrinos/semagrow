/**
 * 
 */
package eu.semagrow.stack.modules.utils;

import java.util.ArrayList;

/**
 * @author Giannis Mouchakis
 *
 */
public class InstanceIndex {
	
	private ArrayList<EquivalentURI> equivalent_uris;

	/**
	 * 
	 */
	public InstanceIndex() {
		super();
	}
	
	/**
	 * @param equivalent_uris the equivalent_uris to set
	 */
	public void setEquivalent_uris(ArrayList<EquivalentURI> equivalent_uris) {
		this.equivalent_uris = equivalent_uris;
	}

	public ArrayList<SesameStoreAnswer> getEndpoints() {
		return new ArrayList<SesameStoreAnswer>();
	}

}