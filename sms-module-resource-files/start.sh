#!/bin/bash
. /home/eirsapp/.bash_profile

#Check for VIP for HA. In case VIP not found on this server then exit
set -x
VAR=""
commonConfigurationFile=$commonConfigurationFilePath
source $commonConfigurationFile
echo "Virtual ip is $virtualIp"
vip_status=`/usr/sbin/ip add show | grep $virtualIp`
#if [ "$vip_status" != "$VAR" ]  &&  [ "$status" == "$VAR" ]
#then
# echo "SMS Notification Module Process should run on this server"
##java -Dlog4j.configurationFile=./log4j2.xml  -Dspring.config.location=file:./application.properties,file:${APP_HOME}/configuration/configuration.properties -jar apiservice3.jar 1>${DATA_HOME}/APIService3/logI3.txt 2>${DATA_HOME}/APIService3/error.txt &
#else
# echo "$virtualIp not attached to this server. SMS Notification Module should run on other server. Exiting..."
# exit
#fi

#Start Process script functionality starts from here
VAR=""
operator=$1
build_path="${APP_HOME}/SmsService/"
build="SmsService.jar"
cd $build_path
status=`ps -ef | grep $build | grep java`
if [ "$status" != "$VAR" ]
then
 echo "Process Already Running"
else
 echo "Starting Process"
# java -Dlog4j.configuration=file:./log4j.properties -Dspring.config.location=file:./application.properties,file:/u01/eirsapp/configuration/configuration.properties  -jar SmsService.jar $operator &
 
java -Dlog4j.configurationFile=file:./log4j2.xml -Dspring.config.location=file:./application.properties,file:/u01/eirsapp/configuration/configuration.properties  -jar SmsService.jar $operator &

echo "Process Started"
fi

