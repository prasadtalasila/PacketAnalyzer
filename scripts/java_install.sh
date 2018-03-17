#!/bin/bash


# install java via wget site oficial oracle

#print a shell command before its execution
set -xv

#check for installation of java, exit if present
JAVA_VER=$(java -version 2>&1 | grep -i version | sed 's/.*version ".*\.\(.*\)\..*"/\1/; 1q')
if [[ $JAVA_VER -ge 7 ]]
then
        echo "JAVA already installed. Aborting the installation of Java"
	exit 0
else
        echo "JAVA NOT installed. Installating Java"
fi


#JAVA_FILE_TAR, JAVA_URL_DOWNLOAD and JAVA_DIR can be entered according to your preference
JAVA_FILE_TAR="jdk-8u161-linux-x64.tar.gz"
JAVA_URL_DOWNLOAD="http://download.oracle.com/otn-pub/java/jdk/8u161-b12/2f38c3b165be4555a1fa6e98c45e0808/${JAVA_FILE_TAR}"
JAVA_DIR="jdk1.8.0_161"

# download and extract tar

if [ ! -f jdk-8u161-linux-x64.tar.gz ]; then
    wget --no-cookies --no-check-certificate --header "Cookie: oraclelicense=accept-securebackup-cookie" ${JAVA_URL_DOWNLOAD}
fi
tar zxvf ${JAVA_FILE_TAR} 
mv ${JAVA_DIR} /opt


# set default java
update-alternatives --install /usr/bin/java java /opt/${JAVA_DIR}/bin/java 1
update-alternatives --install /usr/bin/javac javac /opt/${JAVA_DIR}/bin/javac 1
update-alternatives --install /usr/bin/jar jar /opt/${JAVA_DIR}/bin/jar 1 

# set temp env vars
export JAVA_HOME=/opt/${JAVA_DIR}
export PATH=$PATH:/opt/${JAVA_DIR}/bin:/opt/${JAVA_DIR}/jre/bin
echo "export JAVA_HOME=/opt/${JAVA_DIR}" >> /etc/environment
echo "export PATH=$PATH:/opt/${JAVA_DIR}/bin:/opt/${JAVA_DIR}/jre/bin" >> /etc/environment
ls /opt/${JAVA_DIR} > /dev/null 2>&1
