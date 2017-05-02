#!/bin/bash

echo "] Packaging the app ..."
mvn package
echo "] Packaging complete."
echo "] Restarting tomcat7 ..."
sudo service tomcat7 restart
echo "] Restart complete."
mvn tomcat7:deploy