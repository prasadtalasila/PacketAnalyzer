#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on darshini
# Date: 01-Aug-2017
# Dependencies: Darshini must be installed and running
#		Darshini logs are stored in a file named "darshini"
# Invocation: $./darshini-test.sh as root user
# Output: results in "darshini-test.log"
######################

truncate -s 0 darshini-test.log


echo "--------------------------------------" >> darshini-test.log

for pcapFile in `sudo ls <Path to BITS-Darshini>/data/packet/*.pcap`
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r $pcapFile > /dev/null 2>&1

    truncate -s 0 darshini-logs/darshini
        
    cpus=$(cat /sys/devices/system/cpu/online)

    echo -e "\n\n\nExperiment"  >> darshini-test.log
    echo -e "---------------" >> darshini-test.log
    echo -e "file name = $pcapFile, number of cpus=$cpus" >> darshini-test.log
    echo -e "\n\n"  >> darshini-test.log
    #run one experiment in Darshini
    curl -H "Content-Type: application/json" -d '{"email": "abc", "password": "abc"}' http://localhost:8080/protocolanalyzer/signin
    curl -X GET -H "Content-Type: application/json" http://localhost:8080/protocolanalyzer/test?pcapPath=$pcapFile
    sleep 10
    cat darshini-logs/darshini >> darshini-test.log
done



