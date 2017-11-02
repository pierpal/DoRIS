package doris;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class SplitExpConstSymMigGrid extends Grid {

    double pop1CurrentFrom, pop1CurrentInterval, pop1CurrentTo;
    double pop1AncestralFrom, pop1AncestralInterval, pop1AncestralTo;
    double pop2CurrentFrom, pop2CurrentInterval, pop2CurrentTo;
    double pop2AncestralFrom, pop2AncestralInterval, pop2AncestralTo;
    double generationFrom, generationInterval, generationTo;
    double ancestralTotFrom, ancestralTotInterval, ancestralTotTo;
    double mFrom, mInterval, mTo;
    boolean donePop1Current = false, donePop2Current = false,
            donePop1Ancestral = false, donePop2Ancestral = false,
            doneAncestralTot = false, doneGeneration = false,
            doneM = false;
    double state[] = new double[]{0, 0, 0, 0, 0, 0, -1};

    public SplitExpConstSymMigGrid(String gridParams) throws Exception {
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
            if (splitString[0].equalsIgnoreCase("pop1current")) {
                pop1CurrentFrom = Double.parseDouble(splitString[1]);
                pop1CurrentInterval = Double.parseDouble(splitString[2]);
                pop1CurrentTo = Double.parseDouble(splitString[3]);
                donePop1Current = true;
            } else if (splitString[0].equalsIgnoreCase("pop1ancestral")) {
                pop1AncestralFrom = Double.parseDouble(splitString[1]);
                pop1AncestralInterval = Double.parseDouble(splitString[2]);
                pop1AncestralTo = Double.parseDouble(splitString[3]);
                donePop1Ancestral = true;
            } else if (splitString[0].equalsIgnoreCase("pop2current")) {
                pop2CurrentFrom = Double.parseDouble(splitString[1]);
                pop2CurrentInterval = Double.parseDouble(splitString[2]);
                pop2CurrentTo = Double.parseDouble(splitString[3]);
                donePop2Current = true;
            } else if (splitString[0].equalsIgnoreCase("pop2ancestral")) {
                pop2AncestralFrom = Double.parseDouble(splitString[1]);
                pop2AncestralInterval = Double.parseDouble(splitString[2]);
                pop2AncestralTo = Double.parseDouble(splitString[3]);
                donePop2Ancestral = true;
            } else if (splitString[0].equalsIgnoreCase("ancestraltot")) {
                ancestralTotFrom = Double.parseDouble(splitString[1]);
                ancestralTotInterval = Double.parseDouble(splitString[2]);
                ancestralTotTo = Double.parseDouble(splitString[3]);
                doneAncestralTot = true;
            } else if (splitString[0].equalsIgnoreCase("generation")) {
                generationFrom = Double.parseDouble(splitString[1]);
                generationInterval = Double.parseDouble(splitString[2]);
                generationTo = Double.parseDouble(splitString[3]);
                if (generationFrom % 1 != 0 || generationInterval % 1 != 0 || generationTo % 1 != 0) {
                    throw new Exception("Generations are required to be integer numbers only");
                }
                doneGeneration = true;
            } else if (splitString[0].equalsIgnoreCase("m")) {
                mFrom = Double.parseDouble(splitString[1]);
                mInterval = Double.parseDouble(splitString[2]);
                mTo = Double.parseDouble(splitString[3]);
                doneM = true;
            } else {
                throw new Exception("Problem parsing grid file " + gridParams + " line starting with " + splitString[0]
                        + ". Please specify pop1current, pop1ancestral, pop2current, pop2ancestral, generation, ancestraltot, m.");
            }
        }
        if (!donePop1Current) {
            throw new Exception("pop1current was not specified in " + gridParams);
        }
        if (!donePop2Current) {
            throw new Exception("pop2current was not specified in " + gridParams);
        }
        if (!donePop1Ancestral) {
            throw new Exception("pop1ancestral was not specified in " + gridParams);
        }
        if (!donePop2Ancestral) {
            throw new Exception("pop2ancestral was not specified in " + gridParams);
        }
        if (!doneAncestralTot) {
            throw new Exception("ancestraltot was not specified in " + gridParams);
        }
        if (!doneGeneration) {
            throw new Exception("generation was not specified in " + gridParams);
        }
        if (!doneM) {
            throw new Exception("m was not specified in " + gridParams);
        }
    }

    void resetIterator() {
        state[0] = 0;
        state[1] = 0;
        state[2] = 0;
        state[3] = 0;
        state[4] = 0;
        state[5] = 0;
        state[6] = -1;
    }

    boolean hasNext() {
        return !(state[0] == Math.floor((pop1CurrentTo - pop1CurrentFrom) / pop1CurrentInterval)
                && state[1] == Math.floor((pop1AncestralTo - pop1AncestralFrom) / pop1AncestralInterval)
                && state[2] == Math.floor((pop2CurrentTo - pop2CurrentFrom) / pop2CurrentInterval)
                && state[3] == Math.floor((pop2AncestralTo - pop2AncestralFrom) / pop2AncestralInterval)
                && state[4] == Math.floor((ancestralTotTo - ancestralTotFrom) / ancestralTotInterval)
                && state[5] == Math.floor((generationTo - generationFrom) / generationInterval)
                && state[6] == Math.floor((mTo - mFrom) / mInterval));
    }

    double[] nextSet() {
        if (state[6] == Math.floor((mTo - mFrom) / mInterval)) {
            state[6] = 0;
            if (state[5] == Math.floor((generationTo - generationFrom) / generationInterval)) {
                state[5] = 0;
                if (state[4] == Math.floor((ancestralTotTo - ancestralTotFrom) / ancestralTotInterval)) {
                    state[4] = 0;
                    if (state[3] == Math.floor((pop2AncestralTo - pop2AncestralFrom) / pop2AncestralInterval)) {
                        state[3] = 0;
                        if (state[2] == Math.floor((pop2CurrentTo - pop2CurrentFrom) / pop2CurrentInterval)) {
                            state[2] = 0;
                            if (state[1] == Math.floor((pop1AncestralTo - pop1AncestralFrom) / pop1AncestralInterval)) {
                                state[1] = 0;
                                if (state[0] == Math.floor((pop1CurrentTo - pop1CurrentFrom) / pop1CurrentInterval)) {
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
        } else {
            state[6]++;
        }
        double ret[] = new double[8];
        ret[0] = pop1CurrentFrom + state[0] * pop1CurrentInterval;
        ret[1] = pop1AncestralFrom + state[1] * pop1AncestralInterval;
        ret[2] = pop2CurrentFrom + state[2] * pop2CurrentInterval;
        ret[3] = pop2AncestralFrom + state[3] * pop2AncestralInterval;
        ret[4] = ancestralTotFrom + state[4] * ancestralTotInterval;
        ret[5] = generationFrom + state[5] * generationInterval;
        ret[6] = mFrom + state[6] * mInterval;
        ret[7] = 0; // arbitrary
        return ret;
    }
}
