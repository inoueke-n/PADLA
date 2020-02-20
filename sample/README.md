# sample
## apache-tomcat-8.5.3
* It is a sample software to try PADLA.
* It contains jar files for running PADLA.
* <font color="Salmon">OS : Windows 10</font>
* <font color="Salmon">Use jdk1.8.0_191 or higher.</font>
* <font color="Salmon">If you have not set JRE_HOME yet, please set.</font>

## apache-jmeter-5.0
* It is a load-test tool to try PADLA
* To startup JMter, execute bin/ApacheJMeter.jar.
* You can use a sample access pattern. If you want to use, click "[File]->[open]" and select "[templates]->[Script_jpetstore_exam.jmx]".
* If you want to change "low<->high" of load, adjust "[Thread Group]->[Number of Threads]".

## Try PADLA on Tomcat under a load-test
### load-test outline
In this sample, you can try PADLA on Tomcat under a load-test. At first, startup Tomcat. When Tomcat is starting up, PADLA loads sample learning data(low load access pattern). After Tomcats starts, start a load-test with high load access pattern using JMeter. If PADLA detects irregular phases like high load access that does not exist in the learning data, it changes the log level. The sample Tomcat records log messages with "info" level at normal behavior. After the log level is changed, "debug" and "trace" level log messages are also recorded.

### load-test process
1. Execute `execute.bat`. It starts Tomcat and ApacheJMeter. If Tomcat starts successfully, a console will be activated. If Tomcat does not start, set JAVA_HOME in your environment.
2. In JMeter, you can use the sample access pattern. Click "[File]->[open]" and select "[templates]->[Script_jpetstore_exam.jmx]".
3. Set "[Thread Group]->[Number of Threads]" to 10000 to make irregular behavior of Tomcat by high load access. The sample learning data in `apache-tomcat-8.5.3\learningdata\vectors.txt` was collected with 100 threads as low load access.
4. Start a load-test by click "start"(green triangle). If JMeter hangs, update your jdk to jdk1.8.0_191 or higher.
5. After the load-test finished, shutdown Tomcat by executing `apache-tomcat-8.5.3\bin\shutdown.bat` and close the console window.
6. You can get a log file as `logs\log4j.log`. In the file, debug and trace level log messages are appeared at the time the load-test started. In addition, you can get buffered log messages in `logs\buffer.txt`. 
