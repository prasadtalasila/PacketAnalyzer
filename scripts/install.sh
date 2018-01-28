#!/bin/bash

#install java
sudo bash scripts/java_install.sh

yes | sudo apt-get -y --force-yes install curl
yes | apt-get -y --force-yes install git


#install maven
curl -O http://www-us.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz
sudo tar -xvzf apache-maven-3.5.2-bin.tar.gz
sudo mv apache-maven-3.5.2 /opt/maven 
export M2_HOME=/opt/maven
export M2=$M2_HOME/bin
export MAVEN_OPTS=-Xms256m -Xmx512m
export PATH=${M2}:${PATH}

#check for installation of java, exit if not present
JAVA_VER=$(java -version 2>&1 | grep -i version | sed 's/.*version ".*\.\(.*\)\..*"/\1/; 1q')
if [ $JAVA_VER -ge 7 ]
then
	echo JAVA installed.
else
	echo JAVA NOT installed.
	exit 1
fi

#adjust tomcat settings
cp -rf conf/settings.xml /usr/share/maven/conf/settings.xml
#Create a user and group named tomcat
groupadd tomcat
useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
curl -O https://www.redrockdigimark.com/apachemirror/tomcat/tomcat-8/v8.5.24/bin/apache-tomcat-8.5.24.tar.gz
mkdir /opt/tomcat
tar xzvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1
#Give the tomcat group ownership over the entire installation directory:
sudo chgrp -R tomcat /opt/tomcat
#give the tomcat group read access to the conf directory and all of its contents, and execute access to the directory itself
sudo chmod -R g+r /opt/tomcat/conf
sudo chmod g+x /opt/tomcat/conf
sudo chown -R tomcat /opt/tomcat
cp conf/tomcat.service /etc/systemd/system/
cp -rf conf/tomcat-users.xml /opt/tomcat/conf/tomcat-users.xml

#reload the systemd daemon so that it knows about our service file
systemctl daemon-reload
systemctl start tomcat

#copy the correct property files and create the required directories
cp -f conf/*.properties src/main/resources/META-INF/
mkdir -p /opt/darshini-es/data
chmod 777 /opt/darshini-es/data
mkdir -p /opt/darshini-es/logs
chmod 777 /opt/darshini-es/logs
mkdir -p /opt/darshini-logs
chmod 777 /opt/darshini-logs
touch /opt/darshini-logs/darshini
chown tomcat:tomcat /opt/darshini-logs/darshini
chmod 777 /opt/darshini-logs/darshini

#install nodejs
curl -sL https://deb.nodesource.com/setup_6.x -o nodesource_setup.sh
sudo bash nodesource_setup.sh
sudo apt-get -y install -y nodejs
sudo apt-get -y install build-essential
rm nodesource_setup.sh

mkdir -p src/main/webapp/WEB-INF/node_modules
#get the npm modules for js files of webpages
yes | npm install --no-bin-links src/main/webapp/WEB-INF
