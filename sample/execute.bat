@echo off

cd apache-tomcat-8.5.3\bin

call startup.bat

cd ..\..\apache-jmeter-5.0\bin\
java -jar ApacheJMeter.jar