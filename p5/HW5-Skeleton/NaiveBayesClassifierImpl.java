import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

	//THESE VARIABLES ARE OPTIONAL TO USE, but HashMaps will make your life much, much easier on this assignment.

	//dictionaries of form word:frequency that store the number of times word w has been seen in documents of type label
	//for example, comedyCounts["mirth"] should store the total number of "mirth" tokens that appear in comedy documents
	private HashMap<String, Integer> tragedyCounts = new HashMap<String, Integer>();
	private HashMap<String, Integer> comedyCounts = new HashMap<String, Integer>();
	private HashMap<String, Integer> historyCounts = new HashMap<String, Integer>();

	//prior probabilities, ie. P(T), P(C), and P(H)
	//use the training set for the numerator and denominator
	private double tragedyPrior;
	private double comedyPrior;
	private double historyPrior;

	//total number of word TOKENS for each type of document in the training set, ie. the sum of the length of all documents with a given label
	private int tTokenSum;
	private int cTokenSum;
	private int hTokenSum;

	//full vocabulary, update in training, cardinality is necessary for smoothing
	private HashSet<String> vocabulary = new HashSet<String>();

	/**
	 * Trains the classifier with the provided training data
   Should iterate through the training instances, and, for each word in the documents, update the variables above appropriately.
   The dictionary of frequencies and prior probabilites can then be used at classification time.
	 */
	@Override
	public void train(Instance[] trainingData) {
		// TODO : Implement
		for(int i = 0; i< trainingData.length; i++)
		{
			if (trainingData[i].label == Label.COMEDY){
				genreCounts(trainingData[i], comedyCounts);
			}
			else if (trainingData[i].label == Label.HISTORY){
				genreCounts(trainingData[i], historyCounts);
			}
			else if (trainingData[i].label == Label.TRAGEDY){
				genreCounts(trainingData[i], tragedyCounts);
			}
		}
	}

	private void genreCounts(Instance tempInst, HashMap<String, Integer> gCounts){
		for(String token: tempInst.words)
		{
			if(gCounts.containsKey(token))
			{
				int v = gCounts.get(token);
				v++;
				//gCounts.+=1;
			}else{
				gCounts.put(token,1);
			}
		}
	}

	/*
	 * Prints out the number of documents for each label
	 * A sanity check method
	 */
	public void documents_per_label_count(){
		// TODO : Implement
	}

	/*
	 * Prints out the number of words for each label
	Another sanity check method
	 */
	public void words_per_label_count(){
		// TODO : Implement
		//System.out.println(x);
	}

	/**
	 * Returns the prior probability of the label parameter, i.e. P(COMEDY) or P(TRAGEDY)
	 */
	@Override
	public double p_l(Label label) {
		// TODO : Implement
		if(label==Label.COMEDY)
			return comedyPrior;
		else if(label==Label.TRAGEDY)
			return tragedyPrior;
		else if(label==Label.HISTORY)
			return historyPrior;
		return 0;
	}

	/**
	 * Returns the smoothed conditional probability of the word given the label, i.e. P(word|COMEDY) or
	 * P(word|HISTORY)
	 */
	@Override
	public double p_w_given_l(String word, Label label) {
		// TODO : Implement
		double delta = 0.00001;
		double numerator = delta;
		double sum_v = 0.00;

		if(label==Label.COMEDY)
		{
			numerator += comedyCounts.get(word);
			sum_v = calc_sum_v(comedyCounts);
		}
		else if(label==Label.TRAGEDY)
		{
			numerator += tragedyCounts.get(word);
			sum_v = calc_sum_v(tragedyCounts);

		}			
		else if(label==Label.HISTORY)
		{
			numerator += historyCounts.get(word);
			sum_v = calc_sum_v(historyCounts);
		}

		double denominator = vocabulary.size()*delta + sum_v;

		return numerator/denominator;
	}

	private double calc_sum_v(HashMap<String, Integer> map){
		double total = 0.00;
		for(Integer val: map.values()){
			total += val;
		}
		return total;
	}

	/**
	 * Classifies a document as either a Comedy, History, or Tragedy.
   Break ties in favor of labels with higher prior probabilities.
	 */
	@Override
	public Label classify(Instance ins) {

		// TODO : Implement
		double prob_h = 1;
		double prob_t = 1;
		double prob_c = 1;

		for(String w: ins.words)
		{
			prob_h *= Math.log(p_w_given_l(w, Label.COMEDY));
			prob_t *= Math.log(p_w_given_l(w, Label.TRAGEDY));
			prob_c *= Math.log(p_w_given_l(w, Label.HISTORY));
		}

		prob_h += Math.log(historyPrior);
		prob_t += Math.log(tragedyPrior);
		prob_c += Math.log(comedyPrior);

		double maxprob = Math.max(Math.max(prob_h, prob_c), prob_t);
		if(maxprob == prob_h)
			return Label.HISTORY;
		else if(maxprob == prob_c)
			return Label.COMEDY;
		else if(maxprob == prob_t)
			return Label.TRAGEDY;
		//Initialize sum probabilities for each label
		//For each word w in document ins
		//compute the log (base e or default java log) probability of w|label for all labels (COMEDY, TRAGEDY, HISTORY)
		//add to appropriate sum
		//Return the Label of the maximal sum probability

		return null; 
	}


}
