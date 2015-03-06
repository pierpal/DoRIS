package doris;

import java.util.ArrayList;
import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class LikelihoodUtils {

    public static void runMaximumLikelihood(ArrayList<Double[]> segCounts, LikelihoodUtils like, Grid grid, Demography dem, double genomeSizePerPair, double totalPairs, boolean printProgress) {
        long start = System.currentTimeMillis(), time = start;
        System.out.println();
        System.out.println("Starting likelihood grid search");
        double maxLike = -Double.MAX_VALUE;
        double bestParams[] = null;
        grid.resetIterator();
        while (grid.hasNext()) {
            double params[] = grid.nextSet();
            double thisLike = LikelihoodUtils.computeBinLogLikelihood(params, segCounts, totalPairs, dem, genomeSizePerPair);
            if (thisLike > maxLike) {
                maxLike = thisLike;
                bestParams = params.clone();
                System.out.println("LIKE\t" + dem.ParamsToString(params) + "\t" + " logLike: " + thisLike + "\tNEW BEST");
            } else {
                if (printProgress) {
                    System.out.println("LIKE\t" + dem.ParamsToString(params) + "\t" + " logLike: " + thisLike);
                }
            }
        }
        System.out.println();
        if (maxLike == -Double.MAX_VALUE) {
            System.out.println("Done MaxLikelihood.\tNo suitable parameters found");
            System.out.println();
        } else {
            System.out.println("Done MaxLikelihood.\tBest parameters found: " + dem.ParamsToString(bestParams) + "\tlogLikelihood: " + maxLike);
            System.out.println();
                System.out.println("from\tto\tobserved\tmodel");
            for (int i = 0; i < segCounts.size(); i++) {
                Double[] bin = segCounts.get(i);
                double expLen = dem.expectedLengthInRange(bestParams, bin[0], bin[1]);
                double expFrac = dem.expectedFraction(bestParams, bin[0], bin[1]);
                System.out.println(bin[0] + "\t" + bin[1] + "\t" + bin[2] + "\t"
                        + (expFrac * genomeSizePerPair / expLen * totalPairs));
            }
        }
        System.out.println("Runtime Likelihod: " + (System.currentTimeMillis() - start) / 1000.0);
    }

    public static double computeBinLogLikelihood(double params[], ArrayList<Double[]> segCountHist, double totalPairs, Demography dem, double genomeSizePerPair) {
        PoissonDistribution poiss;
        double logLikelihood = 0;
        for (int i=0; i<segCountHist.size(); i++) {
            double left = segCountHist.get(i)[0];
            double right = segCountHist.get(i)[1];
            double observedCount = segCountHist.get(i)[2];
            double expLen = dem.expectedLengthInRange(params, left, right);
            double expFrac = dem.expectedFraction(params, left, right);
            poiss = new PoissonDistribution(totalPairs * genomeSizePerPair * expFrac / expLen);
            logLikelihood += Math.log(poiss.probability((int)observedCount));
        }
        return logLikelihood;
    }
}
