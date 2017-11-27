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
        double size;
        double warmupTime;
        double n = 100;

        //These try/catch blocks allow you to only specify lambda and mu,
        //taking default values otherwise

        try {
            size = Double.parseDouble(args[2]);
        } catch (IndexOutOfBoundsException e) {
            size = 0.0;
        }

        try {
            warmupTime = Double.parseDouble(args[3]);
        } catch (IndexOutOfBoundsException e) {
            warmupTime = 100;
        }

        //Initialize list to store events
        ArrayList<double[]> results = new ArrayList<double[]>();

        //Run warmup period n times, gather events
        for(int i = 0; i < n; i++) {
            MM2Queue sysA = new MM2Queue(lambda, mu, (int) size);
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

        //Initialize file, write first line
        PrintWriter writer = new PrintWriter("data_file.txt", "UTF-8");
        writer.println("0.0 "+size);

        //Total number of items across all replications
        size = n*size;
        for(double[] arr : results) {
            //For each event, either increase or decrease the total number of items
            //across replications depending on arrival or departure, then write
            //average number of items to file
            size += arr[1];
            writer.println(arr[0]+" "+String.format("%.4f", size/n));
        }
        //Close file
        writer.close();
        System.out.println("File written!");
    }

    public static ArrayList<Double> avgValues(ArrayList<ArrayList<Double>> toAvg) {
        ArrayList<Double> toRet = new ArrayList<Double>();
        int n = toAvg.size();

        for(int i = 0; i < toAvg.get(0).size(); i++) {
            double sum = 0;

            for(ArrayList<Double> list : toAvg) {
                sum += list.get(i);
            }

            toRet.add(sum/n);
        }

        return toRet;
    }

}