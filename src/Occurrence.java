import java.io.Serializable;

/**
 * This is designed for storing the original name of each target string and its
 * occurrence. It can be represented to 3 different types, the given term, the
 * Twitter and the topics by using the "tag" value.
 * 
 * @author Yu Zhang 671205
 * 
 *         Apr 2, 2016
 *
 */
public class Occurrence implements Comparable<Occurrence>, Serializable {

    private static final long serialVersionUID = 1L;
    private int occurence = 0;
    private String mentionedName = "";
    private int tag;

    public Occurrence(String mentionedName) {
        this.mentionedName = mentionedName;
        if (mentionedName.contains(Settings.TWITTER)) {
            this.tag = Settings.MENTIONED_TAG;
        }
        else if (mentionedName.contains(Settings.TOPIC)) {
            this.tag = Settings.TOPIC_TAG;
        }
        else {
            this.tag = Settings.GIVEN_TERM_TAG;
        }
        increase(1);
    }

    public int getOccurence() {
        return occurence;
    }

    public String getOriginalName() {
        return mentionedName;
    }

    public int getTag() {
        return tag;
    }

    public void increase(int occurence) {
        this.occurence += occurence;
    }

    @Override
    public int compareTo(Occurrence another) {
        // The given term will be placed at the top of the list.
        if (tag == Settings.GIVEN_TERM_TAG) {
            return Settings.GREATER;
        }
        // Storing by the descending order.
        int diff = another.getOccurence() - occurence;
        // For TreeSet's comparison. if the difference equals 0, then the order
        // will be the First Come Last Order.
        return diff == 0 ? Settings.GREATER : diff;
    }

    @Override
    public String toString() {
        return mentionedName + ", " + occurence;
    }
}