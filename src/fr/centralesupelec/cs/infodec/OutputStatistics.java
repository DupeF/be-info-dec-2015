/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import java.util.Iterator;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;

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
	 * Creates a new instance of this class.
	 * @param dbDirectory The directory of the graph database.
	 */
	public OutputStatistics(String dbDirectory) {
		graphDb = new GraphDatabaseConnector(dbDirectory);
		graph = graphDb.connect();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Usage: java OutputStatistics <db-directory>");
			System.exit(-1);
		}
		OutputStatistics dl = new OutputStatistics(args[0]);
		System.out.println("Number of nodes : " + dl.numberOfNodes());
	}

	/**
	 * Returns the number of nodes.
	 * 
	 * @return The number of nodes.
	 */
	public int numberOfNodes() {
		int noNodes = 0;
		for( Node n : graph.findNodesByLabelAndProperty(DynamicLabel.label("profile"), "", "")) {
			noNodes++;
		}
		return noNodes;
	}

	/**
	 * Returns the number of nodes by network
	 * 
	 * @return The number of nodes by network.
	 */
	public int numberOfNodesByNetwork() {
		int noNodes = 0;
		// TODO
		return noNodes;
	}

	/**
	 * Returns the number of links of type "friend".
	 * 
	 * @return The number of links of type "friend"
	 */
	public int numberOfFriendLinks() {
		int noLinks = 0;
		// TODO
		return noLinks;
	}

	/**
	 * Returns the number of links of type "me".
	 * 
	 * @return The number of links of type "me"
	 */
	public int numberOfMeLinks() {
		int noLinks = 0;
		// TODO
		return noLinks;
	}

	/**
	 * Returns the average number of friends.
	 * 
	 * @return The average number of friends.
	 */
	public int avgFriends() {
		int noLinks = 0;
		// TODO
		return noLinks;
	}

	/**
	 * Returns the maximum number of friends a node can have.
	 * 
	 * @return The maximum number of friends a node can have.
	 */
	public int maxFriends() {
		int noLinks = 0;
		// TODO
		return noLinks;
	}

	// TODO : autres stats possibles, il serait intéressant de les mettre dans
	// votre rapport. Pensez-y!
}
