REM @echo off
REM set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\..\HeijoAgent\HeijoAgent.jar=OptionFile=..\..\log4j2\conf\AgentOptions_34.xml"

REM SET JAVA_OPTS=%JAVA_OPTS% -Dlog4j.configurationFile=../../log4j2/conf/log4j2.xml

@echo off
set CATALINA_OPTS=%JAVA_OPTS% -javaagent:"..\..\HeijoAgent\HeijoAgent.jar=OptionFile=..\..\log4j2\conf\AgentOptions.xml"

set CLASSPATH=../../log4j2/lib/*;../../log4j2/conf