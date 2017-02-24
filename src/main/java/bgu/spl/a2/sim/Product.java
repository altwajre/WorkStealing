package bgu.spl.a2.sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import bgu.spl.a2.sim.tools.Tool;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements java.io.Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	long startId;
	long finalId=0;
	List<Product> parts = new ArrayList<>();
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
    	this.name=name;
    	this.startId=startId;
    	finalId=startId;

    }
    
	/**
	* @return The product name as a string
	*/
    public String getName(){
    	return name;
    }

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId(){
    	return startId;
    }
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId(){
    	return finalId;
    }
    
    public void setFinalId(long finalId){
    	this.finalId += finalId;
    }

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){
    	return parts;
    	
    }

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){
    	parts.add(p);
    }

    
    @Override
    public String toString(){
    	String str="";
    	str+="ProductName: " + getName() + "  Product Id = " + getFinalId();

		str+="PartsList {";
		if (getParts().size() > 0) {
			for (int i = 0; i < getParts().size(); i++) {
				str+=getParts().get(i).toString();
				}
		}
		str+="}";
		//str = "Hello";
    return str;
    }

}
