%~d0
cd %~dp0
echo set JAVA_HOME=C:\Program Files\Java\jrockit-jdk1.6.0_45-R28.2.7-4.1.0
mvn clean install -Dmaven.test.skip=true