import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	private DecTreeNode root;
	//ordered list of class labels
	private List<String> labels; 
	//ordered list of attributes
	private List<String> attributes; 
	//map to ordered discrete values taken by attributes
	private Map<String, List<String>> attributeValues; 

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train: the training set
	 */
	DecisionTreeImpl(DataSet train) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues =train.attributeValues;
		
		/*List<String> labels = ; 
		List<String> attributes; 
		Map<String, List<String>> attributeValues; */
		
		//List<Instance> examples = new ArrayList<Instance>(train.instances);
		List<Instance> examples = new ArrayList<Instance>();
		for(int i=0; i<train.instances.size();i++){
			String tempS = train.instances.get(i).label;
			List<String> tempL = train.instances.get(i).attributes;
			
			examples.add(new Instance(tempS, tempL));
		}

		root = buildTree(examples, attributes, calculate_majority(examples), 
				"ROOT", attributeValues); 
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;

		//List<Instance> examples = new ArrayList<Instance>(train.instances);
		List<Instance> examples = new ArrayList<Instance>();
		for(int i=0; i<train.instances.size();i++){
			String tempS = train.instances.get(i).label;
			List<String> tempL = train.instances.get(i).attributes;
			
			examples.add(new Instance(tempS, tempL));
		}

		root = buildTree(examples, attributes, 
				calculate_majority(examples), "ROOT", attributeValues);
		root = prune(root, tune);
	}

	private DecTreeNode prune(DecTreeNode tree, DataSet tune)
	{
		List<DecTreeNode> internalNodes_list = new ArrayList<DecTreeNode>();

		boolean no_improvement = true;
		do{
			List<Double> acc_t_N_list = new ArrayList<Double>();
			double accT = getAccuracy(tree, tune);
			//System.out.println(accT);
			internalNodes_list = getInternalNodes(tree, internalNodes_list);
			for(DecTreeNode internal_node: internalNodes_list)
			{
				//System.out.println(internal_node.attribute);
				internal_node.terminal = true; 
				//internal_node.label = calculate_majority(tune.instances);
				acc_t_N_list.add(getAccuracy(tree, tune));
				internal_node.terminal = false; 
			}

			double highestAcc = 0.0;
			int index_hiacc = 0;
			for(int i=0; i< acc_t_N_list.size(); i++)
			{
				if(acc_t_N_list.get(i)>highestAcc)
				{
					highestAcc = acc_t_N_list.get(i);
					index_hiacc = i;
				}
			}
			//System.out.print(highestAcc);
			//System.out.println(accT);
			if(highestAcc >= accT){
				no_improvement = false;
				DecTreeNode t_star = internalNodes_list.get(index_hiacc);
				//t_star.children.clear();
				t_star.terminal = true;
			}
			else
			{
				no_improvement = true;
			}
			internalNodes_list.clear();
		}while(!no_improvement);

		return tree;
	}

	private List<DecTreeNode> getInternalNodes(DecTreeNode tree, List<DecTreeNode> internalNodes_list)
	{
		if(!tree.terminal)
		{
			internalNodes_list.add(tree);
			for (DecTreeNode child : tree.children) 
			{
				List<DecTreeNode> subTree= getInternalNodes(child, 
						internalNodes_list);
				for(DecTreeNode node: subTree)
				{
					//System.out.println(node.attribute);
					if(!internalNodes_list.contains(node))
						internalNodes_list.add(node);
				}
			} 
		}
		return internalNodes_list;
	}

	private String calculate_majority(List<Instance> examples)
	{
		String label1, label2;
		int label1_count = 0, label2_count = 0;

		label1 = examples.get(0).label;
		label2 = "";

		for(Instance temp: examples)
		{
			if(temp.label.equals(label1))
			{
				label1_count++;
			}
			else{
				label2 = temp.label;
				label2_count++;
			}
		}

		if(label1_count > label2_count)
			return label1;
		else
			return label2;  
	}

	private DecTreeNode buildTree(List<Instance> examples,
			List<String> attributes, String default_label,
			String par_attribute_value, Map<String, List<String>> att_val)
	{
		if(examples.isEmpty())
			return new DecTreeNode(default_label, null, 
					par_attribute_value, true);

		if(sameLabel(examples))
			return new DecTreeNode(examples.get(0).label, null, 
					par_attribute_value, true);

		if(attributes.isEmpty())
			return new DecTreeNode(calculate_majority(examples), null, 
					par_attribute_value, true);

		String attr_q = maxInfoGain(examples, attributes, att_val);

		DecTreeNode tree = 
				new DecTreeNode(calculate_majority(examples), attr_q, 
						par_attribute_value, false);
		List<String> values_of_attr_q = att_val.get(attr_q);

		List<String> attributes_newset = new ArrayList<String>(attributes);
		attributes_newset.remove(attr_q);
		Map<String, List<String>> attributeValues_newset = new HashMap(att_val);
		attributeValues_newset.remove(attr_q);

		//System.out.println(attr_q);
		int index = getAttributeIndex(attr_q, attributes);
		boolean[] flags = new boolean[examples.size()];
		int counter = 0;

		for(int i = 0; i < flags.length; i++)
			flags[i] = true;

		for(String v: values_of_attr_q){
			List<Instance> v_ex = new ArrayList<Instance>();
			counter = 0;
			for(Instance example: examples)
			{
				if(flags[counter] == true && example.attributes.get(index).equals(v))
				{
					example.attributes.remove(index);
					v_ex.add(example);
					flags[counter] = false;
				}
				counter++;

			}
			DecTreeNode subtree = buildTree(v_ex, attributes_newset, 
					calculate_majority(examples), v, attributeValues_newset);
			tree.addChild(subtree);
		}

		return tree;
	}

	private boolean sameLabel(List<Instance> examples)
	{
		boolean same_label = true;
		for(Instance temp: examples)
		{
			if(!temp.label.equals(examples.get(0).label))
			{
				same_label = false;
				break;
				//will this break if or loop
			}
		}
		return same_label;
	}

	private String maxInfoGain(List<Instance> examples, List<String> atts, Map<String, List<String>> att_val)
	{
		double entropy = getEntropy(examples);
		int bestAttribute = 0;
		double bestInfoGain = 0;


		for(int i = 0; i < atts.size(); i++)
		{
			double infoGain = getInfoGain(i, examples, entropy, att_val, atts);
			//System.out.println("info gain " + infoGain + " for " + atts.get(i));
			if(infoGain > bestInfoGain)
			{
				bestInfoGain = infoGain;
				bestAttribute = i;
			}
		}
		/*System.out.println("max info gain " + bestInfoGain + " for " + atts.get(bestAttribute));
		System.out.println();
		System.out.println();
		 */
		return atts.get(bestAttribute);
	}

	private double getInfoGain(int index, List<Instance> examples, 
			double entropy, Map<String, List<String>> att_val, List<String> att_list)
	{
		double infoGain = 0.0;
		double condEntropy = 0.0;

		List<String> aValues = att_val.get(att_list.get(index));
		double[][] countValues = new double[aValues.size()][3];
		for(Instance example: examples)
		{
			String blah = example.attributes.get(index);
			for(int i = 0; i < aValues.size(); i++)
			{
				if(blah.equals(aValues.get(i)))
				{
					//if(index==9)
					//System.out.println(aValues.get(i));
					countValues[i][0]++;
					if(example.label.equals(labels.get(0)))
					{
						countValues[i][1]++;
					}
					else
					{
						countValues[i][2]++;
					}
				}
			}
		}

		for(int i = 0; i <aValues.size(); i++)
		{
			double specCondEnt = 0.0;
			for(int j = 1; j <= 2;j++)
			{
				if(countValues[i][0] != 0 && countValues[i][j] != 0)
				{
					specCondEnt += (-1.0)*(countValues[i][j]/countValues[i][0]) * 
							(Math.log10(countValues[i][j]/countValues[i][0])
									/Math.log10(2));
					//if(index==9)
					//System.out.println("innr loop: e  " + entropy + "  sce  " + specCondEnt + "  ce  " + condEntropy);
				}
			}
			/*if(index==9){
				System.out.println("outer loop: e  " + entropy + "  sce  " + specCondEnt + "  ce  " + condEntropy);			
				System.out.println(countValues[i][0]);	
			}	*/	

			condEntropy += specCondEnt *(countValues[i][0]/examples.size()) ;
		}

		//if(index==9)
		//System.out.println("end: e  " + entropy + "  ce  " + condEntropy);
		infoGain = entropy - condEntropy;
		//if(infoGain < 0)
		//return 0.0;
		return infoGain;
	}

	private double getEntropy(List<Instance> examples)
	{
		String label1;
		Double label1_count = 0.0, label2_count = 0.0;
		label1 = examples.get(0).label;

		for(Instance temp: examples)
		{
			if(temp.label.equals(label1))
			{
				label1_count++;
			}
		}

		Double total_count = (double) examples.size();
		label2_count = total_count - label1_count;

		return (-1.0)*(label1_count/total_count)*
				Math.log10(label1_count/total_count)/Math.log10(2) + 
				(-1.0)*(label2_count/total_count)*
				Math.log10(label2_count/total_count)/Math.log10(2);
	}
	@Override
	public String classify(Instance instance) {
		// TODO: add code here
		// call another function and use recursion 
		return classify(instance, root);
	}

	private String classify(Instance instance, DecTreeNode parent) {
		if(parent.terminal){
			return parent.label;
		}
		else{
		String p_att = parent.attribute;
		//System.out.println(getAttributeIndex(p_att));
		//System.out.println(instance.attributes.size());
		String i_att_val = instance.attributes.get(getAttributeIndex(p_att));
		
		for(DecTreeNode kid: parent.children)
		{
			//if(kid.parentAttributeValue.equals(instance.attributes.
			//		get(getAttributeIndex(parent.attribute))))
			String k_p_att_val = kid.parentAttributeValue;
			if(k_p_att_val.equals(i_att_val))
			{
				return classify(instance,kid);
			}
		}}
		String nothing = "nothing";
		return nothing; //use exceptions?
	}



	@Override
	public void rootInfoGain(DataSet train) {
		this.labels = train.labels;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		double entropy = getEntropy(train.instances);
		for(String attribute: attributes)
		{
			System.out.print(attribute + " ");
			System.out.format("%.5f", getInfoGain(getAttributeIndex(attribute),
					train.instances, entropy, attributeValues, attributes));
			System.out.println();
		}
	}

	@Override
	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {

		printTreeNode(root, null, 0);
	}

	/**
	 * Prints the subtree of the node with each line prefixed by 4 * k spaces.
	 */
	public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; i++) {
			sb.append("    ");
		}
		String value;
		if (parent == null) {
			value = "ROOT";
		} else {
			int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
			value = attributeValues.get(parent.attribute).get(attributeValueIndex);
		}
		sb.append(value);
		if (p.terminal) {
			sb.append(" (" + p.label + ")");
			System.out.println(sb.toString());
		} else {
			sb.append(" {" + p.attribute + "?}");
			System.out.println(sb.toString());
			for (DecTreeNode child : p.children) {
				printTreeNode(child, p, k + 1);
			}
		}
	}

	/**
	 * Helper function to get the index of the label in labels list
	 */
	private int getLabelIndex(String label) {
		for (int i = 0; i < this.labels.size(); i++) {
			if (label.equals(this.labels.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attribute in attributes list
	 */
	private int getAttributeIndex(String attr) {
		for (int i = 0; i < this.attributes.size(); i++) {
			//System.out.println("get att ind " + this.attributes.get(i));
			if (attr.equals(this.attributes.get(i))) {
				return i;
			}
		}
		return -1;
	}
	private int getAttributeIndex(String attr, List<String> atts) {
		for (int i = 0; i < atts.size(); i++) {
			//System.out.println("get att ind " + this.attributes.get(i));
			if (attr.equals(atts.get(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
	 */
	private int getAttributeValueIndex(String attr, String value) {
		for (int i = 0; i < attributeValues.get(attr).size(); i++) {
			if (value.equals(attributeValues.get(attr).get(i))) {
				return i;
			}
		}
		return -1;
	}


	/**
   /* Returns the accuracy of the decision tree on a given DataSet.
	 */
	@Override
	public double getAccuracy(DataSet ds){
		//TODO, compute accuracy
		//System.out.println(ds.instances.get(0).attributes.size());
		double correct = 0, total = ds.instances.size();
		for(Instance temp: ds.instances)
		{
			//System.out.println(temp.attributes.size()==attributes.size());
			if(classify(temp).equals(temp.label))
				correct++;
		}
		return correct/total;
	}

	private double getAccuracy(DecTreeNode node, DataSet ds){
		double correct = 0, total = ds.instances.size();
		for(Instance temp: ds.instances)
		{
			if(classify(temp, node).equals(temp.label))
				correct++;
		}
		return correct/total;
	}
}
