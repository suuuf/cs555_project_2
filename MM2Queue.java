import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;

public class MM2Queue {

    //Initialization variables
    private double lambda;
    private double mu;
    private double blockProb;   //blocking probability
    private double meanCusTime; //average amount of time a customer spends in the system
    private double meanCusNum;  //average number of customers in the system
    private LinkedList<Double> queue;
    private Random r;

    private double nextArrival;
    private double nextDeparture1;
    private double nextDeparture2;

    public MM2Queue(double lambda, double mu, int startCustNum) {
        //Initialize
        this.lambda = lambda;
        this.mu = mu;
        blockProb = 0;
        meanCusTime = 0;
        meanCusNum = 0;
        queue = new LinkedList<Double>();
        //Add items into the queue
        for(int i = 0; i < startCustNum; i++) {
            queue.add(-0.1);
        }
        //Initialize random variable
        r = new Random(System.currentTimeMillis());

        //Set the next arrival time
        nextArrival = exp(lambda);

        //If there is someone in the queue, set the next departure time for server 1;
        //otherwise, mark server as empty
        if (queue.size() >= 1)
            nextDeparture1 = exp(mu);
        else
            nextDeparture1 = Double.POSITIVE_INFINITY;

        //If there is someone in the queue not being worked on by server 1,
        //set the next departure time for server 2;
        //otherwise, mark server as empty
        if (queue.size() >= 2)
            nextDeparture2 = exp(mu);
        else
            nextDeparture2 = Double.POSITIVE_INFINITY;
    }

    //Run the queue for timeLen, set the state variables for that interval,
    //and return a list of the times something entered or left the queue
    //and which it was
    public ArrayList<double[]> runFor(double timeLen) {
        //List of times and state changes
        //A {(time), +1} array has time of arrival into buffer; a {(time), -1} array has
        //time of departure from server
        ArrayList<double[]> toRet = new ArrayList<double[]>();

        //Total number of customers who entered the queue and who were blocked
        double custReceived = 0.0;
        double custBlocked = 0.0;

        //The total wait time for customers
        double totalWait = 0.0;

        //The previous time someone entered/left the queue and the mean customer numbers
        double prevEvent = 0.0;
        meanCusNum = 0.0;

        //Simulate an M/M/2/2+5 queue as long as at least one event will still happen in the time interval
        while (!(nextArrival > timeLen && nextDeparture1 > timeLen && nextDeparture2 > timeLen)) {
            
            //If the next arrival time happens after a departure time,
            //it's a departure from...
            if (nextDeparture1 < nextArrival || nextDeparture2 < nextArrival) {
                //...the first server
                if (nextDeparture1 < nextDeparture2) {
                    //Add in the area under the curve from the previous event
                    //until now; mark now as the next event to look back to
                    meanCusNum += queue.size() * (nextDeparture1 - prevEvent);
                    prevEvent = nextDeparture1;

                    //Into the "what happened?" list, add in departure time
                    //and mark that event was departure
                    toRet.add(new double[]{nextDeparture1, -1});

                    //If the item arrived within the interval, add its
                    //wait time to the total wait time
                    double arriveTime = queue.remove();
                    if(arriveTime >= 0)
                        totalWait += (nextDeparture1 - arriveTime);

                    //If queue size is 0, buffer empty, no one being serviced; mark server empty
                    //If queue size is 1, buffer empty, someone being serviced in server 2
                    //Otherwise, buffer has at least one item; begin servicing it in this server
                    if (queue.size() <= 1)
                        nextDeparture1 = Double.POSITIVE_INFINITY;
                    else
                        nextDeparture1 += exp(mu);

                }
                //... the second server; logic identical to first server, except
                //changing nextDeparture1 to nextDeparture2
                else {
                    meanCusNum += queue.size() * (nextDeparture2 - prevEvent);
                    prevEvent = nextDeparture2;

                    toRet.add(new double[]{nextDeparture2, -1});

                    double arriveTime = queue.remove();
                    if(arriveTime >= 0)
                        totalWait += (nextDeparture2 - arriveTime);

                    if (queue.size() <= 1)
                        nextDeparture2 = Double.POSITIVE_INFINITY;
                    else
                        nextDeparture2 += exp(mu);
                }
            }

            //If next arrival time happens before either departure time, next event is arrival
            else {
                //If queue size is 7, buffer is full and both servers are busy;
                //mark blocked customer and call next arrival time
                if (queue.size() >= 7) {
                    custBlocked += 1;
                    nextArrival += exp(lambda);
                }
                //Otherwise, stick item into queue
                else {
                    //Is a server empty? Use it to begin servicing product immediately
                    if (nextDeparture1 == Double.POSITIVE_INFINITY)
                        nextDeparture1 = nextArrival + exp(mu);
                    else if (nextDeparture2 == Double.POSITIVE_INFINITY)
                        nextDeparture2 = nextArrival + exp(mu);

                    //Add in the area under the curve from the previous event
                    //until now; mark now as the next event to look back to
                    meanCusNum += queue.size() * (nextArrival - prevEvent);
                    prevEvent = nextArrival;

                    //Add arrival time into queue; in "what happened?" list, add in departure time
                    //and mark that event was departure
                    queue.add(nextArrival);
                    toRet.add(new double[]{nextArrival, 1});

                    //Calculate next arrival, increment customers received
                    nextArrival += exp(lambda);
                    custReceived += 1;
                }
            }
        }

        //Calculate average customer number in queue, blocking probability, and average amount of time
        //customer spent in system; we subtract the size of the queue from total customers received
        //because those customers are still in the system, so we don't count them when calculating
        //customer wait time
        meanCusNum = meanCusNum / timeLen;
        blockProb = custBlocked / (custBlocked + custReceived);
        meanCusTime = totalWait / (custReceived - queue.size());

        //Get arrival/departure times ready for next interval
        nextArrival = nextArrival - timeLen;
        nextDeparture1 = nextDeparture1 - timeLen;
        nextDeparture2 = nextDeparture2 - timeLen;

        //Mark arrival times as happening before next interval
        for (int i = 0; i < queue.size(); i++) {
            queue.set(i, queue.get(i) - timeLen);
        }

        //Return event list
        return toRet;
    }

    //Calculate new exponential variable
    private double exp(double lambdaOrMu) {
        return -Math.log(r.nextDouble()) / lambdaOrMu;
    }

    //Get state variables from last run

    public double getBlockProb() {
        return blockProb;
    }

    public double getMeanCusTime() {
        return meanCusTime;
    }

    public double getMeanCusNum() {
        return meanCusNum;
    }
}