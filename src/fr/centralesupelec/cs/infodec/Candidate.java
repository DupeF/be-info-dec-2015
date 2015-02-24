/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import org.neo4j.graphdb.Node;

/**
 * A candidate
 *
 */
public class Candidate {

	/**
	 * The candidate node.
	 */
	private Node node;
	
	/**
	 * The support of the candidate.
	 */
	private int support;
	
	/**
	 * The score of the candidate, as assigned by the classifier.
	 */
	private double score;
	
	/**
	 * Creates a new {@code Candidate}.
	 * @param node The candidate node
	 */
	public Candidate(Node node) {
		this.node = node;
		this.support = 0;
		this.score = 0.;
	}
	
	/**
	 * Creates a new {@code Candidate} with the given score.
	 * @param node The candidate node.
	 * @param score The score of this candidate as assigned by the classifier.
	 */
	public Candidate(Node node, double score) {
		this.node = node;
		this.support = 0;
		this.score = score;
	}
	
	/**
	 * Returns the candidate node.
	 * @return The candidate node.
	 */
	public Node getNode() {
		return this.node;
	}
	
	/**
	 * Returns the support of this candidate.
	 * @return The support of this candidate.
	 */
	public int getSupport() {
		return this.support;
	}
	
	/**
	 * Returns the score of this candidate.
	 * @return The score of this candidate.
	 */
	public double getScore() {
		return this.score;
	}
	
	/**
	 * Increments the support of this candidate.
	 */
	public void incrementSupport() {
		this.support += 1;
	}
}
