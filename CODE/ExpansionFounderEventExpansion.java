
package doris;

import doris.Demography;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class ExpansionFounderEventExpansion  extends Demography {

    String ParamsToString(double[] params) {
        return ("Current: " + params[0] + "\tGenerations1: " + params[1]
                + "\tAncestral1: " + params[2] + "\tAncestral2: " + params[3]
                + "\tGenerations2: " + params[4] + "\tAncestral3: " + params[5]);
    }

    double expectedFraction(double[] params, double L1, double L2) {

        double C = params[0];
        double G1 = params[1];
        double A1 = params[2];
        double pst = params[3];
        double G2 = params[4];
        double uPst = params[5];

        double PopSize[] = new double[(int) G2 + 2];
        double CP[] = new double[(int) G2 + 2];
        double NC[] = new double[(int) G2 + 2];

        for (int i = 1; i <= G1; i++) {
            PopSize[i] = C * Math.pow((A1 / C), (i / G1));
            CP[i] = 1 / PopSize[i];
            NC[i] = 1 - CP[i];
        }

        for (int i = (int) G1 + 1; i <= G2; i++) {
            PopSize[i] = pst * Math.pow((uPst / pst), ((i - G1 - 1) / (G2 - G1 - 1)));
            CP[i] = 1 / PopSize[i];
            NC[i] = 1 - CP[i];
        }

        PopSize[(int) G2 + 1] = uPst;
        CP[(int) G2 + 1] = 1 / (double) uPst;
        NC[(int) G2 + 1] = 1 - CP[(int) G2 + 1];

        // compute chance of not coalescence
        double NCG[] = new double[(int) G2 + 2];
        NCG[1] = 1;
        for (int i = 2; i <= G2 + 1; i++) {
            NCG[i] = NCG[i - 1] * NC[i];
        }

        // compute prability up to G2
        double p1 = 0.0;
        for (double k = 1.0; k <= G2; k++) {
            double eL = 1. / 50. * (Math.exp(-k * L1 / 50.) * (50. + k * L1) - Math.exp(-k * L2 / 50.) * (50. + k * L2));
            p1 += NCG[(int) k] * CP[(int) k] * eL;
        }

        // compute prability from G1 to infinity
        double NC_G = NCG[(int) G2];
        double A = uPst;
        double cnst = Math.log(1 - 1 / A);
        double p2 = NC_G/A*Math.exp(-cnst*(1+G2))*((Math.exp((1+G2)*(cnst-L1/50))*(-50*cnst*(50+L1+G2*L1)+L1*(100+L1+G2*L1)))/Math.pow((-50*cnst+L1),2)+(Math.exp((1+G2)*(cnst-L2/50))*(50*cnst*(50+L2+G2*L2)-L2*(100+L2+G2*L2)))/Math.pow((-50*cnst+L2),2));

//        for (int i = 1; i <= G2+1; i++) {
//            System.out.println(i + "\t" + PopSize[i]);
//        }

        return p1 + p2;

    }


}
