#!/bin/bash

echo ">> REDEPLOYING APPLICATION"
echo ""
echo "] Removing the war file ..."
sudo rm -rf /var/lib/tomcat7/webapps/protocolanalyzer.war /var/lib/tomcat7/webapps/protocolanalyzer
echo "] Remove complete."
echo ""
echo "] Restarting tomcat ..."
sudo service tomcat restart
echo "] Restart complete."
echo ""
echo "] Deploying Application ..."
mvn tomcat:deploy
echo "] Deploy complete"
