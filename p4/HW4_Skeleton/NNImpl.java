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

	public double calculateOutputForInstance(Instance inst)
	{
		int k =0;
		for(Node input_temp: inputNodes)
		{
			if(input_temp.getType()==0)
			{
				input_temp.setInput(inst.attributes.get(k));
			}
			k++;
		}

		// set output
		for(Node hidden_temp: hiddenNodes)
		{
			hidden_temp.calculateOutput();
		}

		outputNode.calculateOutput();
		
		return outputNode.getOutput();

	}





	/**
	 * Trains a neural network with the parameters initialized in the constructor for the number of epochs specified in the instance variable maxEpoch.
	 * The parameters are stored as attributes of this class, namely learningRate (alpha) and trainingSet.
	 * Implement stochastic gradient descent: update the network weights using the deltas computed after each the error of each training instance is computed.
	 * An single epoch looks at each instance training set once, so you should update weights n times per epoch if you have n instances in the training set.
	 */

	public void train()
	{
		for(int i=0; i < this.maxEpoch; i++)
		{
			for(Instance temp_example: this.trainingSet)
			{
				double O = calculateOutputForInstance(temp_example);
				double T = temp_example.output;
				double err = T - O;

				//w_jk (hidden to output)
				double g_p_out = (outputNode.getSum() <= 0) ? 0 : 1;
				for(NodeWeightPair hiddenNode: outputNode.parents)
				{
					hiddenNode.set_deltaw_pq(this.learningRate*
							hiddenNode.node.getOutput()*err*g_p_out);
				}

				//w_ij (input to hidden)
				int hid_count =0;
				for(Node hiddenNode: hiddenNodes){
					double g_p_hid = (hiddenNode.getSum() <= 0) ? 0 : 1;
					if(hiddenNode.getType()==2)
					{
						for(NodeWeightPair inputNode: hiddenNode.parents){
							double a_i = inputNode.node.getOutput();
							inputNode.set_deltaw_pq
							(
									this.learningRate*
									a_i*g_p_hid*(err*
									outputNode.parents.get(hid_count).weight*
									g_p_out)
									);
						}
					} else {
						
						
					}
					hid_count++;
				}
				
				// for all w_pq, update weights
				for(Node hiddenNode: hiddenNodes){
					if(hiddenNode.getType()==2){
						for(NodeWeightPair inputNode: hiddenNode.parents){
							inputNode.weight += inputNode.get_deltaw_pq();
							inputNode.set_deltaw_pq(new Double (0.00));
						}
					}
				}
				for(NodeWeightPair hiddenNode: outputNode.parents)
				{
					hiddenNode.weight += hiddenNode.get_deltaw_pq();
					hiddenNode.set_deltaw_pq(new Double (0.00));
				}

			} // end of an instance 
		} // end of an epoch
	}
	/**
	 * Returns the mean squared error of a dataset. That is, the sum of the squared error (T-O) for each instance
	in the dataset divided by the number of instances in the dataset.
	 */

	public double getMeanSquaredError(List<Instance> dataset){
		double total = 0;
		for(Instance temp_example: dataset)
		{
			double O = calculateOutputForInstance(temp_example);
			double T = temp_example.output;
			double err = T - O;
			total += err*err;
		}
		return total/dataset.size();
	}
}
