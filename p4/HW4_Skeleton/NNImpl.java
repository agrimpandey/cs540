/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public Node outputNode=null;// single output node that represents the result of the regression

	public ArrayList<Instance> trainingSet=null;//the training set

	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs


	/**
	 * This constructor creates the nodes necessary for the neural network
	 * Also connects the nodes of different layers
	 * After calling the constructor the last node of both inputNodes and  
	 * hiddenNodes will be bias nodes. 
	 */

	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;

		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=1;
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}

		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);

		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}

		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);



		Node node=new Node(4);
		//Connecting output node with hidden layer nodes
		for(int j=0;j<hiddenNodes.size();j++)
		{
			NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[j]);
			node.parents.add(nwp);
		}	
		outputNode = node;

	}

	/**
	 * Get the output from the neural network for a single instance. That is, set the values of the training instance to
	the appropriate input nodes, percolate them through the network, then return the activation value at the single output
	node. This is your estimate of y. 
	 */

	public int calculateOutputForInstance(Instance inst)
	{
		// TODO: add code here

		// need to set input???

		// set output
		for(Node hidden_temp: hiddenNodes)
		{
			hidden_temp.calculateOutput();
		}

		outputNode.calculateOutput();
		return (int)outputNode.getOutput();

	}





	/**
	 * Trains a neural network with the parameters initialized in the constructor
	 * The parameters are stored as attributes of this class
	 */

	public void train()
	{
		// TODO: add code here

		// update w/ newest skeleton code
		for(int i=0; i < this.maxEpoch; i++)
		{
			// initialize all weights to 0
			double[][] weight_hidToOut_list = 
					new double[hiddenNodes.size()][1];
			double[][] weight_inputToHid_list = 
					new double[inputNodes.size()][hiddenNodes.size()];

			for(Instance temp_example: this.trainingSet)
			{
				double O = calculateOutputForInstance(temp_example);
				double T = 0; //get max class location for instance
				// create private function?
				double err = T - O;

				//w_jk (hidden to output)
				double g_p_out = (outputNode.getOutput() <= 0) ? 0 : 1;
				int count = 0; // dont zero index?
				for(Node hidden_temp: hiddenNodes)
				{
					weight_hidToOut_list[count][1] = this.learningRate*
							hidden_temp.getOutput()*err*g_p_out;
					count++;
				}

				//w_ij (input to hidden)
				int input_count = 0;
				int hiden_count = 0;
				for(Node input_temp : this.inputNodes)
				{
					double a_i = input_temp.getOutput();
					for(Node hidden_temp: this.hiddenNodes)
					{
						double g_p_hid = (hidden_temp.getOutput() <= 0) ? 0 : 1;
						weight_inputToHid_list[input_count][hiden_count]
								= this.learningRate*
								a_i*g_p_hid*err;
						double foo = 0;
						for(int k=0; k < weight_hidToOut_list.length; k++)
						{
							foo += weight_hidToOut_list[k][1];
						}
						weight_inputToHid_list[input_count][hiden_count]
								= weight_inputToHid_list
								[input_count][hiden_count]*foo*g_p_out;

						hiden_count++;
					}	// end of hidden nodes loop
					input_count++;
				} // end of input nodes loop

				// for all w_pq, update W_pq += w_pq


			} // end of an instance 

			// what to do here?!??!

		} // end of an epoch

	}
	/**
	 * Returns the mean squared error of a dataset. That is, the sum of the squared error (T-O) for each instance
	in the dataset divided by the number of instances in the dataset.
	 */


	public double getMeanSquaredError(List<Instance> dataset){
		//TODO: add code here
		double total = 0;
		for(Instance temp_example: dataset)
		{
			double O = calculateOutputForInstance(temp_example);
			double T = 0; //get max class location for instance
			// create private function?
			double err = T - O;
			total += err*err;
		}
		return total/dataset.size(); //size can't be zero, right??
	}
}
