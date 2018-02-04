#!/bin/bash
#########
# Purpose: test all the pcap files for run times and memory consumptions on all the three tools
# Invocation: $./tools_run.sh as non-root user
#
#########

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



truncate -s 0 "$PERF_LOGFILE"

echo -e "bro performance\n" >> "$PERF_LOGFILE"
for pcapFile in data/packet/*.pcap
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1

    #actual run with output of system time and max memory
    #/usr/bin/time  --format "$pcapFile,%S,%M" bro -r $pcapFile
    #actual run with output of elapsed time and max memory
    /usr/bin/time  --format "$pcapFile,%e,%M" bro -r "$pcapFile"
done >> "$PERF_LOGFILE" 2>&1
echo -e "\n--------------------------\n" >> "$PERF_LOGFILE"
echo -e "ntopng performance\n" >> "$PERF_LOGFILE"
for pcapFile in data/packet/*.pcap
do
#    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1

    #actual run with output of system time and max memory
    #/usr/bin/time  --format "$pcapFile,%S,%M" ntopng --shutdown-when-done -i $pcapFile > /dev/null
    #actual run with output of elapsed time and max memory
    /usr/bin/time  --format "$pcapFile,%e,%M" ntopng --shutdown-when-done -i "$pcapFile" > /dev/null
done >> "$PERF_LOGFILE" 2>&1
echo -e "\n--------------------------\n" >> "$PERF_LOGFILE"
echo -e "tshark performance\n" >> "$PERF_LOGFILE"
for pcapFile in data/packet/*.pcap
do
    #perform a trial run to get all the data into RAM
    /usr/bin/time bro -r "$pcapFile" > /dev/null 2>&1

    #actual run with output of system time and max memory
    #/usr/bin/time  --format "$pcapFile,%S,%M" tshark -r $pcapFile > /dev/null
    #actual run with output of elapsed time and max memory
    /usr/bin/time  --format "$pcapFile,%e,%M" tshark -r "$pcapFile" > /dev/null
done >> "$PERF_LOGFILE" 2>&1
