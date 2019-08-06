echo off
SET CLASSPATH=.;C:\ProgIBM\MQ\Java\lib\com.ibm.mqjms.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.mq.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.defaultconfig.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jmqi.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jms.Nojndi.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jndi.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jms.jar
SET PATH=.;C:\Program Files (x86)\Java\jre1.8.0_161\bin;%PATH%
echo on
input %1
java demo.DemoSendRequest -i file:C:\$Data\JMSContext -c jms.QMCI -d jms.DEMO.REQUEST -p large -q white
