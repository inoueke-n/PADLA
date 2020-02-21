# HeijoAgent

## Startup with HeijoAgent
* Add -javaaget option to include Java agent with a JVM argument. A sample is below:
```sh
java -javaagent:"HeijoAgent.jar=OptionFile=AgentOptions.xml"  -jar sampleApp.jar
```
`AgentOptions.xml` is a option file. You must specify its file path.

## Options
* PADLA needs some options. All options can be specified in the `AgentOptions.xml`. Sample `AgentOptions.xml` is bellow:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Options>
    <Option type="target" value="sampleApp.jar"></Option>
    <Option type="phaseOutput" value="vectors.txt"></Option>
    <Option type="learningData" value="vec.txt"></Option>
    <Option type="mode" value="Learning"></Option>
    <Option type="interval" value="1"></Option>
    <Option type="buffer" value="100000"></Option>
    <Option type="threshold" value="0.95"></Option>
    <Option type="agentWaitingTime" value="16000"></Option>
    <Option type="debugLogOutput" value="output.txt"></Option>
    <Option type="sampleInterval" value="10"></Option>
    <Option type="updateInterval" value="500"></Option>
    <Option type="bufferedInterval" value="2"></Option>
    <Option type="isDebug" value="1"></Option>
</Options>

```

### target
* It is file path of a jar file that includes class files of a target system. HeijoAgent monitors methods that are included in the jar file.
### phaseOutput
* It is file path of an output file of learning data. It is needed in the "Learning" mode.
### learningData
* It is file path of a learning data. HeijoAgent use the learning data in the "Adapter" mode to judge whether an interval is known or unknown. If the option is not specified, HeijoAgent starts without learning data. 
### mode
* It represents mode name. You can specify "Learning" or "Adapter" mode using this option.
### interval
* It is number of intervals which PADLA recognizes as one interval. However, basically you don't need to modify the value of this option.
### buffer
* It is the maximum number of log messages that can be kept internally.
### threshold
* It is the similality threshold of the phase detection. 
### agentWaitingTime
* It is the time which HeijoAgent waits for to connect the log4j-core-extended.
### debugLogOutput
* It is log file path of HeijoAgent. The log file contains the results of the phase detection.
### sampleInterval
* It is sampling interval of HeijoAgent. HeijoAgent checks the method stack in every time interval and calculates the method execution time. Its unit is millisecond.
### updateInterval
* It represents the length of one interval for the phase detection. HeijoAgent performs phase detection every interval. If the "interval" option set to 500, the length of one interval will be 0.5s.
### bufferedInterval
* It represents the number of intervals that PADLA keeps log messages internally.
### isDebug
* If the value of this option is "1", debug messages will be output.
 

## Build
* Build using eclipse.
* Use `res/MANIFEST.MF` as a manifest file.