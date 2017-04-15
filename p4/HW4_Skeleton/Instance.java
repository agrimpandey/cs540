package HW4_Skeleton;
import java.util.*;
/**
 * Holds data for a particular instance.
 * Attributes are represented as an ArrayList of Doubles
 * Because this is a regression task, output values for each instance should be 
 * the form of a double.
 * Do not modify
 */

public class Instance{
	public ArrayList<Double> attributes;
	public Double output;
	
	public Instance()
	{
		attributes=new ArrayList<Double>();
		output=0.0;
	}
	
}
