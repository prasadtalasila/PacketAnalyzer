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

#install dependencies for bro
sudo apt-get install cmake make gcc g++ flex bison libpcap-dev libssl-dev python-dev swig zlib1g-dev

#install C++ Actors Framework, Ignore the KEYEXPIRED 1503492954 or NO_PUBKEY error
sudo sh -c "echo 'deb http://download.opensuse.org/repositories/devel:/libraries:/caf/xUbuntu_14.04/ /' > /etc/apt/sources.list.d/caf.list"
sudo apt-get update
sudo apt-get install caf

#install bro
sudo wget https://www.bro.org/downloads/bro-2.5.1.tar.gz
mkdir /opt/bro
sudo tar -xvzf bro-2.5.1.tar.gz -C /opt/bro
sudo apt-get clean all
sudo apt-get update
#rm bro-2.5.1.tar.gz
export PATH=/usr/local/bro/bin:$PATH
