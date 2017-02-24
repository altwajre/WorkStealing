package bgu.spl.a2.sim.conf;

/**
 * a class that represents a manufacturing plan.
 *
 **/
public class ManufactoringPlan {
	String product;
	String[] tools;
	String[] parts;
	/** ManufactoringPlan constructor
	* @param product - product name
	* @param parts - array of strings describing the plans part names
	* @param tools - array of strings describing the plans tools names
	*/
    public ManufactoringPlan(String product, String[] parts, String[] tools){
    this.product=product;
	this.parts=new String[parts.length];
	this.tools=new String[tools.length];
	inserToArray(this.parts,parts);
	inserToArray(this.tools,tools);
    }
    
    private static void inserToArray(String[] array1, String[] array2) {
		for(int i=0;i<array1.length;i++)
			array1[i]=array2[i];
	}
	/**
	* @return array of strings describing the plans part names
	*/
    public String[] getParts(){
    	String[] partsB=new String[parts.length];
    	inserToArray(partsB,parts);
    	return partsB;
    }

	/**
	* @return string containing product name
	*/
    public String getProductName(){
    	return product;
    }
	/**
	* @return array of strings describing the plans tools names
	*/
    public String[] getTools(){
    	String[] toolsB=new String[tools.length];
    	inserToArray(toolsB,tools);
    	return toolsB;
    }

}
