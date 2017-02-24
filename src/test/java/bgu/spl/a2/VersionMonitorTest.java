package bgu.spl.a2;



import junit.framework.TestCase;

public class VersionMonitorTest extends TestCase {

	VersionMonitor monitor = new VersionMonitor();
	Thread t1,t2;

	
	public void setUp() throws Exception {
		
		
	}


	public void tearDown() throws Exception {
	}

   
	public void testGetVersion() {

		int x=monitor.getVersion();
		assertEquals(monitor.version,x);
		
	}

	
	public void testInc() {
		
		int x=monitor.getVersion();
		monitor.inc();
		assertNotSame(x,monitor.getVersion());
	}

	public void testAwait() {
t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
              try {
            	
				monitor.await(1);
				assertNotSame(1,monitor.getVersion());

				
			} catch (InterruptedException e) {
				System.out.println("interrupted!");
			}	
			}
		});
		t1.start();
		monitor.inc();
	}
	
}
