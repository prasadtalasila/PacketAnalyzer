#!/bin/bash

#print a shell command before its execution
set -xv

#load environment variables set by java_install.sh script
#source /etc/environment

#adjust tomcat settings of maven
mv /opt/maven/conf/settings.xml /opt/maven/conf/settings.xml.bkp
cp conf/settings.xml /opt/maven/conf/settings.xml

#Create a user and group named tomcat
groupadd tomcat
useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat

#download and install tomcat
if [ ! -f apache-tomcat-8.5.29.tar.gz ]; then
    curl -O http://redrockdigimark.com/apachemirror/tomcat/tomcat-8/v8.5.29/bin/apache-tomcat-8.5.29.tar.gz
fi
mkdir /opt/tomcat
tar xzf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

#Give the tomcat user ownership over the entire installation directory:
chown -R tomcat:tomcat /opt/tomcat

#give the tomcat group read access to the conf directory and all of its contents, and execute access to the directory itself
cp conf/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml
chown tomcat:tomcat /opt/tomcat/conf/tomcat-users.xml

#reload the systemd daemon so that it knows about our service file
systemctl daemon-reload
systemctl start tomcat

#copy the correct property files
cp -f conf/*.properties src/main/resources/META-INF/

#create directories for storing elastic search data and logs
mkdir -p /opt/darshini-es/data
mkdir -p /opt/darshini-es/logs
chown -R tomcat:tomcat /opt/darshini-es
chmod -R 777 /opt/darshini-es

#create log file for darshini
mkdir -p /opt/darshini-logs
touch /opt/darshini-logs/darshini
chown -R tomcat:tomcat /opt/darshini-logs

#install npm modules for browser client dependencies
mkdir -p src/main/webapp/WEB-INF/node_modules
npm install --prefix src/main/webapp/WEB-INF


ls -l /opt
ls -l /opt/tomcat
systemctl status tomcat
