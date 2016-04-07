import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 * The Message is designed as a data structure for invoking MPI interface to
 * communicate with other processors. It has been implemented as serialized
 * immutable object including a hash-map to store the occurrence of each word
 * which will be searched in the given Twitter data-set.
 * 
 * @author Yu Zhang 671205
 * 
 *         Apr 2, 2016
 *
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    /** The source rank where the message was sent */
    private int source;
    /**
     * Storing the occurrence of the words. Each word's name as the key will be
     * converted to the upper-case for insensitive matching in the map.
     */
    private HashMap<String, Occurrence> table = new HashMap<>();

    public Message() {
        // For each message, it will be assigned a unique uuid as the message
        // id in the system.
        this.id = UUID.randomUUID().toString();
        // The default value is from main processor.
        this.source = Settings.MAIN_PROCESSOR;
    }

    public Message(int source) {
        this();
        this.source = source;
    }

    public void put(Occurrence occurence) {
        if (occurence == null) {
            return;
        }
        // Using upper-case for string matching
        String matchName = occurence.getOriginalName().toUpperCase();
        if (table.containsKey(matchName)) {
            // If the word has been existed in the map, then adding the appears'
            // count.
            table.get(matchName).increase(occurence.getOccurence());
            return;
        }
        // If it is not existed in the map, then storing it.
        table.put(matchName, occurence);
    }

    public String getId() {
        return id;
    }

    public int getSource() {
        return source;
    }

    /**
     * This method will be used to combine each message from different
     * sub-processors into the message which is processed in the main processor
     * for integrating the final result.
     * 
     * @param anotherMsg
     *            The another message sent from sub-processors.
     */
    public void combine(Message anotherMsg) {
        if (anotherMsg == null) {
            return;
        }
        Iterator<Entry<String, Occurrence>> iterator = anotherMsg.getIterator();
        while (iterator.hasNext()) {
            Entry<String, Occurrence> entry = iterator.next();
            put(entry.getValue());
        }
    }

    private Iterator<Entry<String, Occurrence>> getIterator() {
        return table.entrySet().iterator();
    }

    /**
     * Selecting the sorted results divided by different tags. More detail, see
     * Settings.
     * 
     * @param tag
     *            different tags defined in Settings.
     * @param topNum
     *            e.g. Top 10 results.
     * @return List<Occurrence> Selected results.
     */
    private List<Occurrence> getTopList(int tag, int topNum) {
        Iterator<Entry<String, Occurrence>> iterator = getIterator();
        // For sorting the results. Using the red-black tree to sort data
        // inside.
        SortedSet<Occurrence> sortedSet = new TreeSet<>();
        while (iterator.hasNext()) {
            Entry<String, Occurrence> entry = iterator.next();
            // The count of the given term will be only one in the map. So, if
            // one of results has been found, then break the loop.
            if (entry.getValue().getTag() == Settings.GIVEN_TERM_TAG
                    && Settings.GIVEN_TERM_TAG == tag) {
                sortedSet.add(entry.getValue());
                break;
            }
            else if (entry.getValue().getTag() == tag) {
                sortedSet.add(entry.getValue());
            }
        }
        ArrayList<Occurrence> list = new ArrayList<>(sortedSet);
        // Split the sorted results
        if (list.size() >= topNum) {
            return list.subList(0, topNum);
        }
        return list;
    }

    private String listToString(List<Occurrence> list) {
        if (list == null) {
            return "";
        }
        StringBuilder sBuilder = new StringBuilder();
        for (Occurrence o : list) {
            sBuilder.append(o.toString() + "\n");
        }
        return sBuilder.toString();
    }

    public String getTopResults() {
        List<Occurrence> twitters = getTopList(Settings.MENTIONED_TAG,
                Settings.TOP_NUM);
        List<Occurrence> topics = getTopList(Settings.TOPIC_TAG,
                Settings.TOP_NUM);
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(
                "<--Top " + Settings.TOP_NUM + " Twitters' List:-->" + "\n");
        sBuilder.append(listToString(twitters));
        sBuilder.append(
                "<--Top " + Settings.TOP_NUM + " Topic' List:-->" + "\n");
        sBuilder.append(listToString(topics));
        return sBuilder.toString();
    }

    public int getSearchResult() {
        List<Occurrence> list = getTopList(Settings.GIVEN_TERM_TAG, 1);
        if (list.size() == 0) {
            return 0;
        }
        Occurrence o = list.get(0);
        return o.getOccurence();
    }

}
