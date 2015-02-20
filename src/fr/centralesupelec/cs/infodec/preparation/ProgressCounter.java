/**
 * 
 */
package fr.centralesupelec.cs.infodec.preparation;

/**
 * Used to track the progress of a procedure.
 *
 */
public class ProgressCounter {

	/**
	 * Small step. A "." is sent to the standard output every {@code smallStep}.
	 */
	private int smallStep;
	
	/**
	 * Big step. A new line is started every big step.
	 */
	private int bigStep;
	
	/**
	 * A counter
	 */
	private int count;
	
	/**
	 * Creates a new progress counter.
	 * @param smallStep The small step.
	 * @param bigStep The big step.
	 */
	public ProgressCounter(int smallStep, int bigStep) {
		this.smallStep = smallStep;
		this.bigStep = bigStep;
		this.count = 0;
	}

	/**
	 * Returns the current value of the counter.
	 * @return The current value of the counter.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Increments the counter and prints the progress to the standard output,for each small and 
	 * big step. 
	 *  
	 */
	public void increment() {
		count++;
		if (count % bigStep == 0) {
			System.out.println(". "+ count);
		} else if (count % smallStep == 0) {
			System.out.print(".");
		}
	}

}
