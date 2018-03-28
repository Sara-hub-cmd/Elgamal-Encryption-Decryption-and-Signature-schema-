import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class MyVerification {
	
	private BigInteger p, g, y, pMinus2;
	private SecureRandom srng;
	private static final int CRTTY = 300;
	private static final String configPath = "PublicElGamalConfig.txt";
	private static final BigInteger
	ZERO = BigInteger.ZERO,   ONE = BigInteger.ONE,
	TWO  = ONE.add(ONE),    THREE = TWO.add(ONE);

	
	public MyVerification() {
		srng = new SecureRandom();
		try {
			BufferedReader in = new BufferedReader(new FileReader(configPath));
			p = new BigInteger(in.readLine(), 16);
			g = new BigInteger(in.readLine(), 16);
			y = new BigInteger(in.readLine(), 16);
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


	public BigInteger getP() { return p; }
	public BigInteger getG() { return g; }
	public BigInteger getY() { return y; }


	public MyElGamalVerification getVerify() {
		return new MyElGamalVerification(p, g, y);
	}

}
