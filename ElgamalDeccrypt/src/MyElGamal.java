/**
 * @author jay
 * Created on 17/11/2016 06:16:39
 *
 */

import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.*;

public class MyElGamal {

	private BigInteger p, g, a, r, pMinus2;
	private SecureRandom srng;
	private static final int CRTTY = 300;
	private static final String configPath = "MyElGamalConfig.txt";
	private static final String configPath1 = "PublicElGamalConfig.txt";
	private static final BigInteger
	ZERO = BigInteger.ZERO,   ONE = BigInteger.ONE,
	TWO  = ONE.add(ONE),    THREE = TWO.add(ONE);

	//Two constructors -
	//Option 1: generate a random system using specified key size; save config:
	public MyElGamal(int kSz) {  //Random system with at least (kSz) bits in p
		srng = new SecureRandom();
		GeneratorFactory fact = new GeneratorFactory(kSz, CRTTY, srng);
		p = fact.getP(); pMinus2 = p.subtract(TWO);
		g = fact.getG();

		//a should be a random integer in range 1 < a < p-1
		BigInteger pmt = p.subtract(THREE);
		a = (new BigInteger(p.bitLength(), srng)).mod(pmt).add(TWO);
		r = g.modPow(a, p);
		saveConfig();
	}

	//Option 2 (default) -- construct a system from saved values:
	public MyElGamal() {
		srng = new SecureRandom();
		try {
			BufferedReader in = new BufferedReader(new FileReader(configPath));
			p = new BigInteger(in.readLine(), 16);
			a = new BigInteger(in.readLine(), 16);
			g = new BigInteger(in.readLine(), 16);
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
			System.err.println(p.toString(16) + " divides " + g.toString(16) +
					". Terminating.");
			System.exit(1);
		}
	}

	public String toString() {
		String dspStg = "p = " + p.toString(16);
		String nl = dspStg.length() > 80? "\n\n": "\n";
		dspStg += nl + "g = " + g.toString(16);
		dspStg += nl + "a = " + a.toString(16);
		dspStg += nl + "r = " + r.toString(16);
		return dspStg;
	}

	public void saveConfig() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(configPath));
			out.println(p.toString(16));      
			out.println(a.toString(16));
			out.println(g.toString(16));
			out.close();
			PrintWriter out1 = new PrintWriter(new FileWriter(configPath1));
			out1.println(p.toString(16));
			out1.println(g.toString(16));
			out1.println(r.toString(16));
			out1.close();
		} catch (IOException ex) {
			System.err.println("Could not save the config");
		}
	}

	public BigInteger getP() { return p; }
	public BigInteger getG() { return g; }
	public BigInteger getR() { return r; }


	public BigInteger decrypt(BigInteger c0, BigInteger c1) {
		BigInteger c = c0.modPow(a, p).modInverse(p); //c0^-a mod p
		return c.multiply(c1).mod(p);
	}

	public MyElGamalDecrypter getDecrypter() {
		return new MyElGamalDecrypter(p, a);
	}

	/* Test bed
	 * Run with single integer argument, kSz, to generate a system and save it.
	 * Run with no arguments to initialise a system from a saved config,
	 * generate a random BigInteger < p, encrypt it and decrypt it again.
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			new MyElGamal(Integer.parseInt(args[0]));
			System.err.println("Config saved in " + configPath +"and"+configPath1);
		} 
	}
}