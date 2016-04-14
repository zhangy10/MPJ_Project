import java.io.File;

import mpi.Comm;
import mpi.MPI;

/**
 * This class is represented for the instance of each logical Processor
 * including splitting file reading for large files in multi-thread environment,
 * sending and receiving message based on the MPI capacities.
 * 
 * @author Yu Zhang 
 * 
 *         Apr 2, 2016
 *
 */
public class Processors {

    private static Processors processors;

    public static synchronized Processors getInstance() {
        if (processors == null) {
            processors = new Processors();
        }
        return processors;
    }

    /**
     * Assigning different range of file size to each thread, which is simply
     * divided by the size of the threads.
     * 
     * @param size
     *            The size of all threads
     * @param currentRank
     *            The rank of the current thread
     * @param searchKey
     *            The given searching term
     * @return A message object for storing the process results
     */
    public Message processFile(int size, int currentRank, String searchKey) {
        File file = new File(Settings.CSV_PATH);
        long sublength = file.length() / size;
        long remainLength = file.length() % size;
        return new ReadFileTask(Settings.CSV_PATH, currentRank * sublength,
                currentRank == size - 1 ? sublength + remainLength : sublength,
                searchKey, currentRank).run();
    }

    /**
     * Non-blocking sending a message to the main processor.
     * 
     * @param msg
     *            The message which is used to send
     * @param com
     *            The instance reference of the MPI.COMM_WORLD
     * @throws Exception
     */
    public void IsendMessage(Message msg, Comm com) throws Exception {
        // Send a message to the master processor
        Object[] sendObj = new Object[1];
        sendObj[0] = (Object) msg;
        com.Isend(sendObj, 0, sendObj.length, MPI.OBJECT,
                Settings.MAIN_PROCESSOR, Settings.MESSAGE_TAG);
    }

    public Message receiveMessage(Comm com) throws Exception {
        return receiveMessage(com, MPI.ANY_SOURCE);
    }

    /**
     * Blocking receiving a message from each source.
     * 
     * @param com
     *            The instance reference of the MPI.COMM_WORLD
     * @param source
     *            The rank of which source
     * @return A message object
     * @throws Exception
     */
    public Message receiveMessage(Comm com, int source) throws Exception {
        Object[] receiveObj = new Object[1];
        com.Recv(receiveObj, 0, receiveObj.length, MPI.OBJECT, source,
                Settings.MESSAGE_TAG);
        return (Message) receiveObj[0];
    }

    /**
     * Gathering all messages from different sources.
     * 
     * @param size
     *            How many messages will be gathered from all sub-source.
     * @param com
     *            The instance reference of the MPI.COMM_WORLD
     * @return A message object
     * @throws Exception
     */
    public Message gatherMessages(int size, Comm com) throws Exception {
        Message combinedMsg = new Message();
        while (size > 0) {
            Message msg = receiveMessage(com);
            if (msg != null) {
                combinedMsg.combine(msg);
            }
            size--;
        }
        return combinedMsg;
    }
}
