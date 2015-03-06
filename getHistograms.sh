#!/bin/sh

# the suffix of the file containing the IBD segments (will be used for output too).
FILE=EXAMPLES/Exp.10K.40.4K

# change this to the column reporting the length of the IBD segments (in cM) for the IBD file being processed (e.g. this should be 11 for GETMLINE output http://www.cs.columbia.edu/~gusev/germline/, it is 3 in the included example file).
COL=3

# change the value for the number of pairs to match the number of haploid chromosomes for which IBD was detected in the sample. For example, if you have 500 diploid individuals and you are also considering IBD sharing between maternal/paternal chromosomes of each individual (i.e. runs of homozygosity), you have 1,000*999/2=499,500 pairs. If IBD within individuals is not considered you have 1,000*999/2 - 500 = 499,000 pairs of compared chromosomes.
pairs=499500

# change the totalChromosomesLength value to the total genetic length of the regions for which IBD was computed (e.g. approximately 3,500 cM for the autosomes, or 278 cM for chromosome 1).
totalChromosomesLength=278.09

# the histogram for the average fraction of genome shared is computed here and saved to $FILE.sharingDist.txt (IBD segment length in column 11 for GERMLINE format, column 3 in this example file).
cat $FILE.match | awk -v COL=$COL -v len=$totalChromosomesLength -v pairs=$pairs '{ l=sprintf("%d",$COL); total[l]+=$COL; } END{ for (l=1;l<=15;l++) print l "\t" (l+1) "\t" 0+total[l]/(pairs*len); }' > $FILE.sharingDist.txt

# the histogram for the total number of IBD segments is computed here and saved to $FILE.counts.txt (IBD segment length in column 11 for GERMLINE format, column 3 in this example file).
cat $FILE.match | awk -v COL=$COL '{ l=sprintf("%d",$COL); total[l]++; } END{ for (l=1;l<=15;l++) print l "\t" (l+1) "\t" 0+total[l]; }' > $FILE.counts.txt
