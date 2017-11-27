import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

public class ProbFinder {

    public static void main(String args[]) throws IOException {
        //Initialization variables; in order: lambda, mu, the amount of items in the queue to begin
        //with, the warmup period, the length of time each period counted lasts
        double lambda = Double.parseDouble(args[0]);
        double mu = Double.parseDouble(args[1]);
        double initSize;
        double warmup;
        double deltaT;

        //These try/catch blocks allow you to only specify lambda and mu,
        //taking default values otherwise

        try {
            initSize = Double.parseDouble(args[2]);
        } catch (IndexOutOfBoundsException e) {
            initSize = 0;
        }

        try {
            warmup = Double.parseDouble(args[2]);
        } catch (IndexOutOfBoundsException e) {
            warmup = 100;
        }

        try {
            deltaT = Double.parseDouble(args[3]);
        } catch (IndexOutOfBoundsException e) {
            deltaT = 100;
        }

        //Initialize lists to hold the state variables
        ArrayList<Double> blockProb = new ArrayList<Double>();
        ArrayList<Double> meanCusNum = new ArrayList<Double>();
        ArrayList<Double> meanCusTime = new ArrayList<Double>();

        //Create a queue and warm it up
        MM2Queue system = new MM2Queue(lambda, mu, (int) initSize);
        system.runFor(warmup);

        //For 100 times...
        for(int i = 0; i < 100; i++) {
            //run the queue for deltaT and add the state variables
            //to their respective lists
            system.runFor(deltaT);
            blockProb.add(system.getBlockProb());
            meanCusNum.add(system.getMeanCusNum());
            meanCusTime.add(system.getMeanCusTime());
        }

        //Get the average, standard deviation, and lower and upper bounds for
        //each state variable
        double[] blockAvg = avgAndStdDev(blockProb);
        double[] numAvg = avgAndStdDev(meanCusNum);
        double[] timeAvg = avgAndStdDev(meanCusTime);

        //Print results
        System.out.println("Blocking probability");
        System.out.println("--Average: "+blockAvg[0]);
        System.out.println("--Standard deviation: "+blockAvg[1]);
        System.out.println("--Lower bound (90% confidence): "+blockAvg[2]);
        System.out.println("--Upper bound (90% confidence): "+blockAvg[3]);
        System.out.println();

        System.out.println("Number of customers in the system");
        System.out.println("--Average: "+numAvg[0]);
        System.out.println("--Standard deviation: "+numAvg[1]);
        System.out.println("--Lower bound (90% confidence): "+numAvg[2]);
        System.out.println("--Upper bound (90% confidence): "+numAvg[3]);
        System.out.println();

        System.out.println("Time spent in the system");
        System.out.println("--Average: "+timeAvg[0]);
        System.out.println("--Standard deviation: "+timeAvg[1]);
        System.out.println("--Lower bound (90% confidence): "+timeAvg[2]);
        System.out.println("--Upper bound (90% confidence): "+timeAvg[3]);
    }

    public static double[] avgAndStdDev(ArrayList<Double> list) {
        //Initialize array to hold results
        double[] values = new double[4];

        //Calculate average
        double avg = 0.0;
        for (Double d : list)
            avg += d;
        avg = avg/list.size();
        values[0] = avg;

        //Calculate standard deviation
        double stdDev = 0;
        for (Double d : list)
            stdDev += Math.pow((avg - d), 2);
        stdDev = Math.sqrt(stdDev/(list.size() - 1));
        values[1] = stdDev;

        //Calculate lower and upper bounds, respectively, given 90% confidence interval
        values[2] = avg - 1.645*stdDev/(Math.sqrt(list.size()));
        values[3] = avg + 1.645*stdDev/(Math.sqrt(list.size()));

        //Return
        return values;
    }
}