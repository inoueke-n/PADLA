# log4j-core-extended

## Modes
* log4j-core-extended has two modes, "Learning" and "Adopter".
* In "Learning" mode, log4j-core-extended records vectors of a target program.
* In "Adopter" mode, log4j-core-extended adopts the log level of a target program according to its behaviors.
* You can switch between the two modes by changing name of an appender in log4j2.xml. A sample is below("Learning" mode):
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE project>
<Configuration status="off">
	
    <Properties>
        <Property name="format1">%d{yyyy/MM/dd HH:mm:ss.SSS} [%t] %-6p %c{10} %m%n</Property>
    </Properties>
	<Appenders>
	    <File name="Learning" fileName=[File Path]>
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
* The appender name "Learning" represents the "Learning" mode, "Adopter" represents the "Adopter" mode.

## Options
* The following options are specified as arguments of the HeijoAgent.
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
* Execute the following:
```sh
mvn clean
mvn package -DskipTests
```
* If the build is successful, you will get target\log4j-core-3.0.0-SNAPSHOT.jar, then replace log4j-core.jar of a target program with it.