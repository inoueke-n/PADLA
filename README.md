# PADLA
## Overview
![overview](fig1.JPG)
* PADLA is a tool that dynamically adjusts the log level of a running system. It is an extention of Apache Log4j.
* For detail, please refer to "PADLA: A Dynamic Log Level Adapter Using Online Phase Detection" (paper.pdf).


## License

PADLA is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

## Folders
### executableFiles
* It contains jar files of PADLA.
### projects
* It contains projects for development of the PADLA(HeijoAgent and log4j-core-extended).
### release
* It contains sample software to try PADLA.

## Getting started
### Running target system with "Learning" mode of PADLA
* For the "Learning" mode, follow the steps below
1. Replace log4j-core.jar of the target system with executableFiles/log4j-core-3.0.0-SNAPSHOT.jar.
2. Edit log4j2.xml to add an appender named "Learning". A sample is below:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE project>
<Configuration status="off">
	
    <Properties>
        <Property name="format1">%d{yyyy/MM/dd HH:mm:ss.SSS} [%t] %-6p %c{10} %m%n</Property>
    </Properties>
	<Appenders>
	    <File name="Learning" fileName="log4j.log">
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
for details of these options, please refer to  PADLA/projects/HeijoAgent/README.md.

4. After you shutdown the target system, you will get the learning data in vectors.txt with above sample setting.

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

5. You can watch the log level changing on a console. When PADLA detects irregular behaviors and change the log level, "[PADLA]:Unknown Phase Detected!\n[PADLA]Logging Level Down\n↓↓↓↓↓↓↓↓" is displayed.

6. You can get a log file log4j.log. In the file, all level log messages are appeared at the time irregular behaviors occured. Also, you can get buffered log messages in buffer.txt. PADLA keeps log messages internally and output them when it change the log level. In the file, log messages between two "[output]" are outputed at onece. To know when the messages are outputed, please refer to timestamps of them.
