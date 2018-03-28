
/**
 * @author jay
 * Created on 12/11/2016 10:47:03
 *
 */

import java.math.BigInteger; 

public class GenTest {
  public static void main(String[] args) {
    boolean isGen = GeneratorFactory.isGenerator(
      new BigInteger(args[0]), new BigInteger(args[1]), 1);
    System.err.println(args[1] + (isGen?" is ":" is not ") + "a generator mod "+ args[0]);
  }
}