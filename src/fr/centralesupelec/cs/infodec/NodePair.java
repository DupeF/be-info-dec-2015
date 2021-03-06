package fr.centralesupelec.cs.infodec;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.neo4j.graphdb.Node;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;

/**
 * A pair of nodes
 *
 */
public class NodePair {
		
	/**
	 * The first node.
	 */
	private Node first;

	/**
	 * The second node.
	 */
	private Node second;
	
	/**
	 * Creates a new node pair.
	 * @param first The first node.
	 * @param second The second node.
	 */
	public NodePair(Node first, Node second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns the first node.
	 * @return The first node.
	 */
	public Node getFirstNode() {
		return first;
	}

	/**
	 * Returns the second node.
	 * @return The second node.
	 */
	public Node getSecondNode() {
		return second;
	}
	
	/**
	 * Creates a labeled instance for a classifier from this node pair.
	 * @param attributes The attributes of the instance.
	 * @param classLabel The class label for the new instance.
	 * @return The labeled instance for a classifier for this node pair.
	 */
	public Instance createInstance(FastVector attributes, String classLabel) {
		Instance instance = new Instance(attributes.size());

		if (bothHaveAttributeTab("nicknames"))
			instance.setValue((Attribute)attributes.elementAt(0), nicknamesSimilarity());
		if (bothHaveAttribute("realname"))
			instance.setValue((Attribute)attributes.elementAt(1), realnameSimilarity());
		if (bothHaveAttributeTab("emails"))
			instance.setValue((Attribute)attributes.elementAt(2), sameEmail());
		if (bothHaveAttributeTab("websites"))
			instance.setValue((Attribute)attributes.elementAt(3), sameWebsiteURL());
		if (bothHaveAttributeTab("profiles"))
			instance.setValue((Attribute)attributes.elementAt(4), sameProfileLinks());
		if (bothHaveAttributeTab("locations"))
			instance.setValue((Attribute)attributes.elementAt(5), sameLocation());
		instance.setValue((Attribute)attributes.elementAt(6), classLabel);
		return instance;
	}
	
	/**
	 * Creates an unlabeled instance for a classifier from this node pair.
	 * @param attributes The attributes of the instance.
	 * @return The unlabeled instance for a classifier for this node pair.
	 */
	public Instance createInstance(FastVector attributes) {
		Instance instance = new Instance(attributes.size());

		if (bothHaveAttributeTab("nicknames"))
			instance.setValue((Attribute)attributes.elementAt(0), nicknamesSimilarity());
		if (bothHaveAttribute("realname"))
			instance.setValue((Attribute)attributes.elementAt(1), realnameSimilarity());
		if (bothHaveAttributeTab("emails"))
			instance.setValue((Attribute)attributes.elementAt(2), sameEmail());
		if (bothHaveAttributeTab("websites"))
			instance.setValue((Attribute)attributes.elementAt(3), sameWebsiteURL());
		if (bothHaveAttributeTab("profiles"))
			instance.setValue((Attribute)attributes.elementAt(4), sameProfileLinks());
		if (bothHaveAttributeTab("locations"))
			instance.setValue((Attribute)attributes.elementAt(5), sameLocation());


		return instance;
	}

	private boolean bothHaveAttribute(String attribute) {
		String attribute1 = ((String) first.getProperty(attribute, ""));
		String attribute2 = ((String) second.getProperty(attribute, ""));
		return (attribute1.length() > 0  && attribute2.length() > 0);
	}
	
	private boolean bothHaveAttributeTab(String attribute) {
		String[] attributes1 = ((String[]) first.getProperty(attribute, new String[]{}));
		String[] attributes2 = ((String[]) second.getProperty(attribute, new String[]{}));
		return (attributes1.length > 0  && attributes2.length > 0);
	}
	
	private double nicknamesSimilarity() {
		String[] nicknames1 = ((String[]) first.getProperty("nicknames", new String[]{}));
		String[] nicknames2 = ((String[]) second.getProperty("nicknames", new String[]{}));
		double sim = 0;
		for(String nickname1 : nicknames1) {
			for(String nickname2 : nicknames2) {
				if(  nickname1.length() > 3 && nickname2.length() > 3)
					sim = Math.max(sim, normalizedLevenshteinDistance(nickname1, nickname2));
				else if( nickname1.equalsIgnoreCase(nickname2))
					sim = 1;
			}
		}
		return sim;
	}
	
	
	private double realnameSimilarity() {
		// Mesure Jaccard
		String[] realname1 = ((String) first.getProperty("realname", "")).split(" ");
		String[] realname2 = ((String) second.getProperty("realname", "")).split(" ");
		Set<String> differentWords = new HashSet<String>();
		for(String word  : realname1) {
			differentWords.add(word);
		}
		for(String word : realname2) {
			if(!differentWords.contains(word))
				differentWords.add(word);
		}
		double totalNumber = realname1.length + realname2.length;
		double motsCommuns = totalNumber - differentWords.size(); 
		return (totalNumber == 0) ? 0 : motsCommuns/totalNumber;
	}
	
	private int sameEmail() {
		return booleanSimilarity("emails");
	}
	
	private int sameWebsiteURL() {
		return booleanSimilarity("websites");
	}
	
	private int sameProfileLinks() {
		String[] profiles1 = (String[]) first.getProperty("profiles", new String[]{});
		String[] profiles2 = (String[]) second.getProperty("profiles", new String[]{});
		int l1 = profiles1.length;
		int l2 = profiles2.length;
		String profile, profile2;
		for(int i = 0; i<=l1; i++) {
			profile = (i<l1) ? profiles1[i] : (String) first.getProperty("uri");
			for(int j = 0; j<=l2; j++) {
				profile2 = (j<l2) ? profiles2[j] : (String) second.getProperty("uri");
				if(profile.equals(profile2))
					return 1;
			}
		}
		return 0;
	}

	private double sameLocation() {
		String[] locations1 = (String[]) first.getProperty("locations", new String[]{});
		String[] locations2 = (String[]) second.getProperty("locations", new String[]{});
		double sim = 0;
		for(String location : locations1) {
			location.replaceAll(Pattern.quote("?"), "");
			for(String location2 : locations2) {
				location2.replaceAll(Pattern.quote("?"), "");
				sim = Math.max(normalizedLevenshteinDistance(location, location2), sim);
			}
		}
		return sim;
	}
	
	private int booleanSimilarity(String key) {
		String[] attributes1 = (String[]) first.getProperty(key, new String[]{});
		String[] attributes2 = (String[]) second.getProperty(key, new String[]{});
		for(String  attribute : attributes1) {
			for(String attribute2 : attributes2) {
				if(attribute.equals(attribute2))
					return 1;
			}
		}
		return 0;
	}
	
    public static double normalizedLevenshteinDistance(CharSequence s, CharSequence t)
    {
        // degenerate cases
        if (s == null || "".equals(s))
        {
            return t == null || "".equals(t) ? 0 : t.length();
        }
        else if (t == null || "".equals(t))
        {
            return s.length();
        }

        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++)
        {
            v0[i] = i;
        }

        int sLen = s.length();
        int tLen = t.length();
        for (int i = 0; i < sLen; i++)
        {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < tLen; j++)
            {
                int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
                v1[j + 1] = minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            System.arraycopy(v1, 0, v0, 0, v0.length);
        }

        double levenshteinDistance = v1[t.length()];
        double tailleMax = Math.max(sLen, tLen);
        double result = (tailleMax > 0) ? 1-(levenshteinDistance/tailleMax) : 0;
        return result;
    }
    
    private static int minimum(int a, int b,  int c) {
    	return Math.min(c, Math.min(a, b));
    }
    
}
