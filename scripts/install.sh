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
printf 'Y\n' | apt-get install -y tomcat7 tomcat7-admin tomcat7-common
echo "export CATALINA_BASE=/var/lib/tomcat7" >> /usr/share/tomcat7/bin/setenv.sh
echo "export  JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> /usr/share/tomcat7/bin/setenv.sh
chmod +x /usr/share/tomcat7/bin/setenv.sh
cp -rf conf/tomcat-users.xml /var/lib/tomcat7/conf/tomcat-users.xml
sudo sed -i '/JAVA_OPTS="-Djava.awt.*/c\JAVA_OPTS="-Djava.awt.headless=true -Xmx1024m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:InitiatingHeapOccupancyPercent=0 -Dcom.sun.management.jmxremote.port=8086 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"' /etc/default/tomcat7
echo "JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> /etc/default/tomcat7

#Tomcat classpath error fix
sudo wget https://archive.apache.org/dist/logging/log4j/2.8.2/apache-log4j-2.8.2-bin.tar.gz
sudo tar -xvzf apache-log4j-2.8.2-bin.tar.gz -C /usr/share/tomcat7/lib/
rm apache-log4j-2.8.2-bin.tar.gz
echo "CLASSPATH=/usr/share/tomcat7/lib/apache-log4j-2.8.2-bin/">>/usr/share/tomcat7/bin/setenv.sh
service tomcat7 restart

#copy the correct property files and create the required directories
cp -f conf/*.properties src/main/resources/META-INF/
mkdir -p /opt/darshini-es/data
chmod 777 /opt/darshini-es/data
mkdir -p /opt/darshini-es/logs
chmod 777 /opt/darshini-es/logs
mkdir -p /opt/darshini-logs
chmod 777 /opt/darshini-logs

#install nodejs
curl -sL https://deb.nodesource.com/setup_6.x -o nodesource_setup.sh
sudo bash nodesource_setup.sh
sudo apt-get install -y nodejs
sudo apt-get install build-essential

#get the npm modules for js files of webpages
mkdir -p src/main/webapp/WEB-INF/node_modules
npm install --prefix ../src/main/webapp/WEB-INF
