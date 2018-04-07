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

#print a shell command before its execution
set -ex

sudo bash -c 'service ntopng stop'

CONFIG=./scripts/measurements/setup.conf
if [[ -f $CONFIG ]]
then
  # shellcheck disable=SC1090
  . "$CONFIG"
else
  echo "The config file could not be located at ./setup.conf. Exiting."
  exit
fi

truncate -s 0 "$PERF_CONCURRENCYLOG"
truncate -s 0 "$PERF_LOGFILE"

{
  echo "performance of tools on single processor"
  echo "--------------------------------------"
} >> "$PERF_CONCURRENCYLOG"

#turn off three processors
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on single processor
./scripts/measurements/tools_run.sh
cat "$LOGFILE" >> "$PERF_CONCURRENCYLOG"
truncate -s 0 "$PERF_LOGFILE"

{
  echo -e "\n\n\n"
  echo "--------------------------------------"
  echo "performance of tools on two processors"
  echo "--------------------------------------"
} >> "$PERF_CONCURRENCYLOG"

#turn off two processors
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on two processors
./scripts/measurements/tools_run.sh
cat "$PERF_LOGFILE" >> "$PERF_CONCURRENCYLOG"
truncate -s 0 "$PERF_LOGFILE"


{
  echo -e "\n\n\n"
  echo "--------------------------------------"
  echo "performance of tools on three processors"
  echo "--------------------------------------"
} >> "$PERF_CONCURRENCYLOG"

#turn off one processor
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on three processors
./scripts/measurements/tools_run.sh
cat "$PERF_LOGFILE" >> "$PERF_CONCURRENCYLOG"
truncate -s 0 "$PERF_LOGFILE"


{
  echo -e "\n\n\n"
  echo "--------------------------------------"
  echo "performance of tools on four processors"
  echo "--------------------------------------"
} >> "$PERF_CONCURRENCYLOG"

#keep all four processors on
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run all tools on four processors
./scripts/measurements/tools_run.sh
cat "$PERF_LOGFILE" >> "$PERF_CONCURRENCYLOG"
truncate -s 0 "$PERF_LOGFILE"

#remove error messages from tshark (come because of running tshark as root)
grep -v -E "Error during loading" "$PERF_CONCURRENCYLOG" | grep -v "Wireshark" | grep -v "root" > data/log/temp.log
mv data/log/temp.log "$PERF_CONCURRENCYLOG"
rm "$PERF_LOGFILE"
