import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * The reading file task is designed to process a range of the file (from the
 * start position to the total length) in the multi-thread environment.
 * 
 * @author Yu Zhang 671205
 * 
 *         Apr 2, 2016
 *
 */
public class ReadFileTask {

    private int rank;
    private String filePath;
    private long startPos;
    private long totalLength;
    private String searchKey;
    private int bufferSize = 1024;

    public ReadFileTask(String path, long start, long length, String searchKey,
            int rank) {
        this.filePath = path;
        this.startPos = start;
        this.totalLength = length;
        this.searchKey = searchKey;
        this.rank = rank;
    }

    public Message run() {
        BufferedReader br = null;
        // Preparing a empty message from the current rank thread.
        Message msg = new Message(rank);
        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath),
                            Settings.FILE_DECODE),
                    bufferSize);
            // Skip to the start reading position.
            br.skip(startPos);
            // Read the text line-by-line.
            String line = null;
            while ((line = br.readLine()) != null) {
                int hasReadLength = readSize(line);
                if (hasReadLength > totalLength) {
                    // if the current length of the read line is greater than
                    // the given range, then the line will be split.
                    hasReadLength = (int) totalLength;
                    line = splitStr(hasReadLength, line);
                }
                msg = PatternMatch.searchPattern(line.replace("\"\"", "\""),
                        searchKey, msg);
                totalLength -= hasReadLength;
                if (totalLength == 0) {
                    break;
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return msg;
    }

    /**
     * Convert a string to a byte array to get the byte length.
     * 
     * @param line
     * @return The byte length
     * @throws UnsupportedEncodingException
     */
    private static int readSize(String line)
            throws UnsupportedEncodingException {
        return line.getBytes(Settings.FILE_DECODE).length;
    }

    /**
     * Split the line to the given length.
     * 
     * @param length
     *            The given read length.
     * @param line
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String splitStr(int length, String line)
            throws UnsupportedEncodingException {
        byte[] copyBytes = new byte[length];
        byte[] bytes = line.getBytes(Settings.FILE_DECODE);
        System.arraycopy(bytes, 0, copyBytes, 0, copyBytes.length);
        String splitStr = new String(copyBytes, 0, copyBytes.length,
                Settings.FILE_DECODE);
        String restStr = line.substring(splitStr.length());
        // To tackle the issue of that one word could be split into 2 pieces.
        // By using pattern matching to find the first word in the rest of the
        // string for word completion.
        return splitStr + PatternMatch.getCompleteWord(restStr);
    }

}