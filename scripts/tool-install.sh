#!/bin/bash

#print a shell command before its execution
set -xv

#install ntop
sudo wget http://apt-stable.ntop.org/16.04/all/apt-ntop-stable.deb
sudo dpkg -i apt-ntop-stable.deb
rm apt-ntop-stable.deb
sudo apt-get clean all
sudo apt-get update
sudo apt-get install -y pfring pfring-dkms nprobe ntopng ntopng-data n2disk cento nbox

#install tshark
sudo apt-get install -y tshark
sudo apt-get update

#install C++ Actors Framework, Ignore the KEYEXPIRED 1503492954 or NO_PUBKEY error
sudo sh -c "echo 'deb http://download.opensuse.org/repositories/devel:/libraries:/caf/xUbuntu_14.04/ /' > /etc/apt/sources.list.d/caf.list"
sudo apt-get update
sudo apt-get install -y caf --allow-unauthenticated

#install dependencies for bro 
sudo apt-get update
sudo apt-get install -y bison cmake flex g++ gdb make libmagic-dev libpcap-dev libgeoip-dev libssl-dev python-dev swig2.0 zlib1g-dev

#Downloading and extract a GeoIP Database
wget http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz
wget http://geolite.maxmind.com/download/geoip/database/GeoLiteCityv6-beta/GeoLiteCityv6.dat.gz
gzip -d GeoLiteCity.dat.gz
gzip -d GeoLiteCityv6.dat.gz
sudo mv GeoLiteCity.dat /usr/share/GeoIP/GeoIPCity.dat
sudo mv GeoLiteCityv6.dat /usr/share/GeoIP/GeoIPCityv6.dat
rm GeoLiteCity.dat.gz
rm GeoLiteCityv6.dat.gz

#Installing Bro From Source
git clone --recursive https://github.com/bro/bro
cd bro
./configure
make
sudo make install
echo "export PATH=$PATH:/usr/local/bro/bin">>/etc/profile.d/3rd-party.sh
source /etc/profile.d/3rd-party.sh
echo "export PATH=/opt/maven/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/snap/bin:/opt/jdk1.8.0_161/bin:/opt/jdk1.8.0_161/jre/bin:/opt/jdk1.8.0_161/bin:/opt/jdk1.8.0_161/jre/bin:/usr/local/bro/bin" | sudo tee -a /etc/environment >/dev/null 2>&1
source /etc/environment

#Visit the link below for more details on installing and configuring bro.
#https://www.digitalocean.com/community/tutorials/how-to-install-bro-on-ubuntu-16-04
