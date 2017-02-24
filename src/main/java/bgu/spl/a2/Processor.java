package bgu.spl.a2;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    int stepToAnotherProcessor = 1 , countingRun = 0;
    public final int id;
    

    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    public void run() {
    	
   //while(!Thread.currentThread().isInterrupted()){
	  // System.out.println("Hi");
      while(pool.queues.get(id).size()>0){
      		handleTask();
      }
   
       steal();
     
  // }

    }
    
    private void handleTask(){
    	try{
    		Task<?> currentTask = pool.queues.get(id).removeLast();
    		currentTask.handle(this);
    	}catch(NoSuchElementException e){
    		
    	run();
    	}
    		

    }
    
    public void addTask(Task<?> task){
    	
    	pool.queues.get(id).add(task);
    }

     /**
      * the process who want to steal check if he can before stealing.
      * @return true if there are more than one task in this processor queue
      */
     public  boolean canSteal(){
    	 return pool.queues.get(id).size()>1;
     }
     /**
     * search,in circular, for processor which has more than 1 task.
     *  the moment we find it we fetch tasks from it
      */
     public void steal(){
       	 int processorsNum = pool.processors.length;
           int victimId = (id+stepToAnotherProcessor)%(processorsNum);
          // System.out.println("steal");
           //add 1 to the step for next stealing
           stepToAnotherProcessor = (stepToAnotherProcessor+1)%(processorsNum);
           
           //if we are in 'this' processor in the array
           if((victimId % processorsNum)==id){
           	//change the victim id to the next one
           	victimId = (id+stepToAnotherProcessor)%(processorsNum);
           }
           //check if this processor can steal from his neighbor (victim)
           if(pool.processors[victimId].canSteal()){
       
           	//System.out.println("processor" + id +" steal from processor"+ victimId);
           	ConcurrentLinkedDeque<Task> stolenTasks = pool.fetchTasks(victimId);
           //get the 'stolen' tasks and run them using the function run. 
           	pool.queues.get(id).addAll(stolenTasks);
           	
           	run();
           }
      
        }
        
     
}
