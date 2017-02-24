package bgu.spl.a2;


import junit.framework.TestCase;

public class DeferredTest extends TestCase {
    Deferred<Integer> d = new Deferred<Integer>();
    boolean isresolved=false;

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGet() {
		d.resolve(1);
		assertEquals(1,(int)d.get());
	}
	public void testGet2(){
		try{
			d.get();
			assertTrue(false);
			
		}catch(IllegalStateException e){
			assertTrue(true);
		}
			
			
		}
	public void testIsResolved() {
		d.resolve(1);	
		assertTrue(d.isResolved());

	}

	  public void testResolve() {
d.whenResolved(new Runnable() {
			
			@Override
			public void run() {
                   				
			}
		});
		d.resolve(1);
		assertEquals(0,d.callbacks.size());
	}
	  
	  public void testResolve2() {
			d.whenResolved(new Runnable() {
				
				@Override
				public void run() {
	                   				
				}
			});
			d.resolve(1);
			assertEquals(1,(int)d.value);
			
		}
	  
	  public void testResolve3() {
			try{
				d.resolve(null);
				 assertTrue(false);

			  }catch(IllegalStateException e){
		       
		          assertTrue(true);		
			  }
			

			}
	  
	  public void testResolve4() {
			d.resolve(1);
			try{
				d.resolve(2);
				assertTrue(false);
			}catch(IllegalStateException e){
				assertTrue(true);
			}
		
		}
	  

	public void testWhenResolved() {
d.whenResolved(new Runnable() {
			
			@Override
			public void run() {
                   				
			}
		});
	
		assertEquals(1, d.callbacks.size());
	}
	public void testWhenResolved2() {
		d.resolve(1);
		d.whenResolved(new Runnable() {
			
			@Override
			public void run() {
                		
			}
		});
	
		
		
		assertEquals(0, d.callbacks.size());
		
	}

}
