#!/bin/bash
#########
# Purpose: test all the pcap files for run times and memory consumptions on all the three tools
# Invocation: $./tools_run.sh as non-root user
#
#########
LOGFILE="data/log/performance.log"
PATH=$PATH:/opt/bro/bin
export PATH
truncate -s 0 "$LOGFILE"

echo -e "bro performance\n" >> "$LOGFILE"
for pcapFile in data/packet/*.pcap
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1

    #actual run with output of system time and max memory
    #/usr/bin/time  --format "$pcapFile,%S,%M" bro -r $pcapFile
    #actual run with output of elapsed time and max memory
    /usr/bin/time  --format "$pcapFile,%e,%M" bro -r "$pcapFile"
done >> "$LOGFILE" 2>&1
echo -e "\n--------------------------\n" >> "$LOGFILE"
echo -e "ntopng performance\n" >> "$LOGFILE"
for pcapFile in data/packet/*.pcap
do
#    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1

    #actual run with output of system time and max memory
    #/usr/bin/time  --format "$pcapFile,%S,%M" ntopng --shutdown-when-done -i $pcapFile > /dev/null
    #actual run with output of elapsed time and max memory
    /usr/bin/time  --format "$pcapFile,%e,%M" ntopng --shutdown-when-done -i "$pcapFile" > /dev/null
done >> "$LOGFILE" 2>&1
echo -e "\n--------------------------\n" >> "$LOGFILE"
echo -e "tshark performance\n" >> "$LOGFILE"
for pcapFile in data/packet/*.pcap
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1

    #actual run with output of system time and max memory
    #/usr/bin/time  --format "$pcapFile,%S,%M" tshark -r $pcapFile > /dev/null
    #actual run with output of elapsed time and max memory
    /usr/bin/time  --format "$pcapFile,%e,%M" tshark -r "$pcapFile" > /dev/null
done >> "$LOGFILE" 2>&1
