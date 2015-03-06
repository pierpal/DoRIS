package doris;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class Main {

    static boolean doRMSE = false;
    static boolean doMaxLikelihood = false;
    static int numPop = 1;
    static boolean printProgress = false;
    static final boolean ignoreZeros = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        try {
            System.out.println();
            String modelName = null;
            String gridParams = null;
            String averageSharingFile = null;
            String averageSharingFilePop1 = null;
            String averageSharingFilePop2 = null;
            String averageSharingFileAcross = null;
            String segmentCountsFile = null;
            String segmentCountsFilePop1 = null;
            String segmentCountsFilePop2 = null;
            String segmentCountsFileAcross = null;
            String segAndSharingFile = null;
            double genomeSizePerPair = 0; // multiply by 4 if diploid
            int totalPairs = 0;
            Demography dem = null;
            Grid grid = null;
            ArrayList<Double[]> observedFraction = null;
            ArrayList<Double[]> observedFractionPop1 = null;
            ArrayList<Double[]> observedFractionPop2 = null;
            ArrayList<Double[]> observedFractionAcross = null;
            LikelihoodUtils like = null;
            ArrayList<Double[]> segCounts = null;
            ArrayList<Double[]> segCountsPop1 = null;
            ArrayList<Double[]> segCountsPop2 = null;
            ArrayList<Double[]> segCountsAcross = null;

            if (args.length == 0) {
                usage();
                System.exit(0);
            }

            int parseCount = 0;
            while (parseCount < args.length) {
                if (args[parseCount].compareToIgnoreCase("--DemographicModel") == 0
                        || args[parseCount].compareToIgnoreCase("-D") == 0) {
                    parseCount++;
                    modelName = args[parseCount];
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--Grid") == 0
                        || args[parseCount].compareToIgnoreCase("-G") == 0) {
                    parseCount++;
                    gridParams = args[parseCount];
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--AverageSharing") == 0
                        || args[parseCount].compareToIgnoreCase("-A") == 0) {
                    if (!doRMSE) {
                        doRMSE = true;
                        System.out.println("Will run RMSE optimization");
                    }
                    observedFraction = new ArrayList<Double[]>();
                    parseCount++;
                    averageSharingFile = args[parseCount];
                    System.out.print("Reading observed sharing from " + averageSharingFile + "... ");
                    observedFraction = readHistogram(averageSharingFile);
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--AverageSharingPop1") == 0
                        || args[parseCount].compareToIgnoreCase("-AP1") == 0) {
                    if (!doRMSE) {
                        doRMSE = true;
                        System.out.println("Will run RMSE optimization");
                    }
                    observedFractionPop1 = new ArrayList<Double[]>();
                    parseCount++;
                    averageSharingFilePop1 = args[parseCount];
                    System.out.print("Reading observed sharing for population 1 from " + averageSharingFilePop1 + "... ");
                    observedFractionPop1 = readHistogram(averageSharingFilePop1);
                    numPop = 2;
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--AverageSharingPop2") == 0
                        || args[parseCount].compareToIgnoreCase("-AP2") == 0) {
                    if (!doRMSE) {
                        doRMSE = true;
                        System.out.println("Will run RMSE optimization");
                    }
                    observedFractionPop2 = new ArrayList<Double[]>();
                    parseCount++;
                    averageSharingFilePop2 = args[parseCount];
                    System.out.print("Reading observed sharing for population 2 from " + averageSharingFilePop2 + "... ");
                    observedFractionPop2 = readHistogram(averageSharingFilePop2);
                    numPop = 2;
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--AverageSharingAcross") == 0
                        || args[parseCount].compareToIgnoreCase("-AAc") == 0) {
                    if (!doRMSE) {
                        doRMSE = true;
                        System.out.println("Will run RMSE optimization");
                    }
                    observedFractionAcross = new ArrayList<Double[]>();
                    parseCount++;
                    averageSharingFileAcross = args[parseCount];
                    System.out.print("Reading observed cross-population sharing from " + averageSharingFileAcross + "... ");
                    observedFractionAcross = readHistogram(averageSharingFileAcross);
                    numPop = 2;
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--SegmentCounts") == 0
                        || args[parseCount].compareToIgnoreCase("-S") == 0) {
                    if (!doMaxLikelihood) {
                        doMaxLikelihood = true;
                        System.out.println("Will run Maximum Likelihood optimization");
                    }
                    parseCount++;
                    segCounts = new ArrayList<Double[]>();
                    segmentCountsFile = args[parseCount];
                    System.out.print("Reading segment counts from " + segmentCountsFile + " (note: all counts must be integers) ... ");
                    segCounts = readHistogram(segmentCountsFile);
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--SegmentCountsPop1") == 0
                        || args[parseCount].compareToIgnoreCase("-SP1") == 0) {
                    if (!doMaxLikelihood) {
                        doMaxLikelihood = true;
                        System.out.println("Will run Maximum Likelihood optimization");
                    }
                    parseCount++;
                    segCountsPop1 = new ArrayList<Double[]>();
                    segmentCountsFilePop1 = args[parseCount];
                    System.out.print("Reading segment counts for population 1 from " + segmentCountsFilePop1 + " (note: all counts must be integers) ... ");
                    segCountsPop1 = readHistogram(segmentCountsFilePop1);
                    numPop = 2;
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--SegmentCountsPop2") == 0
                        || args[parseCount].compareToIgnoreCase("-SP2") == 0) {
                    if (!doMaxLikelihood) {
                        doMaxLikelihood = true;
                        System.out.println("Will run Maximum Likelihood optimization");
                    }
                    parseCount++;
                    segCountsPop2 = new ArrayList<Double[]>();
                    segmentCountsFilePop2 = args[parseCount];
                    System.out.print("Reading segment counts for population 2 from " + segmentCountsFilePop2 + " (note: all counts must be integers) ... ");
                    segCountsPop2 = readHistogram(segmentCountsFilePop2);
                    numPop = 2;
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--SegmentCountsAcross") == 0
                        || args[parseCount].compareToIgnoreCase("-SAc") == 0) {
                    if (!doMaxLikelihood) {
                        doMaxLikelihood = true;
                        System.out.println("Will run Maximum Likelihood optimization");
                    }
                    parseCount++;
                    segCountsAcross = new ArrayList<Double[]>();
                    segmentCountsFileAcross = args[parseCount];
                    System.out.print("Reading cross-population segment counts from " + segmentCountsFileAcross + " (note: all counts must be integers) ... ");
                    segCountsAcross = readHistogram(segmentCountsFileAcross);
                    numPop = 2;
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--ChromosomeLength") == 0
                        || args[parseCount].compareToIgnoreCase("-C") == 0) {
                    parseCount++;
                    genomeSizePerPair = Double.parseDouble(args[parseCount]);
                    System.out.println("analyzing a region of size: " + genomeSizePerPair);
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--Pairs") == 0
                        || args[parseCount].compareToIgnoreCase("-P") == 0) {
                    parseCount++;
                    totalPairs = Integer.parseInt(args[parseCount]);
                    System.out.println("data consists of " + totalPairs + " pairs.");
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--BinsPerCm") == 0
                        || args[parseCount].compareToIgnoreCase("-B") == 0) {
                    parseCount++;
                    Demography.numberOfSubsegmentsInCM = Double.parseDouble(args[parseCount]);
                    System.out.println("will use " + Demography.numberOfSubsegmentsInCM + " bins per cM in calculations.");
                    parseCount++;
                } else if (args[parseCount].compareToIgnoreCase("--Verbose") == 0
                        || args[parseCount].compareToIgnoreCase("-V") == 0) {
                    printProgress = true;
                    System.out.println("Will print details");
                    parseCount++;
                } else {
                    usage();
                    System.out.println("Unrecognized argument: " + args[parseCount]);
                    System.exit(0);
                }
            }

            if (!doMaxLikelihood && !doRMSE) {
                usage();
                System.out.println("Error: should specify --SegmentCounts or --AverageSharing (or equivalent 2-population options). See usage below:");
                System.exit(0);
            }

            if (modelName.equalsIgnoreCase("Expansion")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new Expansion();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new ExpansionGrid(gridParams);
            } else if (modelName.equalsIgnoreCase("DoubleExpansion")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new DoubleExpansion();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new DoubleExpansionGrid(gridParams);
            } else if (modelName.equalsIgnoreCase("FounderExpansion")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new FounderEventExpansion();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new FounderEventExpansionGrid(gridParams);
            } else if (modelName.equalsIgnoreCase("ExpansionFounderExpansion")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new ExpansionFounderEventExpansion();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new ExpansionFounderEventExpansionGrid(gridParams);
            } else if (modelName.equalsIgnoreCase("SplitExpConstAsymMig")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new SplitExpConstAsymMig();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new SplitExpConstAsymMigGrid(gridParams);
                numPop = 2;
            } else if (modelName.equalsIgnoreCase("SplitExpConstSymMig")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new SplitExpConstSymMig();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new SplitExpConstSymMigGrid(gridParams);
                numPop = 2;
            } else if (modelName.equalsIgnoreCase("SplitExpPulseAsymMig")) {
                System.out.println("Using demographic model: " + modelName);
                dem = new SplitExpPulseAsymMig();
                System.out.println("Parsing grid search parameters from: " + gridParams);
                grid = new SplitExpPulseAsymMigGrid(gridParams);
                numPop = 2;
            } else {
                throw new Exception("Unrecognized demographic model: " + modelName);
            }

            if (doRMSE) {
                if (((numPop == 1 && observedFraction == null) || (numPop == 2 && (observedFractionPop1 == null
                        || observedFractionPop2 == null || observedFractionAcross == null)) || grid == null || dem == null)) {
                    usage();
                    System.out.println("using --AverageSharing requires the following parameters:\n"
                            + "\t--DemographicModel\n"
                            + "\t--Grid\n"
                            + "\tIf a two population model is used make sure your have specified"
                            + "--AverageSharingPop1 --AverageSharingPop2 and --AverageSharingAcross");
                    System.exit(0);
                }
                if (numPop == 1) {
                    RMSEUtils.RMSE_onePop(grid, dem, observedFraction, genomeSizePerPair, printProgress);
                } else if (numPop == 2) {
                    RMSEUtils.RMSE_twoPops(grid, dem, observedFractionPop1, observedFractionAcross, observedFractionPop2, genomeSizePerPair, printProgress);
                }
            }

            if (doMaxLikelihood) {
                if (((numPop == 1 && segCounts == null) || (numPop == 2 && (segCountsPop1 == null
                        || segCountsPop2 == null || segCountsAcross == null)) || grid == null || dem == null || genomeSizePerPair == 0 || totalPairs == 0)) {
                    usage();
                    System.out.println("Error: using --SegmentCounts (or --SegmentCountsPop1 --SegmentCountsAcross --SegmentCountsPop2) requires the following parameters:\n"
                            + "\t--DemographicModel\n"
                            + "\t--Grid\n"
                            + "\t--Pairs\n"
                            + "\t--ChromosomeLength"
                            + "\tIf a two population model is used make sure your have specified"
                            + "--SegmentCountsPop1 --SegmentCountsPop2 and --SegmentCountsAcross");
                    System.exit(0);
                }
                if (numPop == 1) {
                    LikelihoodUtils.runMaximumLikelihood(segCounts, like, grid, dem, genomeSizePerPair, totalPairs, printProgress);
                } else if (numPop == 2) {
                    System.err.println("\nERROR: Maxlikelihood optimization for multiple populations will soon be implemented. Please contact pier@cs.columbia.edu for updates.\n");
                }
            }

        } catch (Exception e) {
            usage();
            System.out.println("Error: " + e.toString()
                    + "\nPlease report bugs/problems to pier@cs.columbia.edu");
            throw e;
        }
    }

    private static ArrayList<Double[]> readHistogram(String fileName) throws FileNotFoundException, IOException {
        ArrayList<Double[]> histogram = new ArrayList<Double[]>();
        FileReader fstream = new FileReader(fileName);
        BufferedReader in = new BufferedReader(fstream);
        String str;
        while (true) {
            str = in.readLine();
            if (str == null) {
                break;
            }
            String[] splitString = str.split("\\s+");
            Double[] tempD = new Double[3];
            tempD[0] = Double.parseDouble(splitString[0]);
            tempD[1] = Double.parseDouble(splitString[1]);
            tempD[2] = Double.parseDouble(splitString[2]);
            if (ignoreZeros && tempD[2] == 0) {
                System.out.println("Warning: ignoring " + tempD[0] + " " + tempD[1] + " " + tempD[2]);
            } else {
                histogram.add(tempD);
            }
        }
        System.out.println(histogram.size() + " read.");
        return histogram;
    }

    public static void usage() {
        System.out.println("\nDoRIS 0.1.20130318\n"
                + "\nThis software was developed by Pier Palamara in Itsik Pe'er's Lab of Computational Genetics (Columbia University), for the article \"Length distributions of identity by descent reveal fine-scale demographic history\" - Pier Francesco Palamara, Todd Lencz, Ariel Darvasi and Itsik Pe'er, AJHG 2012. This is a preliminary version, please check back in the near future for updated versions.\n"
                + "http://www.cs.columbia.edu/~pier/doris\n"
                + "\n"
                + "Command Line Arguments:\n\n"
                + "\t--AverageSharing (or -A) -> file with observed sharing values (for 1 population models)\n"
                + "\t--AverageSharingPop1 (or -AP1) --AverageSharingAcross (or -AAc) --AverageSharingPop2 (or -AP2) -> files with observed sharing values (for 2 population models)\n"
                + "\t--SegmentCounts (or -S) -> file with observed counts of segments in each length range (for 1 population models)\n"
                + "\t--SegmentCountsPop1 (or -SP1) --SegmentCountsAcross (or -AAc) --SegmentCountsPop2 (or -SP2) -> file with observed counts of segments in each length range (for 2 population models)\n"
                + "\t--DemographicModel (or -D) -> Expansion/DoubleExpansion/FounderExpansion/ExpansionFounderExpansion/SplitExpConstAsymMig/SplitExpConstSymMig\n"
                + "\t--Grid (or -G) -> file with grid specification\n"
                + "\t--ChromosomeLength (or -C) -> length of the analyzed region (this option is required when using segment counts)\n"
                + "\t--Pairs (or -P) -> pairs of chromosomes analyzed (e.g. 2*n choose 2, if ROHs, or IBD within individuals are considered; this option is required when using segment counts)\n"
                + "\t--BinsPerCm (or -B) -> number of bins used per cM in calculations (default is 100, can decrease for faster likelihood optimization, sacrificing a bit of accuracy)\n"
                + "\t--Verbose (or -V) -> will print details\n");
    }
}
