import mpi.Comm;
import mpi.MPI;

/**
 * This project is aiming to experience how to use the ideas of the high
 * performance computing platform (Spartan) to solve huge problems based on
 * using Message-Passing Interface meanwhile finding and analyzing the
 * difference between different shell configurations.
 * 
 * In this project, the main task is to program MPI based software to collect
 * the occurrence of the target string in a huge Twitter data-set (10G), while
 * setting up three different Spartan scripts with different nodes and different
 * cores to compare the different execution times.
 * 
 * @author Yu Zhang 
 * 
 *         Apr 2, 2016
 *
 */
public class Spartan {

    private static long runningTime = 0;

    static {
        // When pre-loading is starting, the start time will be recorded.
        runningTime = System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        Comm com = MPI.COMM_WORLD;
        int currentRank = com.Rank();
        int taskSize = com.Size();
        // Representing the given term from java command line.
        String searchKey = null;
        // The first 3 values are required by the MPI lib to initialize the
        // running environment. When the inputs are greater then 3, recording
        // the last input as the special given term.
        if (args.length > 3) {
            searchKey = args[args.length - 1];
        }

        Processors pr = Processors.getInstance();
        // For each interprocess to read the target file by using string pattern
        // matching.
        Message msg = pr.processFile(taskSize, currentRank, searchKey);
        if (currentRank != Settings.MAIN_PROCESSOR) {
            // Other processors will send a non-blocking message to the master
            // processor for combining the final results.
            pr.IsendMessage(msg, com);
        }
        else {
            // Gather the messages sent from other processors.
            Message gatheredMsg = pr.gatherMessages(taskSize - 1, com);
            // Combining the different messages into one message object.
            msg.combine(gatheredMsg);
            // Printing the final matched result.
            if (searchKey != null) {
                System.out.println("<--The Given Term :--> \n\"" + searchKey
                        + "\" Count : " + msg.getSearchResult());
            }
            System.out.println(msg.getTopResults());
            System.out.println("<--Total running time : "
                    + getRunTime(runningTime) + "s-->");
        }
        MPI.COMM_WORLD.Barrier();
        MPI.Finalize();
    }

    public static float getRunTime(long startTime) {
        return (float) (System.currentTimeMillis() - startTime) / 1000;
    }
}
