package ameer.WorkStealing.sim.tools;

import ameer.WorkStealing.sim.Product;

/**
 * an interface to be implemented by all tools
 */
public interface Tool {
	
	/**
	* @return tool name as string
	*/
    public String getType();
	/** Tool use method
	* @param p - Product to use tool on
	* @return a long describing the result of tool use on Product package
	*/
    public abstract long useOn(Product p);
}
