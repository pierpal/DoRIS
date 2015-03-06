package doris;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class FounderEventExpansionGrid extends Grid {

    double currentFrom, currentInterval, currentTo;
    double generationFrom, generationInterval, generationTo;
    double ancestral1From, ancestral1Interval, ancestral1To;
    double ancestral2From, ancestral2Interval, ancestral2To;
    boolean doneCurrent = false, doneGeneration = false,
            doneAncestral1 = false, doneAncestral2 = false;

    double state[] = new double[]{0,0,0,-1};

    public FounderEventExpansionGrid(String gridParams) throws Exception {
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
            }
            else if (splitString[0].equalsIgnoreCase("generation")) {
                generationFrom = Double.parseDouble(splitString[1]);
                generationInterval = Double.parseDouble(splitString[2]);
                generationTo = Double.parseDouble(splitString[3]);
                if (generationFrom%1!=0 || generationInterval%1!=0 || generationTo%1!=0) {
                    throw new Exception("Generations are required to be integer numbers only");
                }
                doneGeneration = true;
            }
            else if (splitString[0].equalsIgnoreCase("ancestral1")) {
                ancestral1From = Double.parseDouble(splitString[1]);
                ancestral1Interval = Double.parseDouble(splitString[2]);
                ancestral1To = Double.parseDouble(splitString[3]);
                doneAncestral1 = true;
            }
            else if (splitString[0].equalsIgnoreCase("ancestral2")) {
                ancestral2From = Double.parseDouble(splitString[1]);
                ancestral2Interval = Double.parseDouble(splitString[2]);
                ancestral2To = Double.parseDouble(splitString[3]);
                doneAncestral2 = true;
            }
            else {
                throw new Exception("Problem parsing grid file " + gridParams + " line starting with " + splitString[0] +
                        ". Please specify Current, Generation, Ancestral1, Ancestral2.");
            }
        }
        if (!doneCurrent) {
            throw new Exception("Current was not specified in " + gridParams);
        }
        if (!doneGeneration) {
            throw new Exception("Generation was not specified in " + gridParams);
        }
        if (!doneAncestral1) {
            throw new Exception("Ancestral1 was not specified in " + gridParams);
        }
        if (!doneAncestral2) {
            throw new Exception("Ancestral2 was not specified in " + gridParams);
        }
    }

    void resetIterator () {
        state[0] = 0;
        state[1] = 0;
        state[2] = 0;
        state[3] = -1;
    }

    boolean hasNext() {
        return !(state[0]==Math.floor((currentTo-currentFrom)/currentInterval)
                && state[1]==Math.floor((generationTo-generationFrom)/generationInterval)
                && state[2]==Math.floor((ancestral1To-ancestral1From)/ancestral1Interval)
                && state[3]==Math.floor((ancestral2To-ancestral2From)/ancestral2Interval));
    }

    double[] nextSet() {
        if (state[3]==Math.floor((ancestral2To-ancestral2From)/ancestral2Interval)) {
            state[3]=0;
            if (state[2]==Math.floor((ancestral1To-ancestral1From)/ancestral1Interval)) {
                state[2]=0;
                if (state[1]==Math.floor((generationTo-generationFrom)/generationInterval)) {
                    state[1]=0;
                    if (state[0]==Math.floor((currentTo-currentFrom)/currentInterval)) {
                        state[0]=-1;
                        return null;
                    }
                    else {
                        state[0]++;
                    }
                }
                else {
                    state[1]++;
                }
            }
            else {
                state[2]++;
            }
        }
        else {
            state[3]++;
        }
        double ret[] = new double[4];
        ret[0] = currentFrom+state[0]*currentInterval;
        ret[1] = generationFrom+state[1]*generationInterval;
        ret[2] = ancestral1From+state[2]*ancestral1Interval;
        ret[3] = ancestral2From+state[3]*ancestral2Interval;
//        System.out.println(state[0] + " " + state[1] + " " + state[2] + " " + state[3]);
//        System.out.println(ret[0] + " " + ret[1] + " " + ret[2] + " " + ret[3]);
        return ret;
    }

}
