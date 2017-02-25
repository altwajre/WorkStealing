/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ameer.WorkStealing.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import ameer.WorkStealing.Task;
import ameer.WorkStealing.WorkStealingThreadPool;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }
    @Override
    protected void start() {
    	
    	if (this.array.length<=1) 
    		{
  
    		this.complete(array);
    	
    		}
    	else
    	{	
    		int [] arrayLeft;
    		int [] arrayRight;
    		if(array.length%2==0)
    		{
    			arrayLeft= new int[array.length/2];
    			arrayRight= new int[array.length/2];
    		}
    		else
    		{
    			arrayLeft= new int[array.length/2];
        		arrayRight= new int[array.length/2+1];
    		}
    		for(int i=0;i<arrayLeft.length;i++) arrayLeft[i]=array[i];
    		for(int i=0;i<arrayRight.length;i++) arrayRight[i]=array[i+arrayLeft.length];
    		MergeSort left = new MergeSort(arrayLeft);
    		MergeSort right = new MergeSort(arrayRight);
    		spawn(left,right);
    		ArrayList<Task<int[]>> arr=new ArrayList<>();
    		arr.add(left);
    		arr.add(right);	
    		whenResolved(arr,()->{
    			int[] leftA=left.getResult().get();
    			int[] rightA=right.getResult().get();
    			int lef=0;
    			int righ=0;
    			while(lef<leftA.length && righ<rightA.length)
    			{
    				
    				if (leftA[lef]<=rightA[righ])
    				{
    					array[lef+righ]=leftA[lef];
    					lef++;
    				}
    				else {
    					array[lef+righ]=rightA[righ];
    					righ++;
    					}
    			}
    			if(lef<leftA.length) for(int j=lef;j<leftA.length;j++) array[righ+j]=leftA[j];
    			if(righ<rightA.length) for(int j=righ;j<rightA.length;j++) array[lef+j]=rightA[j];
    			complete(array);
    		});
    		
    		
    	}
    }

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(200);
        int n = 50; //you may check on different number of elements if you like
        int[] array = {10,8,7,5,4,3,2,6,1,9,45,22,12,70,5897,452,12,87,3,64,7,95,8888,12,45,86};
        System.out.println("initial array: "+ Arrays.toString(array));
        MergeSort task = new MergeSort(array);
        CountDownLatch l = new CountDownLatch(1);

        pool.start();
        pool.submit(task);        
        task.getResult().whenResolved(() ->
		{
            //warning - a large print!! - you can remove this line if you wish
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });
        l.await();
 
       pool.shutdown();

    	
    	
    	System.out.println("FINISHED");

    }
}
