/**
 * @author jay
 * Created on 17/11/2016 06:16:39
 *
 */

import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.*;

public class MyElGamal {

	private BigInteger p, g, r, pMinus2;
	private SecureRandom srng;
	private static final int CRTTY = 300;
	private static final String configPath1 = "PublicElGamalConfig.txt";
	private static final BigInteger
	ZERO = BigInteger.ZERO,   ONE = BigInteger.ONE;

	public MyElGamal() {
		srng = new SecureRandom();
		try {
			BufferedReader in = new BufferedReader(new FileReader(configPath1));
			p = new BigInteger(in.readLine(), 16);
			g = new BigInteger(in.readLine(), 16);
			r = new BigInteger(in.readLine(), 16);
			in.close();
		} catch (NumberFormatException ex) {
			System.err.println("Invalid data in config file - " + ex);
			System.exit(1);
		} catch (EOFException ex) {
			System.err.println("Unexpected end of config file");
			System.exit(1);
		} catch (IOException ex) {
			System.err.println("Trouble reading config file");
			System.exit(1);
		} catch (NullPointerException ex) {
			System.err.println("Trouble reading string from config file - " +ex);
			System.exit(1);
		}

		if (!p.isProbablePrime(CRTTY)) {
			System.err.println(p.toString(16) + " is not prime. Terminating.");
			System.exit(1);
		}
		if (g.mod(p).equals(ZERO)) {
			System.err.println(p.toString(16) + " divides " + g.toString(16) +". Terminating.");
			System.exit(1);
		}
	}


	public BigInteger getP() { return p; }
	public BigInteger getG() { return g; }
	public BigInteger getR() { return r; }

	//A message block is considered a BigInteger.
	//Returns pair of BigIntegers comprising the ElGamal cipher-"text"
	public BigInteger[] encrypt(BigInteger m) {
		BigInteger k = new BigInteger(p.bitLength(), srng);
		k = k.mod(pMinus2).add(ONE);
		BigInteger[] cipher = new BigInteger[2];
		cipher[0] = g.modPow(k, p);
		cipher[1] = r.modPow(k, p).multiply(m).mod(p);
		return cipher;
	}


	public MyElGamalEncrypter getEncrypter() {
		return new MyElGamalEncrypter(p, g, r);
	}

	public static void main(String[] args) {
		
			MyElGamal sys = new MyElGamal();
			SecureRandom sr = new SecureRandom();
			
			BigInteger p = sys.getP(),msg = (new BigInteger(p.bitLength(), sr)).mod(p);
			System.out.println("Message = " + msg.toString(16));
			
			BigInteger[] c = sys.encrypt(msg);
			System.out.println("Cipher: c0 = " + c[0].toString(16));
			System.out.println("Cipher: c1 = " + c[1].toString(16));
			
		 
	}
}