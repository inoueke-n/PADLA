# Release
## apache-tomcat-8.5.34
* It is a sample software to try PADLA.
* It contains jar files for running PADLA.
* OS : Windows 10

## apache-jmeter-5.0
* It is a load-test tool to try PADLA
* To startup JMter, execute bin\ApacheJMeter.jar.
* A sample access pattern is in "[File]->[open]->[templates]->[Script_jpetstore_exam.jmx]".
* If you want to change "low<->high" of load, adjust "[Thread Group]->[Number of Threads]".

### Running Tomcat with "Learning" mode of PADLA
* For the "Learning" mode, follow the steps below
1. Uncomment the following 2 lines of apache-tomcat-8.5.34/bin/setenv.bat.
```bat
rem set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,phaseOutput=..\outputs\vectors.txt,interval=5"
rem @SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_LearningMode.xml
↓
set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,phaseOutput=..\outputs\vectors.txt,interval=5"
@SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_LearningMode.xml
```
and comment out the following 2 lines.
```bat
set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,learningData=..\outputs\vectors.txt,bufferOutput=..\outputs\buffer.txt,buffer=300,interval=5"
@SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_AdopterMode.xml
```
2. Execute apache-tomcat-8.5.34/bin/startup.bat to startup Tomcat.
3. After Tomcat starts, start JMeter by execute apache-jmeter-5.0/bin/ApacheJMeter.jar
4. In JMeter, you can use sample access pattern. Open "[File]->[open]->[templates]->[Script_jpetstore_exam.jmx]".
5. Set "[Thread Group]->[Number of Threads]" to 100. You can change load of Tomcat by change the number. In this step, set the number to 100 to collect low load data as learning data.
6. Start a load-test by click "start"(green triangle).
7. After the load-test finished, shutdown Tomcat by execute apache-tomcat-8.5.34/bin/shutdown.bat.
8. You can get a learning data in apache-tomcat-8.5.34/outputs/vectors.txt


### Running Tomcat with "Adopter" mode of PADLA
* For the "Adopter" mode, follow the steps below
1. Uncomment the following 2 lines of apache-tomcat-8.5.34/bin/setenv.bat.
```bat
rem set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,learningData=..\outputs\vectors.txt,bufferOutput=..\outputs\buffer.txt,buffer=300,interval=5"
rem @SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_AdopterMode.xml
↓
set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,learningData=..\outputs\vectors.txt,bufferOutput=..\outputs\buffer.txt,buffer=300,interval=5"
@SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_AdopterMode.xml
```
and comment out the following 2 lines.
```bat
set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,phaseOutput=..\outputs\vectors.txt,interval=5"
@SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_LearningMode.xml
```
2. Execute apache-tomcat-8.5.34/bin/startup.bat to startup Tomcat.
3. After Tomcat starts, start JMeter by execute apache-jmeter-5.0/bin/ApacheJMeter.jar
4. In JMeter, you can use sample access pattern. Open "[File]->[open]->[templates]->[Script_jpetstore_exam.jmx]".
5. Set "[Thread Group]->[Number of Threads]" to 10000 to make irregular behavior of Tomcat by high load access.
6. Start a load-test by click "start"(green triangle).
7. You can watch the log level changing on a console. When PADLA detects irregular behaviors and change the log level, "[PADLA]:Unknown Phase Detected!\n[PADLA]Logging Level Down\n↓↓↓↓↓↓↓↓" is displayed.
7. After the load-test finished, shutdown Tomcat by execute apache-tomcat-8.5.34/bin/shutdown.bat.
8. You can get a log file as apache-tomcat-8.5.34/outputs/log4j.log. In the file, debug and trace level log messages are appeared at the time the load-test started. Also, you can get buffered log messages in apache-tomcat-8.5.34/outputs/buffer.txt. PADLA keeps log messages internally and output them when it change the log level. In the file, log messages between two "[output]" are outputed at onece. To know when the messages are outputed, please refer to timestamps of them.
