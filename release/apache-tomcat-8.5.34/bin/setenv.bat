set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\HeijoAgent\HeijoAgent.jar=target=jpetstore_plus_tomcat-juli.jar,learningData=vectors.txt,bufferOutput=buffer.txt,phaseOutput=vecs.txt,buffer=300,interval=5"
@SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=file://%CATALINA_HOME%/conf/log4j2.xml
@echo off