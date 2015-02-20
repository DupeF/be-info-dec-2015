/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A dataset containing instances.
 *
 */
public class DataSet {

	/**
	 * The instances in this dataset.
	 */
	private Instances instances;

	/**
	 * The attributes. 
	 */
	private FastVector attributes;

	/**
	 * Initializes the new dataset.
	 * @param name The name of the new dataset.
	 * @param attributes The list of the attributes in the dataset.
	 */
	public DataSet(String name, FastVector attributes) {
		this.instances = new Instances(name, attributes, 10);
		this.attributes = attributes;
	}

	/**
	 * Returns the set of instances.
	 * @return The set of instances.
	 */
	public Instances getInstances() {
		return this.instances;
	}

	/**
	 * Returns the attributes.
	 * @return The attributes.
	 */
	public FastVector getAttributes() {
		return this.attributes;
	}

	/**
	 * Sets the index of the attribute that plays the role of the class in this dataset.
	 * @param classIndex The index of the attribute that plays the role of the class in this dataset.
	 */
	public void setClassIndex(int classIndex) {
		this.instances.setClassIndex(classIndex);
	}

	/**
	 * Adds an instance to this dataset.
	 * @param instance Adds an instance to this dataset.
	 */
	public void addInstance(Instance instance) {
		this.instances.add(instance);
	}

}
