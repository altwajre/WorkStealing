package bgu.spl.a2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.test.MergeSort;

public class Multiply extends Task<Integer> {
	int number;
	
	 public static void main(String[] args) throws InterruptedException {
	        WorkStealingThreadPool pool = new WorkStealingThreadPool(2);
	        int n = 100; //you may check on different number of elements if you like
	        int[] array = new Random().ints(n).toArray();

	        Multiply task = new Multiply(100);

	        CountDownLatch l = new CountDownLatch(1);
	        //pool.submit(task);
	        pool.start();
	        pool.submit(task);
	        
	        task.getResult().whenResolved(() -> {
	            //warning - a large print!! - you can remove this line if you wish
	            System.out.println();
	            l.countDown();
	        });

	        l.await();
	        pool.shutdown();
	        
	        
	       
	        
	    }

	 public Multiply(int number){
		 this.number = number;

	 }
	 
	@Override
	protected void start() {
		int multiplyNumber = number*number;
		//System.out.println("Handled by:"+handler.id);
		if(number <=0){
			complete(0);
			System.out.println("complete(0)");
              Thread.currentThread().interrupt();
			}else{
		//System.out.println(number);
		Multiply task = new Multiply(number-1);
		Multiply task2 = new Multiply(number-2);
		
		 List<Task<?>> tasks = new ArrayList<Task<?>>();
		Task<?>[] listOfTasks = {task,task2};//,task3,task4,task5,task6,task7};
		spawn(listOfTasks);
		
		whenResolved(tasks,()->{
			int sum = multiplyNumber;
			
			for(Task someTask:listOfTasks){
			
			    System.out.println(sum +"     "+handler.id);
				complete(sum);
				
				
			}
			
		});

	}
	}
}
