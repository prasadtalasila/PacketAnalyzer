cd darshini
printf 'Y\n' | add-apt-repository -y ppa:webupd8team/java
apt-get update
echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
apt-get install -y oracle-java7-installer
printf 'Y\n' | apt install -y oracle-java7-set-default
printf 'Y\n' | apt-get install -y maven
sed -i "/<servers>/r conf/maven_settings.txt" /usr/share/maven/conf/settings.xml
printf 'Y\n' | apt-get install -y tomcat7 tomcat7-admin tomcat7-common
echo "export CATALINA_BASE=/var/lib/tomcat7" >> /usr/share/tomcat7/bin/setenv.sh
sed -i "/<tomcat-users>/r conf/tomcat_users.txt" /var/lib/tomcat7/conf/tomcat-users.xml
mvn package
service tomcat7 restart
mvn tomcat7:deploy