import java.util.LinkedList;
import java.util.Random;

public class MM1Queue { 

    public static void main(String[] args) { 
        double lambda = Double.parseDouble(args[0]);  // arrival rate
        double mu = Double.parseDouble(args[1]);  // service rate

        LinkedList<Double> queue  = new LinkedList<Double>();       // arrival times of customers
        double nextArrival = exp(lambda);     // time of next arrival
        double nextDeparture = Double.POSITIVE_INFINITY;  // time of next departure

        // double expectedWait = 1.0 / (mu - lambda);        // W = expected time in system

        double totalWait = 0.0;
        long customersServiced = 0;

        // simulate an M/M/1 queue
        while (true) {
            // it's an arrival
            if (nextArrival <= nextDeparture) {
                if (queue.isEmpty())
                    nextDeparture = nextArrival + exp(mu);
                queue.add(nextArrival);
                nextArrival += exp(lambda);
            }

            // it's a departure
            else {
                double wait = nextDeparture - queue.remove();

                totalWait += wait;
                customersServiced++;

                if (queue.isEmpty())
                    nextDeparture = Double.POSITIVE_INFINITY;
                else
                    nextDeparture += exp(mu);
            }
        }

    }

    private static double exp(double lambda) {
        if (!(lambda > 0.0))
            throw new IllegalArgumentException("lambda must be positive: " + lambda);
        Random r = new Random();
        return -Math.log(1 - r.nextDouble()) / lambda;
    }

}