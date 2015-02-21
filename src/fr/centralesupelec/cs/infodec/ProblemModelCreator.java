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
		// Adding attributes expressing the similarity between two profiles
		problemModel.addAttribute("nicknames");
		problemModel.addAttribute("realname");
		problemModel.addAttribute("emails");
		problemModel.addAttribute("websites");
		problemModel.addAttribute("profilesLinks");
		problemModel.addAttribute("locations");
		
		// A single class "samePerson" is sufficientfor our problem
		problemModel.addClassLabel("samePerson", new String[] {"yes", "no"});

		return problemModel;
	}

}
