# DoRIS

Optimizes the parameters of a demographic model based on identical-by-descent (IBD) segment sharing. See references for details.

NOTE: code is gradware. I hope to post a new version soon. Get in touch if you'd like to use a preliminary version.

### Usage

You can perform demographic inference by either minimizing the squared error of Equation 22 in 10.1016/j.ajhg.2012.08.030, or by maximizing the likelihood of observing a certain number of segments in the samples (Poisson with mean from Equation 14 in 10.1016/j.ajhg.2012.08.030).

One of the following is required

    --AverageSharing (or -A) -> file with observed sharing values (for 1 population models)
    --AverageSharingPop1 (or -AP1) --AverageSharingAcross (or -AAc) --AverageSharingPop2 (or -AP2) -> files with observed sharing values (for 2 population models)
    --SegmentCounts (or -S) -> file with observed counts of segments in each length range (for 1 population models)
    --SegmentCountsPop1 (or -SP1) --SegmentCountsAcross (or -AAc) --SegmentCountsPop2 (or -SP2) -> file with observed counts of segments in each length range (for 2 population models)

A demographic model and the space of parameter values to be explored in a grid are required (note: additional models can be implemented in the code). The models are described in the references.

    --DemographicModel (or -D) -> Expansion/DoubleExpansion/FounderExpansion/ExpansionFounderExpansion/SplitExpConstAsymMig/SplitExpConstSymMig
    --Grid (or -G) -> file with grid specification

Additional parameters (optional or required by one of the above parameters)

    --ChromosomeLength (or -C) -> length of the analyzed region (this option is required when using segment counts)
    --Pairs (or -P) -> pairs of chromosomes analyzed (e.g. (2*n choose 2) for n diploid samples; this option is required when using segment counts)
    --BinsPerCm (or -B) -> number of bins used per cM in calculations (default is 100)
    --Verbose (or -V) -> will print details

### Examples

one population, expansion, minimizing root mean square error using fraction of genome shared per length bin

    java -jar Doris.jar --DemographicModel Expansion --Grid EXAMPLES/grid.10K.40.130K.txt --AverageSharing EXAMPLES/Exp.10K.40.130K.sharingDist.txt 

two populations, split, expansion and migration, minimizing root mean square error using fraction of genome shared per length bin - decrease grid granularity to speed up

    java -jar Doris.jar --DemographicModel SplitExpConstAsymMig --Grid EXAMPLES/grid.TwoPop.55K.130K.0.0167.0.0334.txt --AverageSharingPop1 EXAMPLES/TwoPop.55K.130K.0.0167.0.0334.sharingDist.pop1.txt --AverageSharingPop2 EXAMPLES/TwoPop.55K.130K.0.0167.0.0334.sharingDist.pop2.txt --AverageSharingAcross EXAMPLES/TwoPop.55K.130K.0.0167.0.0334.sharingDist.across.txt

one population, contraction, maximum likelihood using segment counts per length bin

    java -jar Doris.jar --DemographicModel Expansion --Grid EXAMPLES/grid.10K.40.4K.txt --ChromosomeLength 276.289 --Pairs 499500 --SegmentCounts EXAMPLES/Exp.10K.40.4K.counts.txt

### File formats

Individuals are always assumed to be haploid. An inferred population size of 20,000 would correspond to a population of 10,000 diploid individuals. The same holds for the number of samples (also see the getHistograms.sh script on this).

The counts files have format

    FramLength	ToLength	NumberOfSegments
One bin per line.

The average sharing files have format

    FromLength	ToLength	AverageFractionOfGenomeShared
One bin per line.

To compute these histograms you can run the included script "getHistograms.sh", after modifying the parameters as indicated. Remember to change the "COL" field in the script to match the column where the length of each IBD segment is reported, if needed (e.g. this should be 11 for GERMLINE output http://www.cs.columbia.edu/~gusev/germline/). The default lenfth intervals of 1 cM may be changed.

The grid files have format

    ModelParameter  fromValue   interval    toValue
One parameters per line. The demographic models are depicted in the references. The models have the following parameters (also see error messages if any)

    Expansion   Current, Generation, Ancestral.
    DoubleExpansion   Current, Generation1, Ancestral1, Generation2, Ancestral2.
    FounderExpansion    Current, Generation, Ancestral1, Ancestral2.
    ExpansionFounderExpansion   Current, Generation, Ancestral1, Ancestral2, Generation2, Ancestral3.
    SplitExpConstAsymMig    pop1current, pop1ancestral, pop2current, pop2ancestral, generation, ancestraltot, m12, m21.
    SplitExpConstSymMig pop1current, pop1ancestral, pop2current, pop2ancestral, generation, ancestraltot, m.

### Dependencies

uses the Apache Math library (http://commons.apache.org/proper/commons-math/) to compute some probabilities.

### Contact

ppalama AT hsph DOT harvard DOTAGAIN edu

### References

this tool was developed for

- P. F. Palamara, T. Lencz, A. Darvasi, I. Pe'er. "Length distributions of identity by descent reveal fine-scale demographic history". The American Journal of Human Genetics, 2012.
- P. F. Palamara, I. Pe'er. "Inference of historical migration rates via haplotype sharing". Bioinformatics, 2013.
