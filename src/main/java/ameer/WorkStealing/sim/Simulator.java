/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameer.WorkStealing.sim;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ameer.WorkStealing.WorkStealingThreadPool;
import ameer.WorkStealing.conf.ManufactoringPlan;
import ameer.WorkStealing.sim.task.Produce;
import ameer.WorkStealing.sim.tools.GcdScrewDriver;
import ameer.WorkStealing.sim.tools.NextPrimeHammer;
import ameer.WorkStealing.sim.tools.RandomSumPliers;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	static WorkStealingThreadPool pool;
	public static Warehouse wareHouse = new Warehouse();
	 static String jsonDir;
	 static  int threadsNum;
	 static Object obj;
	 static ConcurrentLinkedQueue<Product> allProducts;
	 static List<JSONArray> productsWaves = new ArrayList();
	  static ConcurrentLinkedQueue<Produce> allProduces = new ConcurrentLinkedQueue<>();
	  static AtomicInteger c = new AtomicInteger(0);
	  

	  
	  
	/**
	* Begin the simulation
	* Should not be called before attachWorkStealingThreadPool()
	*/
    public static ConcurrentLinkedQueue<Product> start(){
    	ConcurrentLinkedQueue<Product> allProducts = new ConcurrentLinkedQueue<>();
    	ConcurrentLinkedQueue<Product> Products = new ConcurrentLinkedQueue<>();
    	ConcurrentLinkedQueue<Product> ProductsCombined = new ConcurrentLinkedQueue<>();
    	ConcurrentLinkedQueue<Product>  allProductsList = new ConcurrentLinkedQueue<>();
    	JSONParser parser = new JSONParser();
		try {
			obj = parser.parse(new FileReader(
			        jsonDir));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

    	

		threadsNum = getThreadsNum();
		WorkStealingThreadPool threadpool = new WorkStealingThreadPool(threadsNum);
		getAllTools();
		getAllPlans();
		attachWorkStealingThreadPool(threadpool);
          for(int i=0;i<getNumOfWaves();i++){
		allProductsList.addAll(getAllProducts(i));
    	
    	for(Product product:allProductsList)
    		allProducts.add(product);
    	 
    	 
		  for(Product product:allProducts){
			  Produce produce = new Produce(product);
			  produce.getResult().whenResolved(() ->
					{
						
		            
		            if(c.incrementAndGet()==Simulator.allProduces.size()){
		            	for(Produce produceProduct:allProduces){
		            		Products.add(produceProduct.product);
		            		
		            	}
		            	c.set(0);
		            }
			        });
				 
			  allProduces.add(produce);
			  
		  }
			 for(Produce produce:allProduces){
			       pool.submit(produce);
			     

				 }
			       pool.start();
			      
			       try {
					pool.shutdown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			       while(Products.size()<allProduces.size());
			       ProductsCombined.addAll(Products);
			       Products.clear();
			       allProducts.clear();
			       allProductsList.clear();
			       allProduces.clear();
          }
			       return ProductsCombined;
    	
    }
	
	/**
	* attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
	* @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
	*/
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
		
		pool = myWorkStealingThreadPool;
	
	}
	
	public static int main(String [] args){
		jsonDir=args[0];
    	
		ConcurrentLinkedQueue<Product> SimulationResult;
		SimulationResult =start();
		FileOutputStream fout;
		try {
			ObjectOutputStream oos;
			fout = new FileOutputStream("result.ser");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(SimulationResult);
		   oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}
	
	 static void getAllPlans(){
		JSONObject jsonObject = (JSONObject)obj;
		JSONArray plansJArray = (JSONArray)jsonObject.get("plans");
		
		for(Object plan:plansJArray){
			JSONObject singlePlan = (JSONObject)plan;
               JSONArray partsNameJA = ((JSONArray)singlePlan.get("parts"));	
               JSONArray toolsNameJA = ((JSONArray)singlePlan.get("tools"));	
               String[] partsName = new String[partsNameJA.size()];	
               String[] toolsName = new String[toolsNameJA.size()];	
               for(int i=0;i<partsName.length;i++){
            	   partsName[i] = (String)partsNameJA.get(i);
               }
               for(int i=0;i<toolsName.length;i++){
            	   toolsName[i] = (String)toolsNameJA.get(i);
               }
               
			
           String productName = ((String)singlePlan.get("product"));	


           
           
           
			ManufactoringPlan manPlan = 
					new ManufactoringPlan(productName, partsName, toolsName);
			wareHouse.addPlan(manPlan);
		}
		
	}
	
	 static int getThreadsNum(){
		JSONObject jsonObject = (JSONObject)obj;
		return (int)(long)jsonObject.get("threads");
	}
	
	 static List<Product> getAllProducts(int idx){
		List<Product> allProducts = new ArrayList();
		List<Object> singleProductWave = new ArrayList();
		JSONObject jsonObject = (JSONObject)obj;
		JSONArray waves = getWaves(jsonObject);
		if(idx==0){
		 for(Object singleWave:waves){
			  productsWaves.add((JSONArray)singleWave);
			  
			  }
		}
		    
		 JSONArray wave = (JSONArray)waves.get(idx);
		  
           	 JSONArray singleWave = (JSONArray)wave;
           	 for(Object singleProduct:singleWave ){
           		 singleProductWave.add(singleProduct);
           	 }
           	 
          
            
           for(Object productInfo:singleProductWave){
           	JSONObject info = (JSONObject)productInfo;
           	long qty = (long)info.get("qty");
           	for(long i=0;i<qty;i++){
           		
           	Product product = new Product((long)info.get("startId")+i,(String)info.get("product"));
           	allProducts.add(product);
           	}
           }
           return allProducts;
	}
	
	 static void getAllTools(){
		JSONObject jsonObject = (JSONObject)obj;
		
		JSONArray toolsJArray = (JSONArray)jsonObject.get("tools");
		
		for(Object toolObj:toolsJArray){
			JSONObject toolJsonObj = (JSONObject)toolObj;
			if(((String)toolJsonObj.get("tool")).equals("gs-driver")){
				long qty = (long)toolJsonObj.get("qty");
				
				GcdScrewDriver gsDriver = new GcdScrewDriver();
				wareHouse.addTool(gsDriver,(int)qty);
			}
			
			if(((String)toolJsonObj.get("tool")).equals("np-hammer")){
				long qty = (long)toolJsonObj.get("qty");
				NextPrimeHammer nphammer = new NextPrimeHammer();
				wareHouse.addTool(nphammer,(int)qty);
			}
			
			if(((String)toolJsonObj.get("tool")).equals("rs-pliers")){
				long qty = (long)toolJsonObj.get("qty");
				
				RandomSumPliers rspliers = new RandomSumPliers();
				wareHouse.addTool(rspliers,(int)qty);
			}
			
		}
	}
	static int getNumOfWaves(){
		JSONObject jsonObject = (JSONObject)obj;
		JSONArray waves = getWaves(jsonObject);
		return waves.size();
		
	}
	 static JSONArray getWaves(JSONObject jsonObject){
		return (JSONArray)jsonObject.get("waves");
	}
	private static void PrintPro(Product product) {
		System.out.println("ProductName: " + product.getName() + "  Product Id = " + product.getFinalId());

		System.out.println("PartsList {");
		if (product.getParts().size() > 0) {
			for (int i = 0; i < product.getParts().size(); i++) {
				PrintPro(product.getParts().get(i));
			}
		}
		System.out.println("}");

	}
}
