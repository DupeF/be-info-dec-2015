package fr.centralesupelec.cs.infodec;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;

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
	}	
}
