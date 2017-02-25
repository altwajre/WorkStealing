package ameer.WorkStealing.sim.tools;

import java.math.BigInteger;

import ameer.WorkStealing.sim.Product;

public class GcdScrewDriver implements Tool {
	String name ="gs-driver";
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public long useOn(Product p) {
		long value=0;
		for(Product part : p.getParts()){
			value+=Math.abs(func(part.getFinalId()));
		}
		return value;
	}
	private long func(long id){
		BigInteger b1=BigInteger.valueOf(id);
		BigInteger b2=BigInteger.valueOf(revers(id));
		return b2.gcd(b1).longValue();
	}
	private long revers(long id){
		long revers=0;
		for(long i=id;i>0;i=i/10){
			revers=revers*10;
			revers+=i%10;
		}
		return revers;
		
	}

}
