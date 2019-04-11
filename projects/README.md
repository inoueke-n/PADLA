# projects

## HeijoAgent
* A performance reporter for PADLA.
* It reports the execution time for each method in a fixed interval. 


## log4j-core-extended
* A main componet of PADLA. It is an extention of the log4j-core.
* It has two components: Phase Detection and Logging.
* Phase Detection component judges whether a phase of current interval is known or unknown using performance reports from the HeijoAgent.
* Logging component changes the log level according to the result of the Phase Detection.
