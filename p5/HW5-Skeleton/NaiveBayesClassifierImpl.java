import java.awt.List;
import java.util.ArrayList;
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

	private ArrayList<Instance> instances = new ArrayList<Instance>();

	private int words_c = 0;
	private int words_t = 0;
	private int words_h = 0;
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
			instances.add(trainingData[i]);

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

		comedyPrior = p_l(Label.COMEDY);
		tragedyPrior = p_l(Label.TRAGEDY);
		historyPrior = p_l(Label.HISTORY);
		
	}

	private void genreCounts(Instance tempInst, HashMap<String, Integer> gCounts){
		for(String token: tempInst.words)
		{
			vocabulary.add(token);
			if(gCounts.containsKey(token))
			{
				int v = gCounts.get(token);
				gCounts.put(token,++v);
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
		int t_count = 0;		
		int c_count = 0;
		int h_count = 0;
		for(Instance instance: instances){
			if(instance.label==Label.COMEDY)
				c_count++;
			else if(instance.label==Label.HISTORY)
				h_count++;
			else if(instance.label==Label.TRAGEDY)
				t_count++;
		}
		System.out.println(c_count + "  "  + t_count + "  " + h_count);
	}

	/*
	 * Prints out the number of words for each label
	Another sanity check method
	 */
	public void words_per_label_count(){
		// TODO : Implement

		for(String word: tragedyCounts.keySet())
		{
			words_t += tragedyCounts.get(word);
		}
		for(String word: comedyCounts.keySet())
		{
			words_c += comedyCounts.get(word);
		}
		for(String word: historyCounts.keySet())
		{
			words_h += historyCounts.get(word);
		}
		System.out.println("" + words_c);
		System.out.println("" + words_t);
		System.out.println("" + words_h);

	}

	/**
	 * Returns the prior probability of the label parameter, i.e. P(COMEDY) or P(TRAGEDY)
	 */
	@Override
	public double p_l(Label label) {
		// TODO : Implement
		if(label==Label.COMEDY){
			return words_c/(words_c + words_t + words_h);
		}
		else if(label==Label.TRAGEDY){
			return words_t/(words_c + words_t + words_h);
		}
		else if(label==Label.HISTORY){
			return words_h/(words_c + words_t + words_h);
		}
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
		double denominator = vocabulary.size()*delta;

		if(label==Label.COMEDY)
		{
			if(comedyCounts.containsKey(word)){
				numerator += comedyCounts.get(word);
			}
			denominator += words_c;
		}
		else if(label==Label.TRAGEDY)
		{
			if(tragedyCounts.containsKey(word)){
				numerator += tragedyCounts.get(word);
			}
			denominator += words_t;
		}			
		else if(label==Label.HISTORY)
		{
			if(historyCounts.containsKey(word)){
				numerator += historyCounts.get(word);
			}
			denominator += words_h;
		}

		return numerator/denominator;
	}

	/**
	 * Classifies a document as either a Comedy, History, or Tragedy.
   Break ties in favor of labels with higher prior probabilities.
	 */
	@Override
	public Label classify(Instance ins) {

		// TODO : Implement
		//Initialize sum probabilities for each label
		//For each word w in document ins
		//compute the log (base e or default java log) probability of w|label for all labels (COMEDY, TRAGEDY, HISTORY)
		//add to appropriate sum
		//Return the Label of the maximal sum probability
		
		double prob_h = Math.log(historyPrior);
		double prob_t = Math.log(tragedyPrior);
		double prob_c = Math.log(comedyPrior);

		for(String w: ins.words)
		{
			prob_h += Math.log(p_w_given_l(w, Label.COMEDY));
			prob_t += Math.log(p_w_given_l(w, Label.TRAGEDY));
			prob_c += Math.log(p_w_given_l(w, Label.HISTORY));
		}

		double maxprob = Math.max(Math.max(prob_h, prob_c), prob_t);
		
		// breaking ties
		if(maxprob == prob_h && maxprob == prob_c && maxprob == prob_t)
		{
			double max_priorprob = Math.max(Math.max(comedyPrior,historyPrior), 
					tragedyPrior);
			//no need to break ties here since test cases do not have equal 
			// prior probabilities
			if(max_priorprob == historyPrior)
				return Label.HISTORY;
			else if(max_priorprob == comedyPrior)
				return Label.COMEDY;
			else if(max_priorprob == tragedyPrior)
				return Label.TRAGEDY;
		}
		else if(maxprob == prob_h && maxprob == prob_c)
		{
			if(comedyPrior >= historyPrior)
				return Label.COMEDY;
			else
				return Label.HISTORY;

		}
		else if(maxprob == prob_h && maxprob == prob_t)
		{
			if(historyPrior >= tragedyPrior)
				return Label.HISTORY;
			else
				return Label.TRAGEDY;
		}
		else if(maxprob == prob_c && maxprob == prob_t)
		{
			if(comedyPrior >= tragedyPrior)
				return Label.COMEDY;
			else
				return Label.TRAGEDY;
		}
		// order matters
		else if(maxprob == prob_h)
			return Label.HISTORY;
		else if(maxprob == prob_c)
			return Label.COMEDY;
		else if(maxprob == prob_t)
			return Label.TRAGEDY;
		
		return null; 
	}


}
