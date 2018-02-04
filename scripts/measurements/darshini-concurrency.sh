#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on Darshini
# 	   this is a wrapper script around "darshini-test.sh"
# Date: 05-Sep-2017
# Dependencies: "tools_run.sh" script must exist in the PWD
#		dependencies of "tools_run.sh" must be satisfied
# Invocation: $./darshini-concurrency.sh as non-root user
# Output: results in "darshini-concurrency.log", "darshini-concurrency-summary.log"
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


truncate -s 0 "$CONCURRENCYLOG"

{
  echo "performance of darshini on single processor"
  echo "-------------------------------------------"
} >> "$CONCURRENCYLOG"

#turn off three processors
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run darshini on single processor
./scripts/measurements/darshini-test.sh
cat "$LOGFILE" >>  "$CONCURRENCYLOG"

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
#run darshini on two processors
./scripts/measurements/darshini-test.sh
cat "$LOGFILE" >>  "$CONCURRENCYLOG"

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
#run darshini on three processors
./scripts/measurements/darshini-test.sh
cat "$LOGFILE" >>  "$CONCURRENCYLOG"

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
#run darshini on four processors
./scripts/measurements/darshini-test.sh
cat "$LOGFILE" >>  "$CONCURRENCYLOG"

#summarize results into another log file
grep -f ./scripts/measurements/darshini-concurrency.grep "$CONCURRENCYLOG" \
  > "$CONCURRENCYSUMMARY"
