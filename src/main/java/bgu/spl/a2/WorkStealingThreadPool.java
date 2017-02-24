package bgu.spl.a2;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {

	public Processor[] processors;
	public List<ConcurrentLinkedDeque<Task>> queues = new ArrayList<>();
	public List<Thread> threads = new ArrayList<>();
	VersionMonitor versionMonitor = new VersionMonitor();
	int currentVersion = 0;
	public static boolean finish = false;
	 public static boolean couldSteal0 = false;
	 public static boolean couldSteal1 = false;
	 public static boolean couldSteal2 = false;
	
    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
    	processors = new Processor[nthreads];
        for(int i=0;i<nthreads;i++){
	       processors[i] = new Processor(i, this);
	       queues.add(new ConcurrentLinkedDeque<>());
         }
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
    	//when adding task, call inc() to notifyAll the processors and let them 'race' to handle the task.
      processors[0].addTask(task);
      versionMonitor.inc();
    	
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
     for(Thread t:threads){
    	 t.interrupt();
     }
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
    int processorsLen = processors.length;
   
    /*
     * before run the processors let them wait, because we didn't call submit() yet (we didn't add any task
     * to any processor) 
     */
  
    for(int i=0;i<processorsLen;i++){
    	int j=i;
    	Thread t = new Thread(()->{
    		
    		try {
				versionMonitor.await(currentVersion);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		processors[j].run();
    		
    	});
    	t.start();
    	threads.add(t);
    	
    }
	
    }


    
   
    
    /**
     * This function will be called when processor want to steal from another processor.
     * so this processor fetch the half of its own tasks and return then inside a list.
     * @return list of fetched tasks.
     */
    public ConcurrentLinkedDeque<Task> fetchTasks(int victimId){
    	ConcurrentLinkedDeque<Task>fetchedTasks = new ConcurrentLinkedDeque<Task>();   	 
   	 for(int i=0;i<queues.get(victimId).size()/2;i++){
   		 
   		 try{
   			fetchedTasks.addFirst(queues.get(victimId).removeFirst()); 
   		 }catch(NoSuchElementException e){
   			 return fetchedTasks;
   		 }
   	 }
   	// System.out.println("number of fetched tasks:  " + fetchedTasks.size());
   	 return fetchedTasks;
   	 
    }
    
    
    
}








