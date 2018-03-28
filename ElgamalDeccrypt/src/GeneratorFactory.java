/*
 * @author Jay
 * Created on 12/11/2016 10:01:23
 *
 *
 * Makes a random safe prime p of a given minimum bit length,
 * and a generator mod p.
 * [Fermat little theorem => g^(p-1) = 1 mod p if p prime and not p|g;
 *  A generator mod p is an integer g such that for all 0 < k < p-1, g^k <> 1
 *  mod p. It follows that the g^k % p generate all the integers 0 < n < p.]
 */

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class GeneratorFactory {

	private int minBits, crtty; private SecureRandom srng; private BigInteger p, g; private static final BigInteger
	ZERO = BigInteger.ZERO,   ONE = BigInteger.ONE,TWO  = ONE.add(ONE);

	//Constructors
	public GeneratorFactory (int bits) { this(bits, 300); }

	public GeneratorFactory (int bits, int crtty) {
		this(bits, crtty, new SecureRandom());
	}

	public GeneratorFactory (int bits, int crtty, SecureRandom sr) {
		if (bits < 512)
			System.err.println("WARNING: Safe primes should be >= 512 bits long");
		this.minBits = bits;
		this.crtty   = crtty;
		this.srng    = sr;

		System.out.printf("Making a safe prime of at least %d bits...\n", bits);
		long startTm = System.currentTimeMillis(), endTm;
		makeSafePrimeAndGenerator();
		endTm = System.currentTimeMillis();
		System.err.printf("Generating p, g took %d ms\n", endTm - startTm);
		System.out.printf("p = %x (%d bits)\n", p, p.bitLength());
		System.out.printf("g = %x (%d bits)\n", g, g.bitLength());
	}

	/* Method to make a safe prime (stored in this.p)
	 * and a generator (this.g) mod p. Uses method suggested by D Bishop:
	 * (1) Find a safe prime p = 2rt + 1 where r is smallish (~10^9), t prime;
	 * (2) Obtain the prime factorization of p-1 (quickish in view of (1));
	 * (3) Repeatedly make a random g, 1 < g < p until
	 *     for each prime factor f of p-1, g^((p-1)/f) is incogruent to 1 mod p.
	 */
	public void makeSafePrimeAndGenerator() {
		BigInteger r = BigInteger.valueOf(0x7fffffff),t = new BigInteger(minBits, crtty, srng);

		//(1) make prime p
		do {
			r = r.add(ONE);
			p = TWO.multiply(r).multiply(t).add(ONE);
		} while (!p.isProbablePrime(crtty));

		//(2) obtain prime factorization of p-1 = 2rt
		HashSet<BigInteger> factors = new HashSet<BigInteger>();
		factors.add(t); factors.add(TWO);
		if (r.isProbablePrime(crtty))
			factors.add(r);
		else
			factors.addAll(primeFact(r));

		//We have set of prime factors of p-1.
		//Now (3) look for a generator mod p. Repeatedly make
		// a random g, 1 < g < p, until for each prime factor f of p-1,
		//  g^((p-1)/f) is incogruent to 1 mod p.
		BigInteger pMinusOne = p.subtract(ONE), z, lnr;
		boolean isGen;
		do {
			isGen = true;
			do{
				g = new BigInteger(p.bitLength()-1, srng);//random, < p
			}while(g.equals(ZERO));
			for (BigInteger f: factors) { //check cond on g for each f
				z = pMinusOne.divide(f);
				//System.out.printf(" f= %d g = %d (%d bits) z = %d (%d bits)\n",f, g, g.bitLength(),z, z.bitLength());
				lnr = g.modPow(z, p);
				if (lnr.equals(ONE)) {
					isGen = false;
					break; }
			}
		} while (!isGen);
		// Now g is a generator mod p
	} // end of makeSafePrimeAndGenerator() method

	public static HashSet<BigInteger> primeFact(BigInteger n) {
		BigInteger nn = new BigInteger(n.toByteArray()); //clone n
		HashSet<BigInteger> factors = new HashSet<BigInteger>();
		BigInteger dvsr   = TWO,dvsrSq = dvsr.multiply(dvsr);

		while (dvsrSq.compareTo(nn) <= 0) {
			if (nn.mod(dvsr).equals(ZERO)) {
				//divisor <= sqrt of n
				//found a factor (must be prime):
				//add it to set
				factors.add(dvsr);
				while (nn.mod(dvsr).equals(ZERO)) //divide it out from n completely
					nn = nn.divide(dvsr);           //(ensures later factors are prime)
			}
			dvsr = dvsr.add(ONE);               //next possible divisor
			dvsrSq = dvsr.multiply(dvsr);
		}

		//if nn's largest prime factor had multiplicity >= 2, nn will now be 1;
		//if the multimplicity is only 1, the loop will have been exited leaving nn == this prime factor;
		if (nn.compareTo(ONE) > 0)
			factors.add(nn);
		return factors;
	}

	public BigInteger getP() { return p; }
	public BigInteger getG() { return g; }

	//Test whether p is prime, not p|g, and g is a generator mod p.
	public static boolean isGenerator(BigInteger p, BigInteger g, int crtty) {
		System.err.printf("Testing p = %s,\ng = %s\n",p.toString(16), g.toString(16));
		if (!p.isProbablePrime(crtty)) {
			System.err.println("p is not prime.");
			return false;
		}
		if (g.mod(p).equals(ZERO)) {
			System.err.println("p divides g.");
			return false;
		}
		//See note below on generator test
		BigInteger pMinusOne = p.subtract(ONE), z;
		System.err.println("Finding prime factors of p-1 ...");
		//Warning: a large prime factor will take a long time to find!
		HashSet<BigInteger> factors = primeFact(pMinusOne);
		boolean isGen = true;
		for (BigInteger f: factors) { //check cond on g for each f
			z = pMinusOne.divide(f);
			if (g.modPow(z, p).equals(ONE)) {
				isGen = false;
				System.err.println("g is not a generator mod p.");
				break;
			}
		}
		return isGen;
	}

	//Test driver
	public static void main(String[] args) {
		int bitLen = 512;
		if (args.length > 0) {
			try {
				bitLen = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {
				bitLen = 512;
			} }
		GeneratorFactory fact = new GeneratorFactory(bitLen);
		BigInteger p = fact.getP(), g = fact.getG();
		System.out.println("p probable prime: "
				+ (p.isProbablePrime(300)?"yes":"no"));
		if (g.compareTo(p) < 0)
			System.out.println("g < p");
		else
			System.out.println("p divides g: " + (g.mod(p).equals(ZERO)?"yes":"no"));
	}
}
