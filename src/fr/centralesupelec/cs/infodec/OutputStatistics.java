/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * Prints stats on the database.
 * 
 */
public class OutputStatistics {
	
	/**
	 * The graph database hosting the data.
	 */
	private GraphDatabaseConnector graphDb;
	
	/**
	 * The graph
	 */
	private GraphDatabaseService graph;
	
	/**
	 * A tool used to make operations on the graph
	 */
	private GlobalGraphOperations gOps;
	
	/**
	 * Creates a new instance of this class.
	 * @param dbDirectory The directory of the graph database.
	 */
	public OutputStatistics(String dbDirectory) {
		graphDb = new GraphDatabaseConnector(dbDirectory);
		graph = graphDb.connect();
		gOps = GlobalGraphOperations.at(graph);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Usage: java OutputStatistics <db-directory>");
			System.exit(-1);
		}
		OutputStatistics output = new OutputStatistics(args[0]);
		try (Transaction tx = output.graph.beginTx()) {
			System.out.println("Number of nodes : " + output.numberOfNodes());
			String[] networks = new String[] {"flickr", "livejournal", "twitter", "youtube"};
			for(String network : networks) {
				System.out.println("Number of nodes for " + network + " : " + output.numberOfNodesByNetwork(network));
			}
			System.out.println("Number of \"friend\" relations : " + output.numberOfFriendLinks());
			System.out.println("Number of \"me\" relations : " + output.numberOfMeLinks());
			System.out.println("Average number of friends : " + output.avgFriends());
			System.out.println("Maximal number of friends : " + output.maxFriends());
			tx.success();
		}
		
		output.graphDb.disconnect();
	}

	/**
	 * Returns the number of nodes.
	 * 
	 * @return The number of nodes.
	 */
	public int numberOfNodes() {
		int noNodes = 0;
		for(Node n : gOps.getAllNodes()) {
			noNodes++;
		}
		return noNodes;
	}

	/**
	 * Returns the number of nodes by network
	 * 
	 * @return The number of nodes by network.
	 */
	public int numberOfNodesByNetwork(String network) {
		int noNodes = 0;
		for(Node n : graph.findNodesByLabelAndProperty(DynamicLabel.label("profile"), "network", network)) {
			noNodes++;
		}
		return noNodes;
	}

	/**
	 * Returns the number of links of type "friend".
	 * 
	 * @return The number of links of type "friend"
	 */
	public int numberOfFriendLinks() {
		int noLinks = 0;
		for(Relationship r : gOps.getAllRelationships()) {
			if(r.isType(DynamicRelationshipType.withName("friend"))) {
				noLinks++;
			}
		}
		return noLinks;
	}

	/**
	 * Returns the number of links of type "me".
	 * 
	 * @return The number of links of type "me"
	 */
	public int numberOfMeLinks() {
		int noLinks = 0;
		for(Relationship r : gOps.getAllRelationships()) {
			if(r.isType(DynamicRelationshipType.withName("me"))) {
				noLinks++;
			}
		}		
		return noLinks;
	}

	/**
	 * Returns the average number of friends.
	 * 
	 * @return The average number of friends.
	 */
	public int avgFriends() {
		int total = 0;
		int noNodes = 0;
		for(Node n : gOps.getAllNodes())  {
			for(Relationship r : n.getRelationships(DynamicRelationshipType.withName("friend"))) {
				total++;
			}
			noNodes++;
		}
		return total/noNodes;
	}

	/**
	 * Returns the maximum number of friends a node can have.
	 * 
	 * @return The maximum number of friends a node can have.
	 */
	public int maxFriends() {
		int noLinks = 0;
		int current;
		for(Node n : gOps.getAllNodes())  {
			current = 0;
			for(Relationship r : n.getRelationships(DynamicRelationshipType.withName("friend"))) {
				current++;
			}
			noLinks = (current > noLinks) ? current : noLinks;
		}
		return noLinks;
	}

	// TODO : autres stats possibles, il serait intéressant de les mettre dans
	// votre rapport. Pensez-y!
}
