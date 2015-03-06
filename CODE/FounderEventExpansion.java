
package doris;

import doris.Demography;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class FounderEventExpansion extends Demography {

    String ParamsToString(double[] params) {
        return("Current: " + params[0] + "\tGenerations: " + params[1] + "\tAncestral1: " + params[2] + "\tAncestral2: " + params[3]);
    }

    double expectedFraction(double[] params, double L1, double L2) {

        double C = params[0];
        double G1 = params[1];
        double A = params[2];
        double pst = params[3];

        double PopSize[] = new double [(int)params[1]+2];
        double CP[] = new double [(int)params[1]+2];
        double NC[] = new double [(int)params[1]+2];

	for (int i=1; i<=G1; i++) {
		PopSize[i] = C*Math.pow((A/C),(i/G1));
		CP[i] = 1/PopSize[i];
		NC[i] = 1-CP[i];
	}

	PopSize[(int)G1+1] = pst;
	CP[(int)G1+1] = 1/(float)pst;
	NC[(int)G1+1] = 1-CP[(int)G1+1];

	// compute chance of not coalescence
	double NCG[] = new double[(int)G1+2];
	NCG[1] = 1;
	for (int i=2; i<=G1+1; i++) {
		NCG[i] = NCG[i-1]*NC[i];
	}

	// compute prability up to G1
	double p1=0.0;
	for (int k=1; k<=G1; k++) {
		double eL = 1./50. * (Math.exp(-k*L1/50.) * (50.+k*L1)-Math.exp(-k*L2/50.) * (50.+k * L2));
		p1 += NCG[k]*CP[k]*eL;
	}

	// compute prability from G1 to infinity
	double NC_G = NCG[(int)G1];
	A = pst;
	double cnst = Math.log(1-1/A);
	double p2 = NC_G/A*Math.exp(-cnst*(1+G1))*((Math.exp((1+G1)*(cnst-L1/50))*(-50*cnst*(50+L1+G1*L1)+L1*(100+L1+G1*L1)))/Math.pow((-50*cnst+L1),2)+(Math.exp((1+G1)*(cnst-L2/50))*(50*cnst*(50+L2+G1*L2)-L2*(100+L2+G1*L2)))/Math.pow((-50*cnst+L2),2));

//        for (int i = 1; i <= G1+1; i++) {
//            System.out.println(i + "\t" + PopSize[i]);
//        }

        return p1+p2;

    }

}
