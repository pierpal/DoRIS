package doris;
import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */

public abstract class Demography {

    static double numberOfSubsegmentsInCM = 100.0;

    abstract double expectedFraction(double[] params, double L1, double L2);

    abstract String ParamsToString(double[] params);

    double expectedLengthInRange(double[] params, double L1, double L2) {

        double interval = 1 / numberOfSubsegmentsInCM;
        double sumP = 0;
        double expectedLength = 0;

        for (double pos = L1; pos < L2; pos += interval) {
            double lengthThisChunk = (pos + pos + interval) / 2;
            double probThisChunk = expectedFraction(params, pos, pos + interval) / lengthThisChunk;
            sumP += probThisChunk;
            expectedLength += probThisChunk * lengthThisChunk;
        }

        return expectedLength / sumP;

    }

    double std(double[] params, double L1, double L2, double genomeSizePerPair) {
        double fraction = expectedFraction(params, L1, L2);
        double expLen = expectedLengthInRange(params, L1, L2);
        return Math.sqrt(genomeSizePerPair * fraction * expLen) / genomeSizePerPair;
    }

    double[] getLengthDistribution(double[] params, double L1, double L2, double genomeSizePerPair, double numberOfSubsegmentsInCM) {
        int bins = (int) Math.ceil((L2 - L1) * numberOfSubsegmentsInCM) + 1; // adding 1 to avoid boundary problems
        double interval = (L2 - L1) / bins;
        double dist[] = new double[bins];
        double p = 0;
        for (int i = 0; i < bins; i++) {
            double currentLen = L1 + i * interval + (interval) / 2;
            dist[i] = expectedFraction(params, L1 + i * interval, L1 + (i + 1) * interval) / currentLen;
            p += dist[i];
        }
        for (int i = 0; i < bins; i++) {
            dist[i] /= p;
        }
        return dist;
    }

    public double[] computeSharingFractionDistribution(double params[], double left, double right, double numberOfSubsegmentsInCM, Demography dem, double genomeSizePerPair) {
        int genomeSize = (int) Math.ceil(numberOfSubsegmentsInCM * genomeSizePerPair);
        double genome[] = new double[genomeSize];
        double baseDistribution[] = dem.getLengthDistribution(params, left, right, genomeSizePerPair, numberOfSubsegmentsInCM);
        double currentDistribution[] = baseDistribution.clone();
        double expLen = dem.expectedLengthInRange(params, left, right);
        double expFrac = dem.expectedFraction(params, left, right);
        double lambda = genomeSizePerPair * expFrac / expLen;
        PoissonDistribution poiss = new PoissonDistribution(lambda);
        double probSegCount = 0;
        for (int count = 0; count < lambda || probSegCount > 0; count++) {
            probSegCount = poiss.probability(count);
            if (count == 0) {
                genome[0] = probSegCount;
            } else {
                if (count > 1) {
                    currentDistribution = convolution(currentDistribution, baseDistribution);
                }
                int position = (int) Math.floor(left * count * numberOfSubsegmentsInCM);
                for (int i = 0; i < currentDistribution.length; i++) {
                    int thisPos = i + position;
                    if (thisPos >= genomeSize) {
                        thisPos = genomeSize - 1;
                    }
                    genome[thisPos] += probSegCount * currentDistribution[i];
                }
            }
        }
        return genome;
    }

    static double[] convolution(double[] p1, double[] p2) {
        double conv[] = new double[p1.length + p2.length - 1];
        int lower, upper;

        for (int i = 0; i < p1.length + p2.length - 1; i++) {
            double s = 0.0;
            if (i - p2.length + 1 < 0) {
                lower = 0;
            } else {
                lower = i - p2.length + 1;
            }
            if (p1.length - 1 < i) {
                upper = p1.length - 1;
            } else {
                upper = i;
            }
            int pos_p1 = lower;
            int pos_p2 = i - lower;
            for (int n = lower; n <= upper; n++) {
                conv[i] += p1[pos_p1] * p2[pos_p2];
                pos_p1++;
                pos_p2--;
            }
        }
        return conv;
    }
}
