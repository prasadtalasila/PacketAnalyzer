#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on darshini
# Date: 01-Aug-2017
# Dependencies: Darshini must be installed and running
# Invocation: $./darshini-test.sh as non-root user
# Output: results in "darshini-test.log"
######################

truncate -s 0 data/log/darshini-test.log


echo "--------------------------------------" >> data/log/darshini-test.log
dir=$(pwd)
for pcapFile in `ls $dir/data/packet/*.pcap`
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r $pcapFile > /dev/null 2>&1

    
        
    cpus=$(cat /sys/devices/system/cpu/online)

    echo -e "\n\n\nExperiment"  >> data/log/darshini-test.log
    echo -e "---------------" >> data/log/darshini-test.log
    echo -e "file name = $pcapFile, number of cpus=$cpus" >> data/log/darshini-test.log
    echo -e "\n\n"  >> data/log/darshini-test.log
    #run one experiment in Darshini
    curl -H "Content-Type: application/json" -d '{"email": "abc", "password": "abc"}' http://localhost:8080/protocolanalyzer/signin
    curl -X GET -H "Content-Type: application/json" http://localhost:8080/protocolanalyzer/test?pcapPath=$pcapFile
    sleep 10
    
done



