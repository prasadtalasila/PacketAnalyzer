#!/bin/bash

echo ">> REDEPLOYING APPLICATION"
echo ""
echo "] Removing the war file ..."
sudo rm -rf /var/lib/tomcat7/webapps/protocolanalyzer.war /var/lib/tomcat7/webapps/protocolanalyzer
echo "] Remove complete."
echo ""
echo "] Restarting tomcat7 ..."
sudo service tomcat7 restart
echo "] Restart complete."
echo ""
echo "] Deploying Application ..."
mvn tomcat7:deploy
echo "] Deploy complete"