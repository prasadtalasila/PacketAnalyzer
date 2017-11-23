#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on BroIDS, tshark and ntopng
# 	   this is a wrapper script around "tools-run.sh"
# Date: 01-Aug-2017
# Dependencies: "tools_run.sh" script must exist in the PWD
#		dependencies of "tools_run.sh" must be satisfied
# Invocation: $./tools_concurrency.sh as non-root user
# Output: results in "concurrency.log"
######################

sudo bash -c 'service ntopng stop'

LOGFILE="data/log/performance.log"
CONCURRENCYLOG="data/log/concurrency.log"
truncate -s 0 "$CONCURRENCYLOG"
truncate -s 0 "$LOGFILE"

{
  echo "performance of tools on single processor"
  echo "--------------------------------------"
} >> "$CONCURRENCYLOG"

#turn off three processors
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on single processor
./scripts/measurements/tools_run.sh
cat "$LOGFILE" >> "$CONCURRENCYLOG"
truncate -s 0 "$LOGFILE"

{
  echo -e "\n\n\n"
  echo "--------------------------------------"
  echo "performance of tools on two processors"
  echo "--------------------------------------"
} >> "$CONCURRENCYLOG"

#turn off two processors
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on two processors
./scripts/measurements/tools_run.sh
cat "$LOGFILE" >> "$CONCURRENCYLOG"
truncate -s 0 "$LOGFILE"


{
  echo -e "\n\n\n"
  echo "--------------------------------------"
  echo "performance of tools on three processors"
  echo "--------------------------------------"
} >> "$CONCURRENCYLOG"

#turn off one processor
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on three processors
./scripts/measurements/tools_run.sh
cat "$LOGFILE" >> "$CONCURRENCYLOG"
truncate -s 0 "$LOGFILE"


{
  echo -e "\n\n\n"
  echo "--------------------------------------"
  echo "performance of tools on four processors"
  echo "--------------------------------------"
} >> "$CONCURRENCYLOG"

#keep all four processors on
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on four processors
./scripts/measurements/tools_run.sh
cat "$LOGFILE" >> "$CONCURRENCYLOG"
truncate -s 0 "$LOGFILE"

#remove error messages from tshark (come because of running tshark as root)
grep -v -E "Error during loading" "$CONCURRENCYLOG" | grep -v "Wireshark" | grep -v "root" > data/log/temp.log
mv data/log/temp.log "$CONCURRENCYLOG"
rm "$LOGFILE"
