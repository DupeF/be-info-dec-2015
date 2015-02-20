package fr.centralesupelec.cs.infodec;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Utility class to connect to a Neo4j database.
 */
public class GraphDatabaseConnector {
	/**
	 * Path to the database.
	 */
	private String dbPath;
	
	/**
	 * The graph database.
	 */
	private GraphDatabaseService graphDb;
	
	/**
	 * Creates a new {@code GraphDatabaseConnector}.
	 * 
	 * @param dbPath The path to the database to which the connection 
	 * is open
	 */
	public GraphDatabaseConnector(String dbPath) {
		this.dbPath = dbPath;
	}
	
	/**
	 * Connects to the database.
	 * @return The connection to the database.
	 */
	public GraphDatabaseService connect() {
		
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook(graphDb);
		return graphDb;
	}
	
	/**
	 * Closes the connection to the graph database.
	 */
	public void disconnect() {
		graphDb.shutdown();
	}
	
	/**
	 * Registers a shutdown hook for the Neo4j instance so that it
	 * shuts down nicely when the VM exits (even if you "Ctrl-C" the
	 * running application).
	 * 
	 * @param graphDb A graph database.
	 */
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // 
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}

}
