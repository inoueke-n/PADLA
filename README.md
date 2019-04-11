# PADLA
## Overview
* PADLA is a tool that dynamically adjusts the log level of a running system. It is an extention of Apache Log4j.
* For detail, please refer to "PADLA: A Dynamic Log Level AdapterUsing Online Phase Detection" (paper.pdf).


## License

PADLA is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

## Folders
### projects
* It contains projects for development of the PADLA.
### release
* It contains jar files of PADLA and a sample software to try the tool.

## Getting started
### Running target system with "Learning" mode of PADLA
* For the "Learning" mode, follow the steps below
1. Replace log4j-core.jar of the target system with executableFiles\log4j-core-3.0.0-SNAPSHOT.jar.
2. Edit log4j2.xml to add an appender named "Learning". A sample is below:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE project>
<Configuration status="off">
	
    <Properties>
        <Property name="format1">%d{yyyy/MM/dd HH:mm:ss.SSS} [%t] %-6p %c{10} %m%n</Property>
    </Properties>
	<Appenders>
	    <File name="Learning" fileName="..\log.txt">
	      <PatternLayout pattern="%d{HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"/>
	    </File>
		<Console name="Console" target="SYSTEM_OUT">
	      <PatternLayout pattern="%d{HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"/>
	    </Console>
    </Appenders>

    <Loggers>
        <Root level="trace">
         <AppenderRef ref="Learning" level="info"/>
         <AppenderRef ref="Console" level="info"/>
        </Root>
    </Loggers>
</Configuration>
```
3. Add -javaagent option to include the Java agent with a JVM argument. A sample is below(sampleApp.jar is a your target program):
```bat
java -javaagent:"executableFiles\HeijoAgent\HeijoAgent.jar=target=target.jar,phaseOutput=vecters.txt,interval=5"  -jar sampleApp.jar
```
for details of these options, please refer to  PADLA\projects\HeijoAgent\README.md.

4. After you shutdown the target system, you will get the learning data in vecs.txt with above sample.

### Running target system with "Adopter" mode of PADLA
 * For the "Adopter" mode, follow the steps below
1. Replace log4j-core.jar of the target system with log4j-core-3.0.0-SNAPSHOT.jar.
2. Edit log4j2.xml to add an appender named "Adopter". A sample is below:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE project>
<Configuration status="off">
	
    <Properties>
        <Property name="format1">%d{yyyy/MM/dd HH:mm:ss.SSS} [%t] %-6p %c{10} %m%n</Property>
    </Properties>
	<Appenders>
	    <File name="Adopter" fileName="..\log.txt">
	      <PatternLayout pattern="%d{HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"/>
	    </File>
		<Console name="Console" target="SYSTEM_OUT">
	      <PatternLayout pattern="%d{HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"/>
	    </Console>
    </Appenders>

    <Loggers>
        <Root level="trace">
         <AppenderRef ref="Adopter" level="info"/>
         <AppenderRef ref="Console" level="info"/>
        </Root>
    </Loggers>
</Configuration>
```
3. Add -javaagent option to include the Java agent with a JVM argument. A sample is below(sampleApp.jar is a your target program):
```bat
java -javaagent:"executableFiles\HeijoAgentHeijoAgent.jar=target=target.jar,learningData=vectors.txt,bufferOutput=buffer.txt,buffer=300,interval=5"  -jar sampleApp.jar
```
for details of these options, please refer to  PADLA\projects\HeijoAgent\README.md. You can use vectors.txt that was generated the "Learning" mode as learningData.
4. If the target system performs an irregular behavior that is not exist in the learning data, PADLA will change the log level.
5. After you shutdown the target system, you will get log file(in ..\log.txt) and buffered logs in buffer.txt with above setting. 
