/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Program to find "me" links in a social internetwork.
 *
 */
public class FindMe {

	/**
	 * The graph database hosting the data.
	 */
	private GraphDatabaseConnector graphDb;

	/**
	 * Creates a new instance of this class.
	 * @param dbDirectory The directory of the graph database.
	 */
	public FindMe(String dbDirectory) {
		this.graphDb = new GraphDatabaseConnector(dbDirectory);
	}

	/**
	 * The main method.
	 * @param args Command-line arguments
	 * @throws Exception when something goes wrong...
	 */
	public static void main(String[] args) throws Exception {

		if ( args.length != 1 ) {
			System.err.println("Usage: java FindMe <db-directory>");
			System.exit(-1);
		}
		FindMe findMe = new FindMe(args[0]);
		GraphDatabaseService graph = findMe.graphDb.connect();

		/*
		 *  Load the classifier trained by {@code ModelCreator}, si besoin....
		 */
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream("./social-network-svm"));
		Classifier cls = (Classifier) ois.readObject();
		ois.close();

		// Create the problem model.
		ProblemModel problemModel = ProblemModelCreator.createProblemModel();

		//TODO coder l'algorithme pour découvrir des liens "me". Bon courage! ....

		/*
		 * Besoin d'une petite aide? Pour faire de la prediction en utilisant le classificateur cls, 
		 * utiliser cls.distributionForInstance() --- voir l'exemple de Wikipedia (fichier DiscoverCrossLinks.java)
		 * et la doc de Weka
		 */


		BufferedReader br = 
				new BufferedReader(new InputStreamReader(System.in));
		do {
			System.out.print("Url du profil (type ENTER to exit): ");
			String profileUrl = br.readLine().trim();
			if ( profileUrl.equals("") )
				break;

			System.out.println("Finding me-links for " + profileUrl);
			Node node = findMe.findSourceNode(graph, profileUrl);
			if ( node == null )
				throw new RuntimeException("Node not found!");
			ArrayList<Candidate> candidates = findMe.findCandidates(graph, node);
			ArrayList<Candidate> scoredCandidates = new ArrayList<Candidate>();
			for( Candidate candidate : candidates ) {
				try (Transaction tx = graph.beginTx()) {
					//System.out.println(candidate.getNode().getProperty("uri"));
					tx.success();
				}
				findMe.classifyInstance(graph, cls, node, candidate.getNode(), problemModel, scoredCandidates);

			}

			Collections.sort(scoredCandidates, new ScoredCandidateComparator());
			try (Transaction tx = graph.beginTx()) {
				for ( int i= 0; i < scoredCandidates.size() && i < 5; i += 1 )
					System.out.println(scoredCandidates.get(i).getNode().getProperty("title") + " -- " + scoredCandidates.get(i).getScore());
				tx.success();
			}
		}while(true);

		br.close();
		findMe.graphDb.disconnect();

	}

	//TODO : ceci est un copier coller
	private void classifyInstance(GraphDatabaseService graph, Classifier cls, Node node, Node candidate, 
			ProblemModel problemModel, ArrayList<Candidate> scoredCandidates) throws Exception {
		Instances data = new Instances("Test", problemModel.getAttributes(), 0);
		data.setClassIndex(problemModel.getClassIndex());
		try ( Transaction tx = graph.beginTx() ) {
			Instance instance = (new NodePair(node, candidate).createInstance(problemModel.getAttributes()));
			data.add(instance);

			double[] res = cls.distributionForInstance(data.firstInstance());
			scoredCandidates.add(new Candidate(candidate, res[0]));
			tx.success();
		}

	}

	
	/**
	 * Returns the node with the specified profile url.
	 * @param graph The graph hosting the data. 
	 * @param uril The url of the profile we're searching
	 * @return The node with the specified {@code url} ({@code null} if no node is found).
	 */
	private Node findSourceNode(GraphDatabaseService graph, String url) {
		try (Transaction tx = graph.beginTx()) {
			for (Node n : graph.findNodesByLabelAndProperty(DynamicLabel.label("profile"), "uri", url)) {
				tx.success();
				return n;
			}

			tx.success();
		}

		return null;
	}

	//TODO : ceci est un copier coller : Il faut tout changer
	/**
	 * Finds the candidates.
	 * @param graph The graph
	 * @param node The source node
	 * @return The candidates for the given source node.
	 */
	private ArrayList<Candidate> findCandidates(GraphDatabaseService graph, Node node) {

		HashMap<Long, Integer> candidatesHm = new HashMap<Long, Integer>();
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		try (Transaction tx = graph.beginTx()) {

			for ( Relationship r : node.getRelationships(Direction.OUTGOING, DynamicRelationshipType.withName("Link")) ) {
				Node other = r.getOtherNode(node);
				if ( other.hasLabel(DynamicLabel.label("Category")) )
					continue;
				Iterator<Relationship> it = other.getRelationships(Direction.BOTH, DynamicRelationshipType.withName("Crosslink")).iterator();
				Node otherLang = it.hasNext() ? it.next().getOtherNode(other) : null;
				if ( otherLang == null )
					continue;

				for ( Relationship r1: otherLang.getRelationships(Direction.INCOMING, DynamicRelationshipType.withName("Link")) ) {
					Node target = r1.getOtherNode(otherLang);
					if ( candidatesHm.containsKey(target.getId()) ) 
						candidates.get(candidatesHm.get(target.getId())).incrementSupport();
					else {
						candidates.add(new Candidate(target));
						candidatesHm.put(target.getId(), candidates.size()-1);
					}

				}
			}
			tx.success();
		}

		Collections.sort(candidates, new CandidateComparator());
		ArrayList<Candidate>  candidates1000 = new ArrayList<Candidate>();
		for ( int i = 0; i < candidates.size() && i < 1000; i += 1 )
			candidates1000.add(candidates.get(i));
		return candidates1000;
	}

}

//TODO : ceci est un copier coller
/**
 * 
 * Used to compare two candidates and sort them by decreasing support.
 *
 */
class CandidateComparator implements Comparator<Candidate> {

	public int compare(Candidate o1, Candidate o2) {
		if ( o1.getSupport() == o2.getSupport() )
			return 0;
		if (o1.getSupport() > o2.getSupport())
			return -1;
		return 1;
	}
}

//TODO : ceci est un copier coller
/**
 * 
 * Used to compare two candidates and sort them by decreasing score.
 *
 */
class ScoredCandidateComparator implements Comparator<Candidate> {

	public int compare(Candidate o1, Candidate o2) {
		if ( o1.getScore() == o2.getScore() )
			return 0;
		if (o1.getScore() > o2.getScore())
			return -1;
		return 1;
	}
}

