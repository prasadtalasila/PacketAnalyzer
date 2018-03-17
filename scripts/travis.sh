#!/bin/bash

#print a shell command before its execution
set -xv

#load environment variables set by java_install.sh script
#source /etc/environment

#Create a user and group named tomcat
sudo groupadd tomcat
sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat

#download and install tomcat
if [ ! -f apache-tomcat-8.5.29.tar.gz ]; then
    curl -O http://redrockdigimark.com/apachemirror/tomcat/tomcat-8/v8.5.29/bin/apache-tomcat-8.5.29.tar.gz
fi
sudo mkdir /opt/tomcat
sudo tar xzf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

#Give the tomcat user ownership over the entire installation directory:
sudo chown -R tomcat:tomcat /opt/tomcat

#give the tomcat group read access to the conf directory and all of its contents, and execute access to the directory itself
sudo cp conf/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml
sudo chown tomcat:tomcat /opt/tomcat/conf/tomcat-users.xml

#start tomcat
sudo -H -u tomcat bash -c '/opt/tomcat/bin/startup.sh'

#copy the correct property files
cp -f conf/*.properties src/main/resources/META-INF/

#create directories for storing elastic search data and logs
sudo mkdir -p /opt/darshini-es/data
sudo mkdir -p /opt/darshini-es/logs
sudo chown -R tomcat:tomcat /opt/darshini-es
sudo chmod -R 777 /opt/darshini-es

#create log file for darshini
sudo mkdir -p /opt/darshini-logs
sudo touch /opt/darshini-logs/darshini
sudo chown -R tomcat:tomcat /opt/darshini-logs

#install npm modules for browser client dependencies
mkdir -p src/main/webapp/WEB-INF/node_modules
npm install --prefix src/main/webapp/WEB-INF

#know about tomcat process
ps -efaux | grep tomcat | grep java

bash scripts/travis-deploy.sh

curl http://localhost:8080
curl http://localhost:8080/protocolanalyzer
curl -H "Content-Type: application/json" -d '{"email": "abc", "password": "abc"}' http://localhost:8080/protocolanalyzer/signin

sudo ls -l /opt/tomcat/webapps