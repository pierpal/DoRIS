package doris;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class DoubleExpansion extends Demography {

    String ParamsToString(double[] params) {
        return ("Current: " + params[0] + "\tGenerations1: " + params[1] + "\tAncestral1: " + params[2] +
                 "\tGenerations2: " + params[3] + "\t(GenerationsTot: " + (params[1]+params[3]) + ")\tAncestral2: " + params[4]);
    }

    double expectedFraction(double[] params, double L1, double L2) {

        double C = params[0];
        double G1 = params[1];
        double A1 = params[2];
        double G2 = params[3];
        double A2 = params[4];
        double Gtot = G1 + G2;

        double PopSize[] = new double[(int) Gtot + 2];
        double CP[] = new double[(int) Gtot + 2];
        double NC[] = new double[(int) Gtot + 2];

//        System.out.println(this.ParamsToString(params));

        for (int i = 1; i <= G1; i++) {
            PopSize[i] = C * Math.pow((A1 / C), (i / G1));
//                System.out.println(i + "\t" + PopSize[i]);
            CP[i] = 1 / PopSize[i];
            NC[i] = 1 - CP[i];
        }
        for (int i = (int) G1 + 1; i <= Gtot; i++) {
            PopSize[i] = A1 * Math.pow((A2 / A1), ((i - G1) / G2));
//                System.out.println(i + "\t" + PopSize[i]);
            CP[i] = 1 / PopSize[i];
            NC[i] = 1 - CP[i];
        }

        PopSize[(int) Gtot + 1] = A2;
        CP[(int) Gtot + 1] = 1 / (float) A2;
        NC[(int) Gtot + 1] = 1 - CP[(int) Gtot + 1];

        // compute chance of not coalescence
        double NCG[] = new double[(int) Gtot + 2];
        NCG[1] = 1;
        for (int i = 2; i <= Gtot + 1; i++) {
            NCG[i] = NCG[i - 1] * NC[i];
        }

        // compute prability up to Gtot
        double p1 = 0.0;
        for (int k = 1; k <= Gtot; k++) {
            double eL = 1. / 50. * (Math.exp(-k * L1 / 50.) * (50. + k * L1) - Math.exp(-k * L2 / 50.) * (50. + k * L2));
            p1 += NCG[k] * CP[k] * eL;
        }

        // compute prability from Gtot to infinity
        double NC_G = NCG[(int) Gtot];
        double cnst = Math.log(1 - 1 / A2);
        double p2 = NC_G / A2 * Math.exp(-cnst * (1 + Gtot)) * ((Math.exp((1 + Gtot) * (cnst - L1 / 50)) * (-50 * cnst * (50 + L1 + Gtot * L1) + L1 * (100 + L1 + Gtot * L1))) / Math.pow((-50 * cnst + L1), 2) + (Math.exp((1 + Gtot) * (cnst - L2 / 50)) * (50 * cnst * (50 + L2 + Gtot * L2) - L2 * (100 + L2 + Gtot * L2))) / Math.pow((-50 * cnst + L2), 2));

//        System.out.println(this.ParamsToString(params) + "\t" + L1 + "\t" + L2 + "\t" + (p1+p2));

//        for (int i = 1; i <= G1+1; i++) {
//            System.out.println(i + "\t" + PopSize[i]);
//        }

        return p1 + p2;

    }
}
