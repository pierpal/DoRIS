
package doris;

import doris.Grid;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Pier Palamara <pier@cs.columbia.edu>
 */
public class ExpansionGrid extends Grid {
    
    double currentFrom, currentInterval, currentTo;
    double generationFrom, generationInterval, generationTo;
    double ancestralFrom, ancestralInterval, ancestralTo;
    boolean doneCurrent = false, doneGeneration = false,
            doneAncestral = false;

    double state[] = new double[]{0,0,-1};

    public ExpansionGrid(String gridParams) throws Exception {
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
            else if (splitString[0].equalsIgnoreCase("ancestral")) {
                ancestralFrom = Double.parseDouble(splitString[1]);
                ancestralInterval = Double.parseDouble(splitString[2]);
                ancestralTo = Double.parseDouble(splitString[3]);
                doneAncestral = true;
            }
            else {
                throw new Exception("Problem parsing grid file " + gridParams + " line starting with " + splitString[0] +
                        ". Please specify Current, Generation, Ancestral.");
            }
        }
        if (!doneCurrent) {
            throw new Exception("Current was not specified in " + gridParams);
        }
        if (!doneGeneration) {
            throw new Exception("Generation was not specified in " + gridParams);
        }
        if (!doneAncestral) {
            throw new Exception("Ancestral was not specified in " + gridParams);
        }
    }

    void resetIterator () {
        state[0] = 0;
        state[1] = 0;
        state[2] = -1;
    }

    boolean hasNext() {
        return !(state[0]==Math.floor((currentTo-currentFrom)/currentInterval)
                && state[1]==Math.floor((generationTo-generationFrom)/generationInterval)
                && state[2]==Math.floor((ancestralTo-ancestralFrom)/ancestralInterval));
    }

    double[] nextSet() {
            if (state[2]==Math.floor((ancestralTo-ancestralFrom)/ancestralInterval)) {
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
        double ret[] = new double[3];
        ret[0] = currentFrom+state[0]*currentInterval;
        ret[1] = generationFrom+state[1]*generationInterval;
        ret[2] = ancestralFrom+state[2]*ancestralInterval;
        return ret;
    }

}
