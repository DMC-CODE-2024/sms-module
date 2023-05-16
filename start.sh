VAR=""
build_path="${APP_HOME}/SmsService/"
build="SmsService.jar"
cd $build_path
status=`ps -ef | grep $build | grep java`
if [ "$status" != "$VAR" ]
then
 echo "Process Already Running"
else
 echo "Starting Process"
 java  -Xmx1024m -Xms256m  -Dlog4j.configuration=file:./log4j.properties -jar $build -Dspring.config.location=:./application.properties 1>${APP_HOME}/SmsService/success.txt 2>${APP_HOME}/SmsService/error.txt OPERATOR_NAME &
echo "Process Started"
fi

