import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

public class WarmupCalculator {

    public static void main(String args[]) throws IOException {
        //Initialization variables; in order: lambda, mu, the amount of items in the queue to begin
        //with, the warmup period to test, the number of replications
        double lambda = Double.parseDouble(args[0]);
        double mu = Double.parseDouble(args[1]);
        double initSize;
        double warmupTime;
        double n;

        //These try/catch blocks allow you to only specify lambda and mu,
        //taking default values otherwise

        try {
            initSize = Double.parseDouble(args[2]);
        } catch (IndexOutOfBoundsException e) {
            initSize = 0.0;
        }

        try {
            warmupTime = Double.parseDouble(args[3]);
        } catch (IndexOutOfBoundsException e) {
            warmupTime = 100;
        }

        try {
            n = Double.parseDouble(args[4]);
        } catch (IndexOutOfBoundsException e) {
            n = 100;
        }

        //Initialize list to store events
        ArrayList<double[]> results = new ArrayList<double[]>();

        //Run warmup period n times, gather events
        for(int i = 0; i < n; i++) {
            MM2Queue sysA = new MM2Queue(lambda, mu, (int) initSize);
            results.addAll(sysA.runFor(warmupTime));
        }
        System.out.println("Results calculated...");

        //Sort events in order of time they happened
        Collections.sort(results, new Comparator<double[]>() {
            public int compare(double[] d1, double[] d2) {
                return Double.compare(d1[0], d2[0]);
            }
        });
        System.out.println("Results sorted...");

        //Total number of items across all replications, plus a variable to keep simultaneous
        //events across multiple replications from all being written
        double totalSize = n*initSize;
        double prevEvent = 0;

        //Initialize file, write first line
        PrintWriter writer = new PrintWriter("data_file.txt", "UTF-8");
        writer.println("0.0 "+String.format("%.4f", totalSize/n));

        //For each event, either increase or decrease the total number of items
        //across replications depending on arrival or departure, then write
        //average number of items to file; the if statement keeps events that may
        //happen to occur at the same time in multiple replications from all being
        //written to the file and only writes the net result, keeping the file
        //size down
        for(double[] arr : results) {
            if(prevEvent != arr[0])
                writer.println(prevEvent+" "+String.format("%.4f", totalSize/n));
            totalSize += arr[1];
            prevEvent = arr[0];
        }
        writer.println(prevEvent+" "+String.format("%.4f", totalSize/n));

        //Close file to prevent memory leaks
        writer.close();
        System.out.println("File written!");
    }

}