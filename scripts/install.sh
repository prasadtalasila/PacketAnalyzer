#!/bin/bash

#print a shell command before its execution
set -xv

#install java
sudo bash scripts/java_install.sh

#load environment variables set by java_install.sh script
source /etc/environment

yes | sudo apt-get -y --force-yes install curl
yes | sudo apt-get -y --force-yes install git


#install maven
curl -O http://www-us.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz
tar -xzf apache-maven-3.5.2-bin.tar.gz
sudo mv apache-maven-3.5.2 /opt/maven 

#set shell environment variables for maven
{
    echo 'export MAVEN_HOME=/opt/maven'
    echo 'export MAVEN=/opt/maven/bin'
    echo "export MAVEN_OPTS='-Xms256m -Xmx512m'"
    echo 'export PATH=/opt/maven/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/snap/bin:/opt/jdk1.8.0_161/bin:/opt/jdk1.8.0_161/jre/bin:/opt/jdk1.8.0_161/bin:/opt/jdk1.8.0_161/jre/bin'
} | sudo tee -a /etc/environment >/dev/null 2>&1


#adjust tomcat settings of maven
sudo mv /opt/maven/conf/settings.xml /opt/maven/conf/settings.xml.bkp
sudo cp conf/settings.xml /opt/maven/conf/settings.xml
#cp -rf conf/settings.xml /usr/share/maven/conf/settings.xml


#check for installation of java, exit if not present
JAVA_VER=$(java -version 2>&1 | grep -i version | sed 's/.*version ".*\.\(.*\)\..*"/\1/; 1q')
if [ $JAVA_VER -ge 7 ]
then
	echo "JAVA installed. Proceeding with the installation of Tomcat 8.5"
else
	echo "JAVA NOT installed. Aborting the installation"
	exit 1
fi

#Create a user and group named tomcat
sudo groupadd tomcat
sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat

#download and install tomcat
curl -O http://www-eu.apache.org/dist/tomcat/tomcat-8/v8.5.27/bin/apache-tomcat-8.5.27.tar.gz
sudo mkdir /opt/tomcat
sudo tar xzf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

#Give the tomcat user ownership over the entire installation directory:
sudo chown -R tomcat:tomcat /opt/tomcat

#give the tomcat group read access to the conf directory and all of its contents, and execute access to the directory itself
sudo cp conf/tomcat.service /etc/systemd/system/
sudo cp conf/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml

#reload the systemd daemon so that it knows about our service file
sudo systemctl daemon-reload
sudo systemctl start tomcat

#copy the correct property files and create the required directories
cp -f conf/*.properties src/main/resources/META-INF/
sudo mkdir -p /opt/darshini-es/data
sudo chmod 777 /opt/darshini-es/data
sudo mkdir -p /opt/darshini-es/logs
sudo chmod 777 /opt/darshini-es/logs
sudo mkdir -p /opt/darshini-logs
sudo chmod 777 /opt/darshini-logs
sudo touch /opt/darshini-logs/darshini
sudo chown tomcat:tomcat /opt/darshini-logs/darshini
sudo chmod 777 /opt/darshini-logs/darshini

#install nodejs and npm
curl -sL https://deb.nodesource.com/setup_8.x -o nodesource_setup.sh
sudo bash nodesource_setup.sh
sudo apt-get update && sudo apt-get install --force-yes -y nodejs build-essential
rm nodesource_setup.sh

#install npm modules for browser client dependencies
mkdir -p src/main/webapp/WEB-INF/node_modules
npm install --prefix src/main/webapp/WEB-INF
