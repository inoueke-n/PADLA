rem To use "Learning" mode, uncomment the following 2 lines.
rem set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,phaseOutput=..\outputs\vectors.txt,interval=5"
rem @SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_LearningMode.xml

rem To use "Learning" mode, uncomment the following 2 lines.
rem set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,bufferOutput=..\outputs\buffer.txt,buffer=300,interval=5"
rem @SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2_for_AdopterMode.xml

@echo off