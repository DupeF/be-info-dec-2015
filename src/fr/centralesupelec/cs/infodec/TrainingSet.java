package fr.centralesupelec.cs.infodec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
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
	
	private static final int NUMBER_OF_PAIRS = 50;
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
		
		
		try (Transaction tx = graph.beginTx()) {
			
			// On récupère les noeuds déja marqué avec un lien me : base d'exemples positifs

			GlobalGraphOperations gOps = GlobalGraphOperations.at(graph);
			ArrayList<NodePair> pairs = new ArrayList<NodePair>();
			ArrayList<Node> involvedNodes = new ArrayList<Node>();
			Node n1, n2;
			for(Relationship r : gOps.getAllRelationships()) {
				if(r.isType(DynamicRelationshipType.withName("me"))) {
					n1 = r.getStartNode();
					n2 = r.getEndNode();
					if(n1.getProperty("uri").equals("http://twitter.com/lolrenee")){
						System.out.println(n2.getProperty("uri"));
					}
					if(n2.getProperty("uri").equals("http://twitter.com/lolrenee")){
						System.out.println(n1.getProperty("uri"));
					}
					// On ne prend pas une paire si un des éléments à déja été associé par une autre relation "me" (pour pouvoir mélanger les paires après)
					if(!involvedNodes.contains(n1) && !involvedNodes.contains(n2)) {
						pairs.add(new NodePair(n1,n2));
						involvedNodes.add(n1);
						involvedNodes.add(n2);
					}					
				}
			}
			
			//int pairsNumber = pairs.size();
			int currentPair = 0;
			for(NodePair pair : pairs) {
				currentPair += 1;
				if(currentPair > NUMBER_OF_PAIRS)
					break;
				//System.out.println(currentPair + "/" + pairsNumber + ") " + pair.getFirstNode().getProperty("uri") + " -- " + pair.getSecondNode().getProperty("uri") + " -- yes");
				addInstance(pair.createInstance(getAttributes(), "yes"));
				
			}
			
			// On mélange les paires : exemples négatifs
			Random rand = new Random();
			int low = 0;
			int high = pairs.size();
			currentPair = 0;
			for( NodePair pair : pairs ) {
				currentPair += 1;
				if(currentPair > NUMBER_OF_PAIRS)
					break;
				Node source = pair.getFirstNode(); 
				for ( int i = 0; i < 2; i += 1 )
				{
					int index = -1;
					do {
						index = rand.nextInt(high-low) + low;
					} while( index == currentPair - 1 );
					Node target = pairs.get(index).getSecondNode();
					//System.out.println(currentPair + "/" + pairsNumber + ") " + source.getProperty("uri") + " -- " + target.getProperty("uri") + " -- non");
					addInstance((new NodePair(source, target)).createInstance(getAttributes(), "no"));
				}
			}
			tx.success();
		}
	}

}
