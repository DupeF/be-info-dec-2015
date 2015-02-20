package fr.centralesupelec.cs.infodec.preparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * Class responsible for the creation of the nodes in the target database.
 * 
 */
public class NodeCreator {
	
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
	 * Creates a new {@code NodeCreator}.
	 * @param sourceDb The source database.
	 * @param targetDb The target database.
	 * @param uriFile The file with the URIs of the nodes to be created.
	 * @param nodeIndex The list of the nodes retrieved from the source database.
	 */
	public NodeCreator(GraphDatabaseService sourceDb, BatchInserter targetDb, 
			File uriFile, HashMap<Long, Long> nodeIndex) {
		
		this.sourceDb = sourceDb;
		this.targetDb = targetDb;
		this.uriFile = uriFile;
		this.nodeIndex = nodeIndex;
	}
	
	/**
	 * Creates the nodes.
	 * 
	 * @return The number of nodes created.
	 * @throws IOException when something goes wrong while reading the URI file.
	 * 
	 */
	public int createNodes() throws IOException {
		BufferedReader bd = new BufferedReader(new FileReader(uriFile));
		ProgressCounter progress = new ProgressCounter(1000, 10000);
		int numberOfNodes = 0;
		try (Transaction tx = sourceDb.beginTx()) {
			String uri;
			while( (uri = bd.readLine()) != null ) {
				progress.increment();
				Iterator<Node> it = sourceDb.findNodesByLabelAndProperty(SocialNetworkLabel.profile, "uri", uri).iterator();
				Node node = it.hasNext() ? it.next() : null;
				if ( node == null )
					continue;
				
				long oldId = node.getId();
				String[] nicknames = (String[])node.getProperty("nicknames", null);
				String realname = (String)node.getProperty("realname", null);
				String[] profiles = (String[])node.getProperty("profiles", null);
				String network = (String)node.getProperty("network", null);
				String[] websites = (String[])node.getProperty("websites", null);
				String[] locations = (String[])node.getProperty("locations", null);
				String[] emails = (String[])node.getProperty("emails", null);
				Map<String, Object> properties = new HashMap<String, Object>();
				if ( nicknames != null )
					properties.put("nicknames", nicknames);
				if ( realname != null )
					properties.put("realname", realname);
				if ( profiles != null )
					properties.put("profiles", profiles);
				if ( network != null )
					properties.put("network", network);
				if ( websites != null )
					properties.put("websites", websites);
				if ( locations != null )
					properties.put("locations", locations);
				if ( emails != null )
					properties.put("emails", emails);
				properties.put("uri", uri);
				properties.put("source-id", oldId);
				long newId = targetDb.createNode(properties, SocialNetworkLabel.profile);
				numberOfNodes += 1;
				nodeIndex.put(oldId, newId);
			}
			tx.success();
		}
		bd.close();
		return numberOfNodes;
	}
	
}













