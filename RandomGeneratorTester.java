import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class RandomGeneratorTester {

    public static void main(String[] args) {
        System.out.println("Performing distance test...");
        distTest();
        System.out.println();

        System.out.println("Performing comparison test...");
        sameNumTest();
    }

    public static void distTest() {
        //Make array of 1000 integers; these will be the counts of doubles
        //generated in the relevant ranges; each value automatically initialized
        //to 0
        int[] arr = new int[1000];

        //Make new random, seed it
        Random r = new Random(System.currentTimeMillis());

        //The counter works like this: a random double between 0 and 1 is generated and multiplied by 1000
        //It is then cast to an int, rounding down in the process; so, say, 954.9356 will be rounded to 954
        //The index of the cast number in the array has one added to it
        //
        //In this way, the amount of numbers generated in a range is counted; when a number between 0 and 0.001
        //is generated, multiplied (for a range between 0 and 1), and cast, it will be rounded down to 0
        //A similar method goes for numbers between 0.001 and 0.002 (between 1 and 2 after multiplying, rounded
        //to 1), between 0.002 and 0.003 (between 2 and 3 after multiplying, rounded to 2), all the way up to the
        //0.999 to 1 range

        //Do this for one million numbers
        for(int i = 0; i < 1000000; i++) {
            arr[(int)(1000*r.nextDouble())]++;
        }

        //Initialize variables to count the number of ranges that have the amount of
        //numbers generated outside 10, 25, 50, and 100, respectively
        int ov10 = 0;
        int ov25 = 0;
        int ov50 = 0;
        int ov100 = 0;

        //For each count in the array, calculate the difference between 1000 (the ideal count) and
        //the actual count; if this difference lies outside a certain threshold, increment the
        //relevant variable
        for(int i : arr) {
            int dif = Math.abs(1000 - i);
            if(dif > 10)
                ov10++;
            if(dif > 25)
                ov25++;
            if(dif > 50)
                ov50++;
            if(dif > 100)
                ov100++;
        }

        //Print results
        System.out.println("We generated one million random doubles between 0 and 1, with the "+
                           "range subdivided into one thousand equal-sized intervals. Ideally, "+
                           "the count of the numbers in each range should be one thousand.");
        System.out.println("Count difference more than 10: "+ov10);
        System.out.println("Count difference more than 25: "+ov25);
        System.out.println("Count difference more than 50: "+ov50);
        System.out.println("Count difference more than 100: "+ov100);
    }

    public static void sameNumTest() {
        //Create array of doubles, initialize random generator
        double[] arr1 = new double[1000000];
        Random r = new Random(System.currentTimeMillis());

        //Fill array w/ one million random numbers
        for(int i = 0; i < 1000000; i++) {
            arr1[i] = r.nextDouble();
        }

        //Sort the array
        Arrays.sort(arr1);

        //Sleep for a certain period of time, just to make the times a bit more separate
        try {
            Thread.sleep(394);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        //Create another array of doubles, initialize new random generator w/ different seed
        double[] arr2 = new double[1000000];
        Random r2 = new Random(System.currentTimeMillis());

        //Fill array w/ one million random numbers
        for(int i = 0; i < 1000000; i++) {
            arr2[i] = r2.nextDouble();
        }

        //Sort array
        Arrays.sort(arr2);

        //Create variables to hold the index number for arrays 1 and 2,
        //as well as a variable to hold number of shared numbers
        int ind1 = 0;
        int ind2 = 0;
        int share = 0;

        //With the arrays sorted, we can efficiently compare numbers
        //We start at the lowest number in each array and compare; if
        //array 1 is smaller, go to the next largest number in that, but
        //if array 2 is smaller, go to the next largest number in that
        //If the numbers in the arrays are the same, make a note of that
        //If the sequences are not the same, they will share as few numbers
        //as possible

        //As long as there are still numbers to compare...
        while(ind1 < 1000000 && ind2 < 1000000) {
            //Increment array 1 if it's lower
            if(arr1[ind1] < arr2[ind2]) {
                ind1++;
            }
            //Increment array 2 if it's lower
            else if(arr1[ind1] > arr2[ind2]) {
                ind2++;
            }
            //If neither is lower, they share number
            //Increment array 1 and shared number variable
            else {
                ind1++;
                share++;
            }
        }

        //Print results
        System.out.println("We generated two sets of random doubles of one million numbers each, "+
                           "with two different seeds for the generator, and compared them. If the "+
                           "sets of numbers are different, they should share as few numbers as possible.");

        System.out.println("Number of items shared: "+share);
    }

}