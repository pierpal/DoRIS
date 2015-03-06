package doris;

import doris.Demography;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class SplitExpConstAsymMig extends Demography {

    String ParamsToString(double[] params) {
        return ("CurrentPop1: " + params[0] + "\tpop1Ancestral: " + params[1] + "\tpop2Current: " + params[2]
                + "\tpop2Ancestral: " + params[3] + "\tancestralTot: " + params[4] + "\tgeneration: " + params[5]
                + "\tm12: " + params[6] + "\tm21: " + params[7]);
    }

    double expectedFraction(double[] params, double L1, double L2) {

        double C1 = params[0];
        double A1 = params[1];
        double C2 = params[2];
        double A2 = params[3];
        double Atot = params[4];
        double G = params[5];
        double m12 = params[6];
        double m21 = params[7];
        double popSelector = params[8]; // 1 if both in 1, 2 if both in 2, in two pops otherwise

        double[] state1 = new double[2];
        double[] state2 = new double[2];

        if (popSelector == 1.0) {
            state1[0] = 1.0;
            state1[1] = 0.0;
            state2[0] = 1.0;
            state2[1] = 0.0;
        } else if (popSelector == 2.0) {
            state1[0] = 0.0;
            state1[1] = 1.0;
            state2[0] = 0.0;
            state2[1] = 1.0;
        } else {
            state1[0] = 1.0;
            state1[1] = 0.0;
            state2[0] = 0.0;
            state2[1] = 1.0;
        }

        double[][] M_orig = {{1.0 - m12, m12}, {m21, 1.0 - m21}};
        double[][] M_curr = {{1.0 - m12, m12}, {m21, 1.0 - m21}};

        double[] state1_curr = {state1[0], state1[1]};
        double[] state2_curr = {state2[0], state2[1]};

        double Pop1Size[] = new double[(int) G + 2];
        double Pop2Size[] = new double[(int) G + 2];
        double CP[] = new double[(int) G + 2];
        double NC[] = new double[(int) G + 2];

//        System.out.println(this.ParamsToString(params));

        for (int i = 1; i <= G; i++) {
            Pop1Size[i] = C1 * Math.pow((A1 / C1), (i / G));
            Pop2Size[i] = C2 * Math.pow((A2 / C2), (i / G));
//                System.out.println(i + "\t" + PopSize[i]);
            if (i > 1) {
                M_curr[0][0] = M_curr[0][0] * M_orig[0][0] + M_curr[0][1] * M_orig[1][0];
                M_curr[0][1] = M_curr[0][0] * M_orig[0][1] + M_curr[0][1] * M_orig[1][1];
                M_curr[1][0] = M_curr[1][0] * M_orig[0][0] + M_curr[1][1] * M_orig[1][0];
                M_curr[1][1] = M_curr[1][0] * M_orig[0][1] + M_curr[1][1] * M_orig[1][1];
            }
            state1_curr[0] = state1[0] * M_curr[0][0] + state1[1] * M_curr[1][0];
            state1_curr[1] = state1[0] * M_curr[0][1] + state1[1] * M_curr[1][1];
            state2_curr[0] = state2[0] * M_curr[0][0] + state2[1] * M_curr[1][0];
            state2_curr[1] = state2[0] * M_curr[0][1] + state2[1] * M_curr[1][1];
            CP[i] = (state1_curr[0] * state2_curr[0]) / Pop1Size[i] + (state1_curr[1] * state2_curr[1]) / Pop2Size[i];
            NC[i] = 1 - CP[i];

//            System.out.println("\n" + i + " " + m12 + " " + m21);
//            System.out.println(state1_curr[0] + " " + state1_curr[1]);
//            System.out.println(state2_curr[0] + " " + state2_curr[1]);
//            System.out.println(M_curr[0][0] + " " + M_curr[0][1]);
//            System.out.println(M_curr[1][0] + " " + M_curr[1][1]);
//            if (popSelector==1.0&&L1==1&&L2==2) System.out.println("\n" + i + " " + CP[i] + " " + Pop1Size[i] + " " + Pop2Size[i]);
        }

        Pop1Size[(int) G + 1] = Atot;
        CP[(int) G + 1] = 1 / (float) Atot;
        NC[(int) G + 1] = 1 - CP[(int) G + 1];

        // compute chance of not coalescence
        double NCG[] = new double[(int) G + 2];
        NCG[1] = 1;
        for (int i = 2; i <= G + 1; i++) {
            NCG[i] = NCG[i - 1] * NC[i];
        }

        // compute prability up to G1
        double p1 = 0.0;
        for (int k = 1; k <= G; k++) {
            double eL = 1. / 50. * (Math.exp(-k * L1 / 50.) * (50. + k * L1) - Math.exp(-k * L2 / 50.) * (50. + k * L2));
            p1 += NCG[k] * CP[k] * eL;
//            if (popSelector == 1.0 && L1 == 1 && L2 == 2) {
//                System.out.println(k + " " + NCG[k] + " " + CP[k] + " " + eL);
//            }
        }

        // compute prability from G1 to infinity
        double NC_G = NCG[(int) G];
        double cnst = Math.log(1 - 1 / Atot);
        double p2 = NC_G / Atot * Math.exp(-cnst * (1 + G)) * ((Math.exp((1 + G) * (cnst - L1 / 50)) * (-50 * cnst * (50 + L1 + G * L1) + L1 * (100 + L1 + G * L1))) / Math.pow((-50 * cnst + L1), 2) + (Math.exp((1 + G) * (cnst - L2 / 50)) * (50 * cnst * (50 + L2 + G * L2) - L2 * (100 + L2 + G * L2))) / Math.pow((-50 * cnst + L2), 2));

//          System.out.println(this.ParamsToString(params) + "\t" + L1 + "\t" + L2 + "\t" + (p1+p2));

//            for (int i = 1; i <= G1+1; i++) {
//                System.out.println(i + "\t" + PopSize[i]);
//          }

//        System.out.println("\n" + p1 + "\t" + p2);

        return p1 + p2;
    }
}
