# Release
## apache-tomcat-8.5.34
* It is a sample software to try PADLA.
* It contains jar files for running PADLA.
* OS : Windows 10

## apache-jmeter-5.0
* It is a load-test tool to try PADLA
* To startup JMter, execute bin\ApacheJMeter.jar.
* You can use a sample access pattern. If you want to use, click "[File]->[open]" and select "[templates]->[Script_jpetstore_exam.jmx]".
* If you want to change "low<->high" of load, adjust "[Thread Group]->[Number of Threads]".

## Try PADLA on Tomcat under a load-test
### load-test outline
In this sample, you can try PADLA on Tomcat under a load-test. At first, startup Tomcat. When Tomcat is starting up, PADLA loads sample learning data(low load access pattern). After Tomcats starts, start a load-test with high load access pattern using JMeter. If PADLA detects irregular phases like high load access that does not exist in the learning data, it changes the log level. The sample Tomcat records log messages with "info" level at normal behavior. After the log level is changed, "debug" and "trace" level log messages are also recorded.

### load-test process
1. Execute apache-tomcat-8.5.34/bin/startup.bat to startup Tomcat.
2. After Tomcat starts, start JMeter by executing apache-jmeter-5.0/bin/ApacheJMeter.jar
3. In JMeter, you can use the sample access pattern. Click "[File]->[open]" and select "[templates]->[Script_jpetstore_exam.jmx]".
4. Set "[Thread Group]->[Number of Threads]" to 10000 to make irregular behavior of Tomcat by high load access. The sample learning data in apache-tomcat-8.5.34/outputs/vectors.txt was collected with 100 threads as low load access.
5. Start a load-test by click "start"(green triangle).
6. You can watch the log level changing on a console. When PADLA detects irregular behavior and changes the log level, the following statements are displayed in the console.
```sh
[LOG4JCORE-EXTENDED]:Unknown Phase Detected!
[LOG4JCORE-EXTENDED]:Logging Level Down
↓↓↓↓↓↓↓↓
```

7. After the load-test finished, shutdown Tomcat by executing apache-tomcat-8.5.34/bin/shutdown.bat and close the console window.
8. You can get a log file as apache-tomcat-8.5.34/outputs/log4j.log. In the file, debug and trace level log messages are appeared at the time the load-test started. Also, you can get buffered log messages in apache-tomcat-8.5.34/outputs/buffer.txt. PADLA keeps the latest 300 (in this setting) log messages internally and outputs them with "[OUTPUT]" at the beginning when it changes the log level. To know when the messages are outputted, please refer to timestamps of them.
