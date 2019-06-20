# PADLA
## Overview
![overview](fig1.JPG)
* PADLA is a tool that dynamically adjusts the log level of a running system. It is an extension of Apache Log4j.
* For more detail, please refer to the paper ["PADLA: A Dynamic Log Level Adapter Using Online Phase Detection"](paper.pdf).


## License

PADLA is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

## Folders
### executableFiles
* It contains the jar files of PADLA.
* `executableFiles/HeijoAgent/HeijoAgent.jar` needs `javassist-3.22.0-GA.jar` and `msgpack-0.6.12.jar`, so put them in the same folder.
### projects
* It contains projects for the development of the PADLA (HeijoAgent and log4j-core-extended).
### sample
* It contains sample software to try PADLA. <font color="Salmon">Download this folder and try PADLA easily if you want</font>. For detailed instructions, please refer to [sample/README.md](sample/README.md).

## Demo movie
* A 2D action game with PADLA
* https://youtu.be/qoqilsbhD38

## Getting started in your project
* PADLA has two modes, "Learning" and "Adapter".
* In "Learning" mode, it records vectors of a target program as known phase.
* In "Adapter" mode, it adapts the log level of a target program according to its behavior.

### Running target system with "Learning" mode of PADLA
* For the "Learning" mode, follow the steps below
1. Replace log4j-core.jar of the target system with executableFiles/log4j-core-extended-2.11.0.jar.
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
3. Add -javaagent option to include the Java agent with a JVM argument. A sample is below (sampleApp.jar is a your target program):
```bat
java -javaagent:"executableFiles\HeijoAgent\HeijoAgent.jar=target=target.jar,phaseOutput=vecters.txt,interval=5"  -jar sampleApp.jar
```
for details of these options, please refer to  PADLA/projects/HeijoAgent/README.md.

4. After you shut down the target system, you will get the learning data in vectors.txt with above sample setting.

### Running target system with "Adapter" mode of PADLA
 * For the "Adapter" mode, follow the steps below
1. Replace log4j-core.jar of the target system with log4j-core-extended-2.11.0.jar.
2. Edit log4j2.xml to add an appender named "Adapter". A sample is below:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE project>
<Configuration status="off">
	
    <Properties>
        <Property name="format1">%d{yyyy/MM/dd HH:mm:ss.SSS} [%t] %-6p %c{10} %m%n</Property>
    </Properties>
	<Appenders>
	    <File name="Adapter" fileName="..\log.txt">
	      <PatternLayout pattern="%d{HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"/>
	    </File>
		<Console name="Console" target="SYSTEM_OUT">
	      <PatternLayout pattern="%d{HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"/>
	    </Console>
    </Appenders>

    <Loggers>
        <Root level="trace">
         <AppenderRef ref="Adapter" level="info"/>
         <AppenderRef ref="Console" level="info"/>
        </Root>
    </Loggers>
</Configuration>
```
3. Add -javaagent option to include the Java agent with a JVM argument. A sample is below (sampleApp.jar is a your target program):
```bat
java -javaagent:"executableFiles\HeijoAgentHeijoAgent.jar=target=target.jar,learningData=vectors.txt,bufferOutput=buffer.txt,buffer=300,interval=5"  -jar sampleApp.jar
```
for details of these options, please refer to  PADLA\projects\HeijoAgent\README.md. You can use vectors.txt that was generated the "Learning" mode as learningData.

4. If the target system performs an irregular behavior that does not exist in the learning data, PADLA will change the log level to emit more detailed logging.

5. You can watch the log level changing on a console. When PADLA detects irregular behavior and changes the log level, "[PADLA]:Unknown Phase Detected!\n[PADLA]Logging Level Down\n↓↓↓↓↓↓↓↓" is displayed.

6. You can get a log file `log4j.log`. In this file, all level log messages appear at the time irregular behavior occurred. Also, you can get buffered log messages in buffer.txt. PADLA keeps the latest N (defiled by "buffer" option) log messages internally and outputs them with "[OUTPUT]" at the beginning when it changes the log level. In this file, log messages between two "[output]" are outputted at once. To know when the messages are outputted, please refer to timestamps of them.
