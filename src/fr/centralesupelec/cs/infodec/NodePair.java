package fr.centralesupelec.cs.infodec;

import org.neo4j.graphdb.Node;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

/**
 * A pair of nodes
 *
 */
public class NodePair {
	
	/**
	 * The first node.
	 */
	private Node first;

	/**
	 * The second node.
	 */
	private Node second;
	
	/**
	 * Creates a new node pair.
	 * @param first The first node.
	 * @param second The second node.
	 */
	public NodePair(Node first, Node second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns the first node.
	 * @return The first node.
	 */
	public Node getFirstNode() {
		return first;
	}

	/**
	 * Returns the second node.
	 * @return The second node.
	 */
	public Node getSecondNode() {
		return second;
	}
	
	/**
	 * Creates a labeled instance for a classifier from this node pair.
	 * @param attributes The attributes of the instance.
	 * @param classLabel The class label for the new instance.
	 * @return The labeled instance for a classifier for this node pair.
	 */
	public Instance createInstance(FastVector attributes, String classLabel) {
		Instance instance = new Instance(attributes.size());

		instance.setValue((Attribute)attributes.elementAt(0), nicknamesSimilarity());
		instance.setValue((Attribute)attributes.elementAt(1), realnameSimilarity());
		instance.setValue((Attribute)attributes.elementAt(2), sameEmail());
		instance.setValue((Attribute)attributes.elementAt(3), sameWebsiteURL());
		instance.setValue((Attribute)attributes.elementAt(4), sameProfileLinks());
		instance.setValue((Attribute)attributes.elementAt(5), sameLocation());
		instance.setValue((Attribute)attributes.elementAt(6), classLabel);

		return instance;
	}
	
	/**
	 * Creates an unlabeled instance for a classifier from this node pair.
	 * @param attributes The attributes of the instance.
	 * @return The unlabeled instance for a classifier for this node pair.
	 */
	public Instance createInstance(FastVector attributes) {
		Instance instance = new Instance(attributes.size());

		instance.setValue((Attribute)attributes.elementAt(0), nicknamesSimilarity());
		instance.setValue((Attribute)attributes.elementAt(1), realnameSimilarity());
		instance.setValue((Attribute)attributes.elementAt(2), sameEmail());
		instance.setValue((Attribute)attributes.elementAt(3), sameWebsiteURL());
		instance.setValue((Attribute)attributes.elementAt(4), sameProfileLinks());
		instance.setValue((Attribute)attributes.elementAt(5), sameLocation());

		return instance;
	}

	private double nicknamesSimilarity() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private double realnameSimilarity() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int sameEmail() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int sameWebsiteURL() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int sameProfileLinks() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int sameLocation() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
