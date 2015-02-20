/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.neo4j.graphdb.GraphDatabaseService;

import weka.classifiers.Classifier;

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
		
		
		findMe.graphDb.disconnect();

	}
}
