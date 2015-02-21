package fr.centralesupelec.cs.infodec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

import weka.core.FastVector;
import weka.core.converters.ArffSaver;


/**
 * A training set
 *
 */
public class TrainingSet extends DataSet {
	/**
	 * Creates a new empty training set
	 * 
	 * @param name The name of this training set.
	 * @param attributes The list of the attributes for this training set.
	 */
	public TrainingSet(String name, FastVector attributes) {
		super(name, attributes);
	}

	/**
	 * Saves this dataset to Arff file
	 * @throws IOException when something goes wrong while writing the training set to file.
	 */
	public void saveToArff() throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(getInstances());
		saver.setFile(new File("./training.arff"));
		saver.writeBatch();
	}

	/**
	 * Populates the training set with instances obtained from the graph database.
	 * @param graph The graph database.
	 */
	public void createTrainingSet(GraphDatabaseService graph) {
		//TODO bon courage!
		// On récupère les noeuds déja marqué avec un lien me : bas d'exemples positifs
		try (Transaction tx = graph.beginTx()) {
			GlobalGraphOperations gOps = GlobalGraphOperations.at(graph);
			ArrayList<NodePair> pairs = new ArrayList<NodePair>();
			for(Relationship r : gOps.getAllRelationships()) {
				if(r.isType(DynamicRelationshipType.withName("me"))) {
					pairs.add(new NodePair(r.getStartNode(), r.getEndNode()));
					if(r.getStartNode() == r.getEndNode()) {
						System.out.println("Problem");
					}
				}
			}
			
			int pairsNumber = pairs.size();
			int currentPair = 0;
			for(NodePair pair : pairs) {
				currentPair += 1;
				System.out.println(currentPair + "/" + pairsNumber + ") " + pair.getFirstNode().getProperty("uri") + " -- " + pair.getSecondNode().getProperty("uri") + " -- yes");
				addInstance(pair.createInstance(getAttributes(), "yes"));
			}
			tx.success();
		}
	}	
}
