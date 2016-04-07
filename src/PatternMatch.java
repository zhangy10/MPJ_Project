import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Designed for String Pattern Matching to count the occurrence of the target
 * string in the given text file.
 * 
 * @author Yu Zhang 671205
 * 
 *         Apr 2, 2016
 *
 */
public class PatternMatch {

    /**
     * The regular expression is to select the first word in the given string
     * for word completion.
     */
    private static final String PATTERN_FIRST_WORD = "^([\\w\\-]+)";
    /**
     * The regular expression is to select either the mentioned Twitter and the
     * topics in Twitter data-set.
     */
    private static final String PATTERN_TWITTER = "(?<=^|(?<=[^a-zA-Z0-9-_\\.]))?[@#]([A-Za-z0-9_]+)";

    private static String getPattern(String searchKey) {
        String pattern = searchKey == null ? ""
                : "(\\b(?i)" + searchKey + "\\b)|";
        return pattern + PATTERN_TWITTER;
    }

    /**
     * Search the key patterns and the special given term in the given text.
     * 
     * @param matchText
     *            The given text
     * @param searchKey
     *            The given term
     * @param msg
     *            For storing the results
     * @return A message object
     */
    public static Message searchPattern(String matchText, String searchKey,
            Message msg) {
        if (msg == null) {
            return null;
        }
        Pattern p = Pattern.compile(getPattern(searchKey));
        Matcher m = p.matcher(matchText);
        while (m.find()) {
            msg.put(new Occurrence(m.group()));
        }
        return msg;
    }

    /**
     * Find the first word in the given string for the word completion when the
     * original string was split due to the reading length restrictions.
     * 
     * @param line The given string
     * @return The first word 
     */
    public static String getCompleteWord(String line) {
        Pattern p = Pattern.compile(PATTERN_FIRST_WORD);
        Matcher m = p.matcher(line);
        String restStr = "";
        while (m.find()) {
            restStr = m.group();
            break;
        }
        return restStr;
    }
}
