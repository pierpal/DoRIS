
package doris;

import java.util.ArrayList;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class RMSEUtils {

    public static void RMSE_onePop(Grid grid, Demography dem, ArrayList<Double[]> observedFraction, double genomeSizePerPair, boolean printProgress) {
        long start = System.currentTimeMillis(), time = start;
        System.out.println("Starting RMSE grid search");
        double minErr = Double.MAX_VALUE;
        double[] bestParams = null;
        while (grid.hasNext()) {
            double params[] = grid.nextSet();
            double err = 0;
            for (int i = 0; i < observedFraction.size(); i++) {
                Double[] bin = observedFraction.get(i);
                double expected = dem.expectedFraction(params, bin[0], bin[1]);
                double thisErr;
                thisErr = Math.pow((Math.log(bin[2]) - Math.log(expected)), 2.0);
                err += thisErr;
//                if (bin[1]-bin[0]==1) System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + "\t" + thisErr + "\t" + params[0]);
//                System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + " " + Math.log(bin[2]) + " " + Math.log(expected) + " " + thisErr);
            }
            err = Math.sqrt(err / observedFraction.size());
            if (err < minErr) {
                minErr = err;
                bestParams = params.clone();
                System.out.println("RMSE\t" + dem.ParamsToString(params) + "\terror: " + err + "\tNEW BEST");
            } else {
                if (printProgress) {
                    System.out.println("RMSE\t" + dem.ParamsToString(params) + "\terror: " + err);
                }
            }
        }
        System.out.println();
        System.out.println("Done RMSE.\tBest parameters found: " + dem.ParamsToString(bestParams) + "\terror: " + minErr);
        System.out.println();

        System.out.println("from\tto\tobserved\tmodel");
        for (int i = 0; i < observedFraction.size(); i++) {
            Double[] bin = observedFraction.get(i);
            System.out.println(bin[0] + "\t" + bin[1] + "\t" + bin[2] + "\t" + dem.expectedFraction(bestParams, bin[0], bin[1]));
        }
        System.out.println("Runtime RMSE: " + (System.currentTimeMillis() - start) / 1000.0);

    }

    public static void RMSE_twoPops(Grid grid, Demography dem, ArrayList<Double[]> observedFractionPop1,
        ArrayList<Double[]> observedFractionAcross, ArrayList<Double[]> observedFractionPop2, double genomeSizePerPair, boolean printProgress) {
        long start = System.currentTimeMillis(), time = start;
        System.out.println("Starting RMSE grid search");
        double minErr = Double.MAX_VALUE;
        double[] bestParams = null;
        while (grid.hasNext()) {
            double params[] = grid.nextSet();
            params[params.length - 1] = 1.0;
            double err = 0;
            for (int i = 0; i < observedFractionPop1.size(); i++) {
                Double[] bin = observedFractionPop1.get(i);
                double expected = dem.expectedFraction(params, bin[0], bin[1]);
                double thisErr;
                double obs = bin[2];
                thisErr = Math.pow((Math.log(obs) - Math.log(expected)), 2.0);
                err += thisErr;
//                if (bin[1]-bin[0]==1) System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + "\t" + thisErr + "\t" + params[0]);
//                System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + " " + Math.log(bin[2]) + " " + Math.log(expected) + " " + thisErr);
            }
            params[params.length - 1] = 0.0;
            for (int i = 0; i < observedFractionAcross.size(); i++) {
                Double[] bin = observedFractionAcross.get(i);
                double expected = dem.expectedFraction(params, bin[0], bin[1]);
                double thisErr;
                thisErr = Math.pow((Math.log(bin[2]) - Math.log(expected)), 2.0);
                err += thisErr;
//                if (bin[1]-bin[0]==1) System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + "\t" + thisErr + "\t" + params[0]);
//                System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + " " + Math.log(bin[2]) + " " + Math.log(expected) + " " + thisErr);
            }
            params[params.length - 1] = 2.0;
            for (int i = 0; i < observedFractionPop2.size(); i++) {
                Double[] bin = observedFractionPop2.get(i);
                double expected = dem.expectedFraction(params, bin[0], bin[1]);
                double thisErr;
                thisErr = Math.pow((Math.log(bin[2]) - Math.log(expected)), 2.0);
                err += thisErr;
//                if (bin[1]-bin[0]==1) System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + "\t" + thisErr + "\t" + params[0]);
//                System.out.println("RMSE\t\t" + bin[0] + " " + bin[1] + " " + Math.log(bin[2]) + " " + Math.log(expected) + " " + thisErr);
            }
            err = Math.sqrt(err / (observedFractionPop1.size() + observedFractionAcross.size() + observedFractionPop2.size()));
            if (err < minErr) {
                minErr = err;
                bestParams = params.clone();
                System.out.println("RMSE\t" + dem.ParamsToString(params) + "\terror: " + err + " NEW BEST");
            } else {
                if (printProgress) {
                    System.out.println("RMSE\t" + dem.ParamsToString(params) + "\terror: " + err);
                }
            }
        }
        System.out.println();
        System.out.println("Done RMSE.\tBest parameters found: " + dem.ParamsToString(bestParams) + "\terror: " + minErr);
        System.out.println();

        System.out.println("from\tto\tobservedP1\tmodelP1");
        bestParams[bestParams.length - 1] = 1.0;
        for (int i = 0; i < observedFractionPop1.size(); i++) {
            Double[] bin = observedFractionPop1.get(i);
            System.out.println(bin[0] + "\t" + bin[1] + "\t" + bin[2] + "\t" + dem.expectedFraction(bestParams, bin[0], bin[1]));
        }
        System.out.println("from\tto\tobservedAc\tmodelAc");
        bestParams[bestParams.length - 1] = 0.0;
        for (int i = 0; i < observedFractionAcross.size(); i++) {
            Double[] bin = observedFractionAcross.get(i);
            System.out.println(bin[0] + "\t" + bin[1] + "\t" + bin[2] + "\t" + dem.expectedFraction(bestParams, bin[0], bin[1]));
        }
        System.out.println("from\tto\tobservedP2\tmodelP2");
        bestParams[bestParams.length - 1] = 2.0;
        for (int i = 0; i < observedFractionPop2.size(); i++) {
            Double[] bin = observedFractionPop2.get(i);
            System.out.println(bin[0] + "\t" + bin[1] + "\t" + bin[2] + "\t" + dem.expectedFraction(bestParams, bin[0], bin[1]));
        }
        System.out.println("Runtime RMSE: " + (System.currentTimeMillis() - start) / 1000.0);
    }

}
