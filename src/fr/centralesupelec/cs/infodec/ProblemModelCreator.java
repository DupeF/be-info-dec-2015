/**
 * 
 */
package fr.centralesupelec.cs.infodec;

/**
 * Class responsible for the creation of a problem model.
 *
 */
public class ProblemModelCreator {
	
	// TODO create the problem model for your classification problem
	
	/**
	 * Creates the problem model for a given classification problem.
	 * @return The problem model for the given classification problem.
	 */
	public static ProblemModel createProblemModel() {
		ProblemModel problemModel = new ProblemModel();
		problemModel.addAttribute("attribute-1");
		problemModel.addAttribute("attribute-2");
		
		problemModel.addClassLabel("class", new String[] {"class-label-1", "class-label2"});

		return problemModel;
	}

}
