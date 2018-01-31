#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on darshini
# Date: 01-Aug-2017
# Dependencies: Darshini must be installed and running
# Invocation: $./darshini-test.sh as non-root user
# Output: results in "darshini-test.log"
######################

#print a shell command before its execution
set -xv

CONFIG=./scripts/measurements/setup.conf
if [[ -f $CONFIG ]]
then
  # shellcheck disable=SC1090
  . "$CONFIG"
else
  echo "The config file could not be located at ./setup.conf. Exiting."
  exit
fi

truncate -s 0 "$LOGFILE"


echo "--------------------------------------" >> "$LOGFILE"
dir=$(pwd)
protocolGraphPath=$dir/data/graph.p4
for pcapFile in $dir/data/packet/*.pcap
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1
    #truncate application logs of Darshini
    truncate -s 0 "$WEBAPPLOG"
    cpus=$(cat /sys/devices/system/cpu/online)
    {
      echo -e "\n\n\nExperiment"
      echo -e "---------------"
      echo -e "file name = $pcapFile, number of cpus=$cpus"
      echo -e "\n\n"
    } >> "$LOGFILE"
    #run one experiment in Darshini
    curl -H "Content-Type: application/json" -d '{"email": "abc", "password": "abc"}' http://localhost:8080/protocolanalyzer/signin
    curl -X GET -H "Content-Type: application/json" http://localhost:8080/protocolanalyzer/test?pcapPath="$pcapFile"\&protocolGraphPath="$protocolGraphPath"
    sleep 10
    cat "$WEBAPPLOG" >> "$LOGFILE"
done
