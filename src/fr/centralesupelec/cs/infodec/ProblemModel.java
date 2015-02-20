/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * This class represents the definition of a classification problem.
 *
 */
public class ProblemModel {
	/**
	 * The attributes (features) of the instances.
	 */
	private FastVector attributes;

	/**
	 * The index of the class labels in the attribute vector.
	 */
	private int classIndex;


	/**
	 * Creates a new empty {@code ProblemModel}.
	 */
	public ProblemModel() {
		this.attributes = new FastVector();
		this.classIndex = -1;
	}

	/**
	 * Adds a new numeric attribute.
	 * 
	 * @param name The name of the new attribute.
	 */
	public void addAttribute(String name) {
		this.attributes.addElement(new Attribute(name));
	}

	/**
	 * Adds a new nominal attribute.
	 * @param name The name of the new attribute.
	 * @param possibleValues The possible values of the attribute.
	 */
	public void addAttribute(String name, String[] possibleValues) {
		FastVector fvNominalVal = new FastVector(possibleValues.length);
		for ( String value : possibleValues )
			fvNominalVal.addElement(value);

		this.attributes.addElement(new Attribute(name, fvNominalVal));
	}

	/**
	 * Sets the class labels.
	 *  
	 * @param name The name of the class.
	 * @param classLabels The class labels.
	 */
	public void addClassLabel(String name, String[] classLabels) {

		FastVector fvClassVal = new FastVector(classLabels.length);
		for ( String label : classLabels )
			fvClassVal.addElement(label);


		this.attributes.addElement(new Attribute(name, fvClassVal));
		this.classIndex = this.attributes.size() - 1;
	}

	/**
	 * Returns the index of the class attribute.
	 * @return The index of the class attribute.
	 */
	public int getClassIndex() {
		return this.classIndex;
	}

	/**
	 * Returns the attributes (list of attributes + class labels).
	 * 
	 * @return The attributes.
	 */
	public FastVector getAttributes() {
		return this.attributes;
	}

}
