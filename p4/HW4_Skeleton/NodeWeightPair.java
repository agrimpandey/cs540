package HW4_Skeleton;
/**
 * Class to identfiy connections
 * between different layers.
 * 
 */

public class NodeWeightPair{
	public Node node; //The parent node
	public Double weight; //Weight of this connection
	public Double deltaw_pq;
	//Create an object with a given parent node 
	//and connect weight
	public NodeWeightPair(Node node, Double weight)
	{
		this.node=node;
		this.weight=weight;
	}
	
	public void set_deltaw_pq(double val){
		this.deltaw_pq = val;
	}
	
	public double get_deltaw_pq(){
		return this.deltaw_pq;
	}
}