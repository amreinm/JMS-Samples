@echo OFF
SET CLASSPATH=.;C:\ProgIBM\MQ\Java\lib\com.ibm.mqjms.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.mq.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.defaultconfig.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jmqi.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jms.Nojndi.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jndi.jar;C:\ProgIBM\MQ\Java\lib\com.ibm.jms.jar
rem SET PATH=.;C:\ProgIBM\WebSphere\AppServer\java\bin;%PATH%
:_start
echo/
echo/
@echo OFF
set /p QM=enter CF / Queue Manager name ..................:
set /p size=enter size (e.g. small,medium,large,...) ......:
set /p color=enter color (e.g. blue,green,...) .............:

:_makdefs
echo on
java demo.DemoSendRequest -i file:/C:/$Data/JMSContext -c jms.%QM% -d jms.DEMO.REQUEST -p %size% -q %color%

:_eof
pause
@echo OFF
goto _start