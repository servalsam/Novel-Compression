import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Main driver class that runs the Huffman Tree algorithm on
 * various literature text files.
 *
 * @author Samuel Wainright, swain91@uw.edu
 * @version 24 May 2019
 */
public class Main {

    /** Prevents instantiation of Main class */
    private Main () {}

    /**
     * Main function that accepts command line arguments. Runs
     * the Hoffman coding algorithm and displays runtime statistics about
     * the compression process.
     *
     * @param args The command line arguments.
     * @throws IOException Throws exception when the designated file is not found.
     */
    public static void main(String[] args) throws IOException {

        Instant start = Instant.now();

        final double KILO = 1024.0;

        String fileName = "WarAndPeace.txt";

        // Extra files
        //String fileName = "AnimalFarm.txt";
        //String fileName = "MobyDick.txt";

        // Read in file
        Stream<String> stream = Files.lines(Paths.get(fileName));
        StringBuilder contentBuilder = new StringBuilder();
        stream.forEach(s -> contentBuilder.append(s).append("\n"));
        String completeText = contentBuilder.toString();

        // Initialize constructor
        CodingTree huffman = new CodingTree(completeText);

        // Stream close
        stream.close();

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        File inputFile = new File(fileName);
        File outputFile = new File("compressed.txt");

        // Statistics
        BigDecimal uncompressedSize = new BigDecimal(inputFile.length() / KILO).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal compressedSize = new BigDecimal(outputFile.length() / KILO).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal compressionRatio = new BigDecimal(100 - ((compressedSize.doubleValue() / uncompressedSize.doubleValue()) * 100.0)).setScale(4, RoundingMode.HALF_EVEN);

        System.out.println("Time Elapsed: " + timeElapsed + " ms");
        System.out.println("Uncompressed file size: " + uncompressedSize + " KB");
        System.out.println("Compressed file size: " + compressedSize + " KB");
        System.out.println("Compression ratio: " + compressionRatio + "%");

        //testCodingTree();
        //testMyHashTable();
    }

    /**
     * Test function for the coding tree class.
     */
    public static void testCodingTree() {

        CodingTree testHuffman = new CodingTree("Transformers, more than meets the eye!");
        System.out.println();
    }

    /**
     * Test function for the MyHashTableClass.
     */
    public static void testMyHashTable() {

        MyHashTable<String, Integer> myHash = new MyHashTable<>(10);

        System.out.println();

        // Put copy values to test value replacement
        myHash.put("undermining", 1);
        myHash.put("undermining", 2);

        // containsKey() test
        System.out.println(myHash.containsKey("undermining"));

        // get() test
        System.out.println(myHash.get("undermining"));

        // Test updating values
        Integer count = myHash.get("undermining");
        myHash.put("undermining", count + 1);
        System.out.println(myHash.get("undermining"));

        // Test size(), should be 1 then 2
        System.out.println(myHash.getSize());
        myHash.put("daunting", 1);
        System.out.println(myHash.getSize());

        // getKeys() and getValues() test, all should be non-null
        ArrayList<String> keys = myHash.getKeys();
        ArrayList<Integer> values = myHash.getValues();

        for (int i = 0; i < keys.size(); i++) {
            System.out.println(keys.get(i) + ", " + values.get(i));
        }

        myHash.stats();
    }
}
