import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * The CodingTree class is the concretization of the Huffman
 * encoding algorithm. Encodes a table of binary strings which correspond
 * to a character's location within the Huffman tree and the steps to proceed to the location.
 *
 * @author Samuel Servane
 * @version 24 May 2019
 */
class CodingTree {

    /** A string containing the full totality of a text file. */
    private String fulltext;

    private List<String> primedText;

    /** A map containing the codes obtained from Huffman tree traversal. */
    private MyHashTable<String, String> codes;

    /** A String representation of the text conversion to binary. */
    private String bits;

    /** A map containing a character mapped with its frequency. */
    private MyHashTable<String, Integer> frequencyTable;

    /** A priority queue a stores Huffman tree elements. */
    private PriorityQueue<MinNode> weightTable;

    /**
     * Constructor that initializes all data elements and
     * begins the compression process of the Huffman algorithm.
     *
     * @param message The string of text to be encoded.
     */
    CodingTree(String message) {

        fulltext = message;
        primedText = new ArrayList<>();
        frequencyTable = new MyHashTable<>(32768);
        codes = new MyHashTable<>(32768);
        weightTable = new PriorityQueue<>();

        compression();
    }

    /**
     * Helper method that creates a frequency table of character elements from
     * the text string. Disallows duplicates through a HashSet data structure.
     */
    private void createFrequencyTable() {

        StringBuilder wordBuilder = new StringBuilder();
        char[] parsedText = fulltext.toCharArray();

        for (char ch:
             parsedText) {

            if (isInWordSet(ch)) {
                wordBuilder.append(ch);
            } else {
                if (wordBuilder.length() > 0) {
                    primedText.add(wordBuilder.toString());
                    wordBuilder.setLength(0);
                }

                wordBuilder.append(ch);
                primedText.add(wordBuilder.toString());
                wordBuilder.setLength(0);
            }
        }

        for (String ps:
             primedText) {
            if (!frequencyTable.containsKey(ps)) {
                frequencyTable.put(ps, 1);
            } else {
                int count = frequencyTable.get(ps);
                frequencyTable.put(ps, (count + 1));
            }
        }
    }

    /**
     * Creates the weight table of all separate forests before the
     * Huffman tree process takes place. Each node has its own corresponding weights
     * based on the character's frequency in the message string.
     */
    private void createWeightTable() {

        ArrayList<String> allKeys = frequencyTable.getKeys();
        for (String s : allKeys) {

            weightTable.add(new MinNode(frequencyTable.get(s), s, frequencyTable.get(s), null, null));
        }
    }

    /**
     * Creates the Huffman tree from the weight queue by merging the
     * two lowest frequencies to a parent node that only contains their
     * combined weights until a single node that is parent to all nodes is
     * left.
     *
     * @return A root MinNode that contains the largest weight.
     */
    private MinNode createHuffmanTree() {
        while (weightTable.size() > 1) {
            MinNode parentLeft = weightTable.poll();
            MinNode parentRight = weightTable.poll();
            MinNode parent = new MinNode(parentLeft.minNodeFrequency + parentRight.minNodeFrequency, null, parentLeft.minNodeFrequency + parentRight.minNodeFrequency, parentLeft, parentRight);
            weightTable.add(parent);
        }

        return weightTable.peek();
    }

    /**
     * A helper method that traverses the Huffman tree through recursion and labels
     * a character that is at a leaf with a corresponding binary sequence. That sequence
     * is mapped to the character.
     *
     * @param concat The string that represent's the character's binary sequence according to the Huffman encoding.
     * @param current The current MinNode that is being visted.
     */
    private void createEncodeTable(String concat, MinNode current) {

        if (!current.isLeaf()) {
            createEncodeTable(concat + "0", current.left);
            createEncodeTable(concat + "1", current.right);
        } else {
            codes.put(current.word, concat);
        }

    }

    /**
     * A helper method that converts the every character in the message
     * to a corresponding binary sequence and appends that sequence to a bit string.
     */
    private void encode() {

        StringBuilder bitstring = new StringBuilder();

        for (String s: primedText) {

            bitstring.append(codes.get(s));
        }

        bits = bitstring.toString();
    }

