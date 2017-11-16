#!/bin/bash

echo "] Packaging the app ..."
mvn package
echo "] Packaging complete."
echo ""
echo "] Restarting tomcat ..."
sudo service tomcat restart
echo "] Restart complete."
echo ""
echo "] Deploying Application ..."
mvn tomcat:deploy
echo "] Deploy complete"
