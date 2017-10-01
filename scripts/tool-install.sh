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

#install bro
sudo apt-get install cmake make gcc g++ flex bison libpcap-dev libssl-dev python-dev swig zlib1g-dev
sudo apt-get install bro
sudo wget https://www.bro.org/downloads/bro-2.5.1.tar.gz
mkdir /opt/bro
sudo tar -xvzf bro-2.5.1.tar.gz -C /opt/bro
sudo apt-get clean all
sudo apt-get update
rm bro-2.5.1.tar.gz
export PATH=/usr/local/bro/bin:$PATH

