/**
 * 
 */
package fr.centralesupelec.cs.infodec;

import java.io.IOException;
import java.util.Random;

import org.neo4j.graphdb.GraphDatabaseService;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.SerializationHelper;

/**
 * Class to create a model (classifier)
 *
 */
public class ModelCreator {

	/**
	 * The graph database hosting the data.
	 */
	private GraphDatabaseConnector graphDb;

	/**
	 * Creates a new instance of this class.
	 * @param dbDirectory The directory of the graph database.
	 */
	public ModelCreator(String dbDirectory) {
		graphDb = new GraphDatabaseConnector(dbDirectory);
	}

	/**
	 * The main method.
	 * @param args Command-line arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		if ( args.length != 1 ) {
			System.err.println("Usage: java ModelBuilder <db-directory>");
			System.exit(-1);
		}
		ModelCreator dl = new ModelCreator(args[0]);
		GraphDatabaseService graph = dl.graphDb.connect();

		System.out.println("Creating the problem model...");
		ProblemModel problemModel = ProblemModelCreator.createProblemModel();
		System.out.println("Done!\n");

		System.out.println("Creating the training set...");
		TrainingSet trainingSet = new TrainingSet("social-network-training", problemModel.getAttributes());
		trainingSet.setClassIndex(problemModel.getClassIndex());
		trainingSet.createTrainingSet(graph);
		trainingSet.saveToArff();
		System.out.println("Done!\n");
		
		SMO svm = dl.getSVM();
		System.out.println("Training and evaluating the classifier...");

		try {

			// Train the classifier (build the model).
			svm.buildClassifier(trainingSet.getInstances());
			Evaluation eval = new Evaluation(trainingSet.getInstances());
			eval.crossValidateModel(svm, trainingSet.getInstances(), 10, new Random(1));

			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(eval.toMatrixString());


		}
		catch( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}

		System.out.println("Saving the classifier...");
		try {
			SerializationHelper.write("./social-network-svm", svm);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Done!\n");

		dl.graphDb.disconnect();

	}

	/**
	 * Gets the SVM.
	 * @return The SVM
	 */
	private SMO getSVM() {
		SMO svm = new SMO();
		svm.setBuildLogisticModels(true);
		return svm;
	}

}
