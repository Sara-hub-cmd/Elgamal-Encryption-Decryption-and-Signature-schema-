import java.math.BigInteger;
import java.io.*;

public class MyElGamalTst {
	
	public static void main(String[] args) throws IOException {
		
		MyElGamal pkc = new MyElGamal();
		MyTransformer trfmr;
			trfmr = pkc.getDecrypter();
		File inFile = new File(args[0]);
		int inputLength = (int)(inFile.length()/100 + 1)*100;
		BufferedInputStream inStrm = new BufferedInputStream(
				new FileInputStream(inFile));
		BufferedOutputStream outStrm = new BufferedOutputStream(
				new FileOutputStream("D/"+args[0]));
		System.err.println("Buffer size = " + inputLength);
		
		byte[] buf = new byte[inputLength];
		
		int nBytes = inStrm.read(buf);
		System.out.println("\n" + nBytes + " bytes read");
		
		byte[] msg = new byte[nBytes];
		System.arraycopy(buf, 0, msg, 0, nBytes);
		
		byte[] tmsg = trfmr.transform(msg);
		System.out.println("" + tmsg.length + " bytes produced");
		outStrm.write(tmsg);
		inStrm.close();
		outStrm.close();
	}
}