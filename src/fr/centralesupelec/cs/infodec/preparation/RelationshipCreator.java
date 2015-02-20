/**
 * 
 */
package fr.centralesupelec.cs.infodec.preparation;

import java.util.HashMap;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * Class responsible for the creation of the relationships.
 *
 */
public class RelationshipCreator {
	/**
	 * The source graph database.
	 */
	private GraphDatabaseService sourceDb;

	/**
	 * The target database.
	 */
	private BatchInserter targetDb;

	/**
	 * The selected set of nodes of the source database.
	 */
	private HashMap<Long, Long> nodeIndex;

	/**
	 * Creates a new {@code RelationshipCreator}.
	 * @param sourceDb The source database.
	 * @param targetDb The target database.
	 * @param nodeIndex The node index.
	 */
	public RelationshipCreator(GraphDatabaseService sourceDb, BatchInserter targetDb, HashMap<Long, Long> nodeIndex) {
		
		this.sourceDb = sourceDb;
		this.targetDb = targetDb;
		this.nodeIndex = nodeIndex;
	}
	
	/**
	 * Creates the relationships.
	 * @return The number of created relationships.
	 */
	public int createRelationships() {
		ProgressCounter counter = new ProgressCounter(1000, 10000);
		int numberOfRels = 0;
		Set<Long> nodes = nodeIndex.keySet();
		try( Transaction tx = sourceDb.beginTx() ) {
			for ( Long sourceNodeId : nodes ) {
				counter.increment();
				Node sourceNode = sourceDb.getNodeById(sourceNodeId);
				for ( Relationship r : sourceNode.getRelationships(Direction.OUTGOING) ) {
					Node targetNode = r.getOtherNode(sourceNode);
					long targetNodeId = targetNode.getId();
					if ( nodeIndex.containsKey(targetNodeId) ) {
						targetDb.createRelationship(nodeIndex.get(sourceNodeId), nodeIndex.get(targetNodeId), r.getType(), null);
						numberOfRels += 1;
					}
				}
			}
			tx.success();
		}
		return numberOfRels;
	}
}
