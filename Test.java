import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Java class for testing the LogsBloom implementation.
 * 
 * @author Matteo Loporchio
 */
public class Test {
    public static final Gson gson = new Gson();
    public static final int BUFSIZE = 65536;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: Test <inputFile> <outputFile>");
            System.exit(1);
        }
        final String inputFile = args[0], outputFile = args[1];
		// Reads GZIP-compressed JSON file.
		try (
			JsonReader reader = new JsonReader(
				new InputStreamReader(
					new GZIPInputStream(
						new FileInputStream(inputFile), BUFSIZE)));
			PrintWriter out = new PrintWriter(outputFile);
		) {
			reader.beginArray();
			while (reader.hasNext()) {
				// Deserialize block and extract all fields of interest.
				Block b = gson.fromJson(reader, Block.class);
				int blockId = Integer.decode(b.number);
				byte[] logsBloom = Bits.fromHex(b.logsBloom.substring(2)), 
				myLogsBloom = null;
				// Read all transactions, if any.
				if (b.transactions != null) {
					List<String> keys = new ArrayList<>();
					for (Transaction t : b.transactions) {
						if (t.logs != null) {
							for (Log l : t.logs) {
								if (l.address != null) {
									keys.add(l.address.substring(2));
								}
								if (l.topics != null)  {
									for (String topic : l.topics) {
										keys.add(topic.substring(2));
									}
								}
							}
						}
					}
					// After reading all TXs, we can build the filter.
					LogsBloom filter = new LogsBloom();
					for (String key : keys) filter.put(Bits.fromHex(key));
					myLogsBloom = filter.getBytes();
				}
				// If there are no transactions, we just build an empty filter.
				else myLogsBloom = new byte[256];
				// Finally, we print the result to the output file.
				boolean equal = Arrays.equals(logsBloom, myLogsBloom);
				out.printf("%d,%d\n", (equal) ? 1 : 0, blockId);
			}
			reader.endArray();
		}
		catch (Exception e) {
			System.err.printf("Error: %s\n", e.getMessage());
			System.exit(1);
		}
	}
}

/**
 * Represents a TX log.
 * 
 * @author Matteo Loporchio
 */
class Log {
	public final String address;
	public final List<String> topics;
	public final String logIndex;
	
	public Log(String address, List<String> topics, String logIndex) {
		this.address = address;
		this.topics = topics;
		this.logIndex = logIndex;
	}
}

/**
 * Represents an Ethereum transaction.
 * 
 * @author Matteo Loporchio
 */
class Transaction {
	public final String hash;
	public final String blockNumber;
	public final String blockHash;
	public final List<Log> logs;
	
	public Transaction(String hash, String blockNumber, String blockHash, List<Log> logs) {
		this.hash = hash;
		this.blockNumber = blockNumber;
		this.blockHash = blockHash;
		this.logs = logs;
	}
}

/**
 * Represents an Ethereum block.
 * 
 * @author Matteo Loporchio
 */
class Block {
	public final String number;
	public final String logsBloom;
	public final String timestamp;
	public final List<Transaction> transactions;
	
	public Block(String number, String logsBloom, String timestamp, List<Transaction> transactions) {
		this.number = number;
		this.logsBloom = logsBloom;
		this.timestamp = timestamp;
		this.transactions = transactions;
	}
}

