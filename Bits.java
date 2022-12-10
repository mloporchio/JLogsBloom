import java.nio.ByteBuffer;

import org.bouncycastle.util.encoders.Hex;

/**
 * Static methods for manipulating bit sequences and producing bit representations 
 * of primitive data types.
 * 
 * @author Matteo Loporchio
 */
public final class Bits {

  /**
   * Returns the integer value whose byte representation corresponds 
   * to the given 4 bytes (in big-endian order).
   * @param b1 first byte
   * @param b2 second byte
   * @param b3 third byte
   * @param b4 fourth byte
   * @return the integer value made up of the given bytes
   */
  public static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
    return ((b1 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 8) | ((b4 & 0xff) << 0);
  }

  /**
   *  Converts an array of bytes to a human-readable hexadecimal string.
   *  @param data array of bytes
   *  @return a hexadecimal string representing the content of the array
   */
  public static String toHex(byte[] data) {
    return Hex.toHexString(data);
  }

  /**
   *  Constructs an array of bytes parsed from a hexadecimal string.
   *  @param str a hexadecimal string
   *  @return an array of bytes parsed from a hexadecimal string
   */
  public static byte[] fromHex(String str) {
    return Hex.decodeStrict(str);
  }
  
  /**
   *  Counts the number of bits equal to 1 in the given array of bytes.
   *  @param data the array of bytes
   *  @return number of ones in the array
   */
  public static int countOnes(byte[] data) {
    int result = 0;
    for (int i = 0; i < data.length; i++)
      result += Integer.bitCount(data[i] & 0xff);
    return result;
  }

  /**
   *  Converts an array of longs into an array of bytes.
   *  @param data array of longs
   *  @return an array containing all bytes of the long values
   */
  public static byte[] toByteArray(long[] data) {
    ByteBuffer bb = ByteBuffer.allocate(data.length * Long.BYTES);
    bb.asLongBuffer().put(data);
    return bb.array();
  }

  /**
   *  Converts an array of bytes into an array of longs.
   *  @param data array of bytes
   *  @return an array containing longs (obtained from the given bytes)
   */
  public static long[] toLongArray(byte[] data) {
    // The size of the input array must be a multiple of 8.
    if (data.length % Long.BYTES != 0) 
      throw new IllegalArgumentException("Incorrect input array size.");
    ByteBuffer bb = ByteBuffer.wrap(data);
    return bb.asLongBuffer().array();
  }

}
