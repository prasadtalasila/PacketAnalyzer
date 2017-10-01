#!/bin/bash

#install ntop
sudo wget http://apt-stable.ntop.org/16.04/all/apt-ntop-stable.deb
sudo dpkg -i apt-ntop-stable.deb
rm apt-ntop-stable.deb
sudo apt-get clean all
sudo apt-get update
sudo apt-get install pfring nprobe ntopng ntopng-data n2disk cento nbox

#install wireshark and tshark
sudo apt-get install wireshark tshark
sudo apt-get update

#install bro nad tomcat7
sudo apt-get install bro
sudo apt-get install tomcat7

#Tomcat classpath error fix
sudo wget https://archive.apache.org/dist/logging/log4j/2.8.2/apache-log4j-2.8.2-bin.tar.gz
sudo tar -xvzf apache-log4j-2.8.2-bin.tar.gz -C /usr/share/tomcat7/lib/
rm apache-log4j-2.8.2-bin.tar.gz
echo "CLASSPATH=/usr/share/tomcat7/lib/apache-log4j-2.8.2-bin/">>/usr/share/tomcat7/bin/setenv.sh

#install nodejs
curl -sL https://deb.nodesource.com/setup_6.x -o nodesource_setup.sh
sudo bash nodesource_setup.sh
sudo apt-get install nodejs

#install maven
sudo apt-get install maven

#npm packages for front end
cd ../src/main/webapp/WEB-INF/
sudo npm install
