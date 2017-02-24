package bgu.spl.a2.sim.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;

import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

public class Produce extends Task<Product> {
	public Product product;
	AtomicInteger counter = new AtomicInteger(0);
	

	public Produce(Product product){
		this.product = product;
		
		
	}

	@Override
	protected void start() {
	ManufactoringPlan currentPlan = Simulator.wareHouse.getPlan(product.getName());
 
    
	List<Task<Product>> partsAsProduct=new ArrayList<>();
	for(String partName:currentPlan.getParts()){
		Product part = new Product(product.getStartId() +1,partName);
		product.addPart(part);
		Produce partToProduce = new Produce(part);
		partsAsProduct.add(partToProduce);
	}
	

	
	if(product.getParts().size()==0){

		complete(product);
	}
	
	for(Task<Product> task:partsAsProduct)
	spawn(task);
	


   whenResolved(partsAsProduct, ()->{
	  
	   if(currentPlan.getTools().length!=0){
		for(String toolName:currentPlan.getTools()){
			
			Deferred<Tool> toolDeferred = Simulator.wareHouse.acquireTool(toolName);
			toolDeferred.whenResolved(()->{
				
				product.setFinalId(toolDeferred.get().useOn(product));
				Simulator.wareHouse.releaseTool(toolDeferred.get());
				if(counter.incrementAndGet() == currentPlan.getTools().length){

					complete(product);
				}
			});
			
			
		}
	   }else{
          
		   complete(product);
	   }
		

	
		
	
   });		
	
	
		
	}

}
