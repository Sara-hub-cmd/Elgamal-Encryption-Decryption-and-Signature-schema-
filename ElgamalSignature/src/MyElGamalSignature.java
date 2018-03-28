import java.math.BigInteger;
import java.security.SecureRandom;

public class MyElGamalSignature extends MyTransformer {
	private BigInteger p, g,a, pMinus2,pMinus1;
	private SecureRandom srng;
	private static final BigInteger ZERO = BigInteger.ZERO,ONE = BigInteger.ONE, TWO = ONE.add(ONE);

	//Assume p is prime, g is a gen mod p, r = g^a mod p (a = pvt key)
	public MyElGamalSignature(BigInteger p, BigInteger g, BigInteger a) {
		srng = new SecureRandom();
		this.p = p; this.g = g;  this.a = a;
		pMinus1 = p.subtract(ONE);
		pMinus2 = p.subtract(TWO);
		System.out.println("Signature key:");
		System.out.println("p = " + p.toString(16));
		System.out.println("g = " + g.toString(16));
		System.out.println("a = " + a.toString(16));
	}

	public byte[] transform(byte[] msg) {
		
		long startTm = System.currentTimeMillis();
		//byte[] hashed = sha256_Gen(msg);
		int blkSz = (p.bitLength() - 1)/8;
		//byte[][] ba1 = block(pad(msg, blkSz), blkSz);
		byte[][] ba = block(pad(msg, blkSz), blkSz);
		byte[][] ba3 = new byte[3*ba.length][];
		BigInteger m, hashed_msg, r, s, k = new BigInteger(p.bitLength(), srng),ktemp = ZERO;
		System.err.println("" + ba.length + " blocks");
		
		for (int i=0; i<ba.length; i++) {
			m = new BigInteger(1, ba[i]);      //make +ve BigInt out of current blk
			byte[] hashed = sha256_Gen(getBytes(m));
			hashed_msg = new BigInteger(1, hashed);
			
			//Step 1 - Generate k.
			while(!ktemp.equals(BigInteger.ONE)){
				srng = new SecureRandom();
				k = new BigInteger(p.bitLength(), srng);
				k = k.mod(pMinus2).add(ONE);       //rndm k, 0 < k < p-1
				ktemp = k.gcd(pMinus1);
			}
			
			//Step 2 r = g^k (mod p)
			r = g.modPow(k, p);               
			
			BigInteger ar = a.multiply(r);
			s = hashed_msg.subtract(ar);
			s = s.multiply(k.modInverse(pMinus1));
			s = s.mod(pMinus1);
			
			ba3[3*i]   = getBytes(r);         // convert to bytes
			ba3[3*i+1] = getBytes(s);
			ba3[3*i+2] = getBytes(m);
			if (i%10 == 0) System.err.print("\rBlock " + i);
		}
		System.err.println("\rSignature took " +
				(System.currentTimeMillis()-startTm) + " ms");
		return unblock(ba3, blkSz+1);
	}

}
