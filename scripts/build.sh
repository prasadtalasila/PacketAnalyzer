#!/bin/bash

echo "] Packaging the app ..."
mvn package
echo "] Packaging complete."
echo ""
echo "] Restarting tomcat7 ..."
sudo service tomcat7 restart
echo "] Restart complete."
echo ""
echo "] Deploying Application ..."
mvn tomcat7:deploy
echo "] Deploy complete"