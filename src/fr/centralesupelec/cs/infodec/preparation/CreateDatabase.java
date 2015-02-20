/**
 * 
 */
package fr.centralesupelec.cs.infodec.preparation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

/**
 * Program to create the database given to the students.
 * The database is a subset of a much larget dataset.
 *
 */
public class CreateDatabase {
	
	/**
	 * The source graph database.
	 */
	private GraphDatabaseService sourceDb;
	
	/**
	 * The target database.
	 */
	private BatchInserter targetDb;
	
	/**
	 * The file with the URIs of the nodes (used to retrieve the data from the source database)
	 */
	private File uriFile;
	
	/**
	 * The selected set of nodes of the source database.
	 */
	private HashMap<Long, Long> nodeIndex;
	
	/**
	 * Initializes this object attributes, namely:
	 * @param sourceDbPath The path to the source database.
	 * @param targetDbPath The path to the target database.
	 * @param uriFilePath The path to the file with the URIs of the nodes to extract from the source database.
	 */
	CreateDatabase(String sourceDbPath, String targetDbPath, String uriFilePath) {
		this.sourceDb = new GraphDatabaseFactory().newEmbeddedDatabase(sourceDbPath);
		registerShutdownHook(this.sourceDb);
		
		this.targetDb = BatchInserters.inserter(targetDbPath);
		targetDb.createDeferredSchemaIndex(SocialNetworkLabel.profile).on("uri").create();
		targetDb.createDeferredSchemaIndex(SocialNetworkLabel.profile).on("network").create();
		
		this.uriFile = new File(uriFilePath);
		
		this.nodeIndex = new HashMap<Long, Long>();
	}
	
	/**
	 * Entry point of the program
	 * @param args Command-line arguments, namely: source db path, target db path, uri file path
	 * @throws IOException when something goes wrong while reading the URI file.
	 */
	public static void main(String[] args) throws IOException {
		if ( args.length != 3 ) {
			System.err.println("Usage: java CreateDatabase sourceDbPath targetDbPath uriFilePath");
			System.exit(-1);
		}
		String sourceDbPath = args[0];
		String targetDbPath = args[1];
		String uriFilePath = args[2];
		CreateDatabase dbCreator = new CreateDatabase(sourceDbPath, targetDbPath, uriFilePath);
		System.out.println("Creating the nodes....");
		System.out.println("\nNumber of nodes created: " + dbCreator.createNodes());
		System.out.println("\nCreating the relationships....");
		System.out.println("\nNumber of relationships created: " + dbCreator.createRelationships());
		System.out.println("Shutting down....");
		dbCreator.shutdown();
		System.out.println("Finished!");
	}
	
	/**
	 * Creates the nodes.
	 * 
	 * @return The number of nodes created.
	 * @throws IOException when something goes wrong while reading the URI file.
	 */
	private int createNodes() throws IOException {
		NodeCreator nodeCreator = new NodeCreator(this.sourceDb, this.targetDb, this.uriFile, this.nodeIndex);
		return nodeCreator.createNodes();
	}
	
	/**
	 * Creates the relationships.
	 * 
	 * @return The number of relationships created.
	 */
	private int createRelationships() {
		RelationshipCreator rCreator = new RelationshipCreator(this.sourceDb, this.targetDb, this.nodeIndex);
		return rCreator.createRelationships();
	}
	
	/**
	 * Shuts down the connection to the source and target databases.
	 */
	private void shutdown() {
		this.sourceDb.shutdown();
		this.targetDb.shutdown();
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
