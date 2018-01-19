#!/bin/bash
printf 'Y\n' | add-apt-repository -y ppa:webupd8team/java
apt-get update
echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
apt-get install -y oracle-java8-installer
printf 'Y\n' | apt-get install -y oracle-java8-set-default
JAVA_HOME=/usr/lib/jvm/java-8-oracle
export JAVA_HOME
PATH=${JAVA_HOME}/bin:$PATH
export PATH

printf 'Y\n' | apt-get install -y maven
printf 'Y\n' | apt-get install git

#adjust tomcat7 settings
cp -rf conf/settings.xml /usr/share/maven/conf/settings.xml
#Create a user and group named tomcat
groupadd tomcat
useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat
curl -O http://redrockdigimark.com/apachemirror/tomcat/tomcat-8/v8.5.23/bin/apache-tomcat-8.5.23.tar.gz
mkdir /opt/tomcat
tar xzvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1
#Give the tomcat group ownership over the entire installation directory:
sudo chgrp -R tomcat /opt/tomcat
#give the tomcat group read access to the conf directory and all of its contents, and execute access to the directory itself
sudo chmod -R g+r /opt/tomcat/conf
sudo chmod g+x /opt/tomcat/conf
sudo chown -R tomcat /opt/tomcat
cp tomcat.service /etc/systemd/system/
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
chown tomcat7:tomcat7 /opt/darshini-logs/darshini
chmod 777 /opt/darshini-logs/darshini

#install nodejs
curl -sL https://deb.nodesource.com/setup_6.x -o nodesource_setup.sh
sudo bash nodesource_setup.sh
sudo apt-get install -y nodejs
sudo apt-get install build-essential
rm nodesource_setup.sh

#get the npm modules for js files of webpages
mkdir -p src/main/webapp/WEB-INF/node_modules
npm install --prefix ../src/main/webapp/WEB-INF
