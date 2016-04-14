
/**
 * All of the configuration information will be placed in this file for
 * convenient update and management.
 * 
 * @author Yu Zhang
 * 
 *         Apr 2, 2016
 *
 */
public class Settings {

    // The rank of the master processor.
    public static final int MAIN_PROCESSOR = 0;
    // For sorting data, it represents that the next data will be greater than
    // the previous data.
    public static final int GREATER = 1;

    // The mode for reading a file.
    public static final String READ_MODE = "r";
    // public static final String CSV_PATH = "/Users/zhangyu/Desktop/Twitter
    // Data/miniTwitter_5744.csv";
    public static final String CSV_PATH = "/data/projects/COMP90024/twitter.csv";
    public static final String FILE_DECODE = "UTF-8";

    public static final int TOP_NUM = 10;
    public static final String TWITTER = "@";
    public static final String TOPIC = "#";

    // The tag for classifying the different types.
    public static final int MESSAGE_TAG = 100;
    public static final int TOPIC_TAG = MESSAGE_TAG + 1;
    public static final int MENTIONED_TAG = MESSAGE_TAG + 2;
    public static final int GIVEN_TERM_TAG = MENTIONED_TAG + 3;

}
