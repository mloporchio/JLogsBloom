import org.bouncycastle.jcajce.provider.digest.Keccak.DigestKeccak;

/**
 * A minimalist implementation of Ethereum's logsBloom filters.
 * To insert and check the membership of an arbitrary sequence of bytes, 
 * the filter uses the Keccak-256 cryptographic hash function.
 *  
 * @author Matteo Loporchio
 */
public class LogsBloom {
	/**
	 * Ethereum's logsBloom always use 2048 bits.
	 */
	public final int numBits = 2048;
	
	/**
	 * Stores the bit representation.
	 */
	private long[] bits;
	
	/**
	 * Constructs an empty logsBloom filter.
	 */
	public LogsBloom() {
		this.bits = new long[numBits / (Byte.SIZE * Long.BYTES)];
	}

	/**
	 * Constructs a logsBloom filter from a given array of bytes.
	 * @param data the array of bytes
	 */
	public LogsBloom(byte[] data) {
		// Check the validity of the input array (length must be 256 bytes).
		if (data.length != (numBits / Byte.SIZE)) 
			throw new IllegalArgumentException("Incorrect number of bytes (length must be 256 bytes)");
		this.bits = Bits.toLongArray(data);
	}
	
	/**
	 *  Inserts a given sequence of bytes into the logsBloom filter.
	 *  @param data the array of bytes
	 */
	public void put(byte[] data) {
		if (data == null) return;
	    byte[] digest = keccak256(data);
	    for (int i = 0; i < 6; i += 2) {
	    	int h = Bits.fromBytes((byte) 0x00, (byte) 0x00, digest[i], digest[i+1]),
	    	x = Integer.remainderUnsigned(h, numBits),
	    	pos = numBits - 1 - x;
	    	set(pos);
	    }
	}

	/**
	 *  Checks if a given sequence of bytes has already been inserted in the
	 *  logsBloom filter.
	 *  @param data the array of bytes
	 *  @return false if the sequence has not been inserted, true if it might
	 *  have been added
	 */
	public boolean contains(byte[] data) {
		if (data == null) return false;
		byte[] digest = keccak256(data);
		for (int i = 0; i < 6; i += 2) {
			int h = Bits.fromBytes((byte) 0x00, (byte) 0x00, digest[i], digest[i+1]),
			x = Integer.remainderUnsigned(h, numBits),
			pos = numBits - 1 - x;
			if (!get(pos)) return false;
		}
		return true;
	}
	
	/**
	 * Returns a byte array containing all bits of the logsBloom filter.
	 * @return byte array with all
	 */
	public byte[] getBytes() {
		return Bits.toByteArray(bits);
	}
	
	/**
	 * Sets the i-th bit of the logsBloom filter.
	 * @param i position of the bit
	 */
	private void set(int i) {
		bits[i >>> 6] |= (1L << (Long.SIZE - i - 1));
	}

	/**
	 * Returns the value of the i-th bit of the logsBloom filter.
	 * @param i position of the bit
	 * @return value of the bit
	 */
	private boolean get(int i) {
		return ((bits[i >>> 6] & (1L << (Long.SIZE - i - 1))) != 0);
	}
	
	/**
	 *	Computes the Keccak-256 cryptographic hash of a sequence of bytes.
	 *	@param data an array of bytes representing the input data
	 *	@return the cryptographic digest of the input data
	 */
	public static byte[] keccak256(byte[] data) {
		DigestKeccak dk = new DigestKeccak(256);
		dk.engineUpdate(data, 0, data.length);
		return dk.engineDigest();
	}
}