    /**
     * A method that writes the whole encoding map to an output file.
     */
    private void writeCodes() {

        File fileOut = new File("codes.txt");
        ArrayList<String> allKeys = codes.getKeys();
        ArrayList<String> allValues = codes.getValues();
        int count = 0;

        try {

            FileWriter fileOutWriter = new FileWriter(fileOut);

            while (count < allKeys.size()) {
                fileOutWriter.write("(" + allValues.get(count) + "=" + allKeys.get(count) + "), ");
                count++;

                if ((count % 5 == 0) && (count != 0)) {
                    fileOutWriter.write("\n");
                }
            }

            fileOutWriter.close();
        } catch (IOException ioe) {

            ioe.printStackTrace();
        }
    }

    /**
     * A method that converts the binary bit string representation of
     * the message into bytes that are written to a file.
     */
    private void writeBinaryFile() {

            int padding = bits.length() % 8;

            for (int i = 0; i < padding; i++) {
                bits += "0";
            }

            byte[] bytes = new byte[bits.length() / 8];

            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseUnsignedInt(bits.substring((i * 8), (i * 8) + 8), 2);
            }

            try {

                FileOutputStream fileOut = new FileOutputStream("compressed.txt");
                BufferedOutputStream buffed = new BufferedOutputStream(fileOut);
                for (int i = 0; i < bytes.length; i++) {
                    buffed.write(bytes[i]);
                }
                buffed.close();
                fileOut.close();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }
    }

    /**
     * A method that runs the complete compression method of the Coding Tree class.
     */
    private void compression() {

        createFrequencyTable();
        createWeightTable();
        createEncodeTable("", createHuffmanTree());
        encode();
        writeCodes();
        writeBinaryFile();
        frequencyTable.stats();
    }

    private boolean isInWordSet(char ch) {
        return (Character.isLetter(ch) || Character.isDigit(ch) || ch == '-' || ch == '\'');
    }

    /**
     * A MinNode is a binary node that can store a character and its frequency.
     * If it is a parent node of two lesser weights, then it only contains a totaled
     * frequency of the weights.
     */
    protected class MinNode implements Comparable<MinNode>{

        /** The (combined) frequency value of the node. */
        Integer minNodeFrequency;

        /** The word contained in the node. */
        String word;

        /** The value obtained from frequency. */
        Integer value;

        /** The left node reference. */
        MinNode left;

        /** The right node reference. */
        MinNode right;

        /**
         * A constructor that initializes a MinNode with an internal frequency,
         * a character-frequency wrapper, up to two nodes.
         *
         * @param frequency The frequency of either a combined weight or of the character's frequency.
         * @param leftNode The left MinNode linked to this one.
         * @param rightNode The right MinNode link to this one.
         */
        MinNode(Integer frequency, String word, Integer value, MinNode leftNode, MinNode rightNode) {

            this.minNodeFrequency = frequency;
            this.word = word;
            this.value = value;
            this.left = leftNode;
            this.right = rightNode;
        }

        /**
         * A method that determines if the node has no children.
         *
         * @return The boolean flag corresponding to its parental status.
         */
        boolean isLeaf() {

            return (left == null) && (right == null);
        }

        /**
         * An overridden compareTo() method that determines the if the node's
         * frequency is less than, equal to, or greater than to maintain its order in structures.
         *
         * @param otherNode The other node being tested against this one.
         * @return An integer value representing the results of the test.
         */
        @Override
        public int compareTo(MinNode otherNode) {

            return this.minNodeFrequency - otherNode.minNodeFrequency;
        }

        /**
         * Overridden toString() method that returns the character and its frequency stored in the
         * node if the node is a leaf. Displays only the frequency otherwise.
         *
         * @return The character and its corresponding frequency or frequency only if parent to only weights.
         */
        @Override
        public String toString() {

            if (word != null) {
                return this.word + ", " + this.minNodeFrequency;
            } else {
                return "Parent Frequency: " + this.minNodeFrequency + " Children: " + left.minNodeFrequency + ", " + right.minNodeFrequency;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left, this.right, this.word, this.value);
        }
    }
}
