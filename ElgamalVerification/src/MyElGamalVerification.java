import java.math.BigInteger;

public class MyElGamalVerification extends MyTransformer {

	private BigInteger p, y, g,pMinus1;
	private boolean inBound,inScope;
	private static final BigInteger
	ZERO = BigInteger.ZERO,   ONE = BigInteger.ONE;

	public MyElGamalVerification (BigInteger p, BigInteger g , BigInteger y) {
		this.p = p; this.g = g;this.y = y;
		pMinus1 = p.subtract(ONE);
		System.out.println("Verification key:");
		System.out.println("p = " + p.toString(16));
		System.out.println("g = " + g.toString(16));
		System.out.println("y = " + y.toString(16));
	}

	public byte[] transform(byte[] msg) {

		long startTm = System.currentTimeMillis();
		int blkSz = (p.bitLength()-1)/8 + 1;
		byte[] hash;
		byte[][] ba3 = block(msg, blkSz);
		byte[][] ba  = new byte[ba3.length/3][];
		BigInteger m, r, s, c,txt;
		System.err.println("" + ba.length + " blocks");

		for (int i=0; i<ba.length; i++) {
			r = new BigInteger(1, ba3[3*i]);   //make +ve BigInts out of
			s= new BigInteger(1, ba3[3*i+1]); //current  blocks
			txt = new BigInteger(1, ba3[3*i+2]);
			System.out.println("txt "+txt);

			if((r.compareTo(ZERO)==1)&(r.compareTo(p)==-1)&(s.compareTo(ZERO)==1)&(s.compareTo(pMinus1)==-1)){
				inBound = true;
			}
			else    
				inBound = false;
			
			ba[i] = getBytes(txt);                //convert to bytes
			System.out.println("txt "+ba[i]);
			
			hash = sha256_Gen(ba[i]);
			BigInteger file_hash = new BigInteger(hash);
			BigInteger first_side = g.modPow(file_hash, p);
			BigInteger t1 = y.modPow(r, p);
			BigInteger t2 = r.modPow(s, p);
			BigInteger tmp1 = t1.multiply(t2);
			BigInteger second_side = tmp1.mod(p);

			if(first_side.equals(second_side)){
				inScope = true;
			}
			else    
				inScope = false;
			
			if (i%10 == 0) System.err.print("\rBlock " + i);

			if(inBound){
				System.err.println("-------------\nFile Verified!\n---------------");
				System.err.println("0<r<p  and  o<s<p-1");
				System.err.println("g^H(m)(mod P) = y^r.r^s(mod p)");
			}
			else{
				System.err.println("--------\nWARNING\n--------\nDigital Signature failed!");
			}
		}
		System.err.println("\nVerification took " + (System.currentTimeMillis()-startTm) + " ms");
		return unpad(unblock(ba, blkSz-1), blkSz-1);
	}
}
