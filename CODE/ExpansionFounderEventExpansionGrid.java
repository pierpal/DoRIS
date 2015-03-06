package doris;

import doris.Grid;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class ExpansionFounderEventExpansionGrid extends Grid {

    double currentFrom, currentInterval, currentTo;
    double generation1From, generation1Interval, generation1To;
    double ancestral1From, ancestral1Interval, ancestral1To;
    double ancestral2From, ancestral2Interval, ancestral2To;
    double generation2From, generation2Interval, generation2To;
    double ancestral3From, ancestral3Interval, ancestral3To;
    boolean doneCurrent = false, doneGeneration1 = false,
            doneAncestral1 = false, doneAncestral2 = false,
            doneGeneration2 = false, doneAncestral3 = false;
    double state[] = new double[]{0, 0, 0, 0, 0, -1};

    public ExpansionFounderEventExpansionGrid(String gridParams) throws Exception {
        FileReader fstream = new FileReader(gridParams);
        BufferedReader in = new BufferedReader(fstream);
        String str;
        int counter = 0;
        while (true) {
            str = in.readLine();
            if (str == null) {
                break;
            }
            String[] splitString = str.split("\\s+");
            if (splitString[0].equalsIgnoreCase("current")) {
                currentFrom = Double.parseDouble(splitString[1]);
                currentInterval = Double.parseDouble(splitString[2]);
                currentTo = Double.parseDouble(splitString[3]);
                doneCurrent = true;
            } else if (splitString[0].equalsIgnoreCase("generation1")) {
                generation1From = Double.parseDouble(splitString[1]);
                generation1Interval = Double.parseDouble(splitString[2]);
                generation1To = Double.parseDouble(splitString[3]);
                if (generation1From % 1 != 0 || generation1Interval % 1 != 0 || generation1To % 1 != 0) {
                    throw new Exception("Generations are required to be integer numbers only");
                }
                doneGeneration1 = true;
            } else if (splitString[0].equalsIgnoreCase("ancestral1")) {
                ancestral1From = Double.parseDouble(splitString[1]);
                ancestral1Interval = Double.parseDouble(splitString[2]);
                ancestral1To = Double.parseDouble(splitString[3]);
                doneAncestral1 = true;
            } else if (splitString[0].equalsIgnoreCase("ancestral2")) {
                ancestral2From = Double.parseDouble(splitString[1]);
                ancestral2Interval = Double.parseDouble(splitString[2]);
                ancestral2To = Double.parseDouble(splitString[3]);
                doneAncestral2 = true;
            } else if (splitString[0].equalsIgnoreCase("generation2")) {
                generation2From = Double.parseDouble(splitString[1]);
                generation2Interval = Double.parseDouble(splitString[2]);
                generation2To = Double.parseDouble(splitString[3]);
                if (generation2From % 1 != 0 || generation2Interval % 1 != 0 || generation2To % 1 != 0) {
                    throw new Exception("Generations are required to be integer numbers only");
                }
                doneGeneration2 = true;
            } else if (splitString[0].equalsIgnoreCase("ancestral3")) {
                ancestral3From = Double.parseDouble(splitString[1]);
                ancestral3Interval = Double.parseDouble(splitString[2]);
                ancestral3To = Double.parseDouble(splitString[3]);
                doneAncestral3 = true;
            } else {
                throw new Exception("Problem parsing grid file " + gridParams + " line starting with " + splitString[0]
                        + ". Please specify Current, Generation, Ancestral1, Ancestral2, Generation2, Ancestral3.");
            }
        }
        if (!doneCurrent) {
            throw new Exception("Current was not specified in " + gridParams);
        }
        if (!doneGeneration1) {
            throw new Exception("Generation1 was not specified in " + gridParams);
        }
        if (!doneAncestral1) {
            throw new Exception("Ancestral1 was not specified in " + gridParams);
        }
        if (!doneAncestral2) {
            throw new Exception("Ancestral2 was not specified in " + gridParams);
        }
        if (!doneGeneration2) {
            throw new Exception("Generation2 was not specified in " + gridParams);
        }
        if (!doneAncestral3) {
            throw new Exception("Ancestral3 was not specified in " + gridParams);
        }
    }

    void resetIterator() {
        state[0] = 0;
        state[1] = 0;
        state[2] = 0;
        state[3] = 0;
        state[4] = 0;
        state[5] = -1;
    }

    boolean hasNext() {
        return !(state[0] == Math.floor((currentTo - currentFrom) / currentInterval)
                && state[1] == Math.floor((generation1To - generation1From) / generation1Interval)
                && state[2] == Math.floor((ancestral1To - ancestral1From) / ancestral1Interval)
                && state[3] == Math.floor((ancestral2To - ancestral2From) / ancestral2Interval)
                && state[4] == Math.floor((generation2To - generation2From) / generation2Interval)
                && state[5] == Math.floor((ancestral3To - ancestral3From) / ancestral3Interval));
    }

    double[] nextSet() {
        if (state[5] == Math.floor((ancestral3To - ancestral3From) / ancestral3Interval)) {
            state[5] = 0;
            if (state[4] == Math.floor((generation2To - generation2From) / generation2Interval)) {
                state[4] = 0;
                if (state[3] == Math.floor((ancestral2To - ancestral2From) / ancestral2Interval)) {
                    state[3] = 0;
                    if (state[2] == Math.floor((ancestral1To - ancestral1From) / ancestral1Interval)) {
                        state[2] = 0;
                        if (state[1] == Math.floor((generation1To - generation1From) / generation1Interval)) {
                            state[1] = 0;
                            if (state[0] == Math.floor((currentTo - currentFrom) / currentInterval)) {
                                state[0] = -1;
                                return null;
                            } else {
                                state[0]++;
                            }
                        } else {
                            state[1]++;
                        }
                    } else {
                        state[2]++;
                    }
                } else {
                    state[3]++;
                }
            } else {
                state[4]++;
            }
        } else {
            state[5]++;
        }
        double ret[] = new double[6];
        ret[0] = currentFrom + state[0] * currentInterval;
        ret[1] = generation1From + state[1] * generation1Interval;
        ret[2] = ancestral1From + state[2] * ancestral1Interval;
        ret[3] = ancestral2From + state[3] * ancestral2Interval;
        ret[4] = generation2From + state[4] * generation2Interval;
        ret[5] = ancestral3From + state[5] * ancestral3Interval;
        return ret;
    }
}
