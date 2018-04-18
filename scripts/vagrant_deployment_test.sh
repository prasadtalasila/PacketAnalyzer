#!/bin/bash

#print a shell command before its execution
set -ex

#give permissions to tomcat over packet-files(packet_data.pcap)
cp data/packet/packet_data.pcap /tmp/packet_data.pcap
sudo chown tomcat:tomcat /tmp/packet_data.pcap

#deployment of the Application
bash scripts/redeploy.sh
bash scripts/redeploy.sh

#run deployment test
curl -H "Content-Type: application/json" -d '{"email": "abc", "password": "abc"}' http://localhost:8080/protocolanalyzer/signup
curl -H "Content-Type: application/json" -d '{"email": "abc", "password": "abc"}' http://localhost:8080/protocolanalyzer/signin

pcapFile="/tmp/packet_data.pcap"
protocolGraphPath="/data/graph.p4"

curl -X GET -H "Content-Type: application/json" http://localhost:8080/protocolanalyzer/test?pcapPath="$pcapFile"\&protocolGraphPath="$protocolGraphPath"
sleep 90

cat /opt/darshini-logs/darshini

#check condition
grep 'PcapAnalyzer:104 - Final read count = 214' /opt/darshini-logs/darshini
grep 'PerformanceMetrics:71 - Total experiment Duration :' /opt/darshini-logs/darshini

#Remove the log filea and packet data file
truncate -s 0 /opt/darshini-logs/darshini
sudo rm /tmp/packet_data.pcap

echo "=========Success========="

