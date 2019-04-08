# HeijoAgent

## Startup with HeijoAgent
* Add -javaaget option to include Java agent with a JVM argument. A sample is below:
```sh
java -javaagent:"HeijoAgent.jar" -jar sampleApp.jar
```
## Options
* PADLA need some options. These are specified as arguments of HeijoAgent like below:
```sh
-javaagent"HeijoAgent.jar=target=target.jar,learningData=vectors.txt,bufferOutput=buffer.txt,phaseOutput,=vecs.txt,buffer=300,interval=5"
```
### target
* It is file path of a jar file that includes class files of a target system. HeijoAgent monitors methods that are included in the jar file.
### learningData
* It is file path of a learning data. log4j-core-extended use the learning data in the "Adopter" mode to judge whether a interval is known or unknown. If the option is not specified, log4j-core-extended starts without learning data. 
### bufferOutput
* It is file path of a output file of buffered logs. In the "Adopter" mode, log4j-core-extended internally buffers recent N(it is defined by the "buffer" option) log messages and output it when the log level changed.
### phaseOutput
* It is file path of a output file of learning data. It is need in the "Learning" mode.
### buffer
* It is the maximum number of log messages that can be kept internally. 
### interval
* It represents the length of one interval. log4j-core-extended performs phase detection every interval. If the "interval" option set to 1, the length of one interval will be 0.1s.

## Build
* Build using eclipse.
* Use res\MANIFEST.MF as a manifest file.