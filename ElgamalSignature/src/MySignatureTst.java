import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MySignatureTst {

	public static void main(String[] args) throws IOException {
		
		MySignature pkc = new MySignature();
		MyTransformer trfmr;
		trfmr = pkc.getSignature();
		File inFile = new File(args[0]);
		int inputLength = (int)(inFile.length()/100 + 1)*100;
		BufferedInputStream inStrm = new BufferedInputStream(
				new FileInputStream(inFile));
		BufferedOutputStream outStrm = new BufferedOutputStream(
				new FileOutputStream("S/"+args[0]));
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
