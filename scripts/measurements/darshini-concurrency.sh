#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on Darshini
# 	   this is a wrapper script around "darshini-test.sh"
# Date: 05-Sep-2017
# Dependencies: "tools_run.sh" script must exist in the PWD
#		dependencies of "tools_run.sh" must be satisfied
# Invocation: $./darshini-concurrency.sh as root user
# Output: results in "darshini-concurrency.log", "darshini-concurrency-summary.log"
######################

truncate -s 0 darshini-concurrency.log

echo "performance of darshini on single processor"  >> darshini-concurrency.log
echo "-------------------------------------------" >> darshini-concurrency.log

#turn off three processors
echo 0 > /sys/devices/system/cpu/cpu3/online
echo 0 > /sys/devices/system/cpu/cpu2/online
echo 0 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run darshini on single processor
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat darshini-test.log >> darshini-concurrency.log

echo -e "\n\n\n" >> darshini-concurrency.log
echo "-----------------------------------------" >> darshini-concurrency.log
echo "performance of darshini on two processors" >> darshini-concurrency.log
echo "-----------------------------------------" >> darshini-concurrency.log

#turn off two processors
echo 0 > /sys/devices/system/cpu/cpu3/online
echo 0 > /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run darshini on two processors
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat darshini-test.log >> darshini-concurrency.log


echo -e "\n\n\n" >> darshini-concurrency.log
echo "-------------------------------------------" >> darshini-concurrency.log
echo "performance of darshini on three processors" >> darshini-concurrency.log
echo "-------------------------------------------" >> darshini-concurrency.log

#turn off one processor
echo 0 > /sys/devices/system/cpu/cpu3/online
echo 1 > /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run darshini on three processors
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat darshini-test.log >> darshini-concurrency.log


echo -e "\n\n\n" >> darshini-concurrency.log
echo "------------------------------------------" >> darshini-concurrency.log
echo "performance of darshini on four processors" >> darshini-concurrency.log
echo "------------------------------------------" >> darshini-concurrency.log

#keep all four processors on
echo 1 > /sys/devices/system/cpu/cpu3/online
echo 1 > /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run darshini on four processors
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat darshini-test.log >> darshini-concurrency.log

#summarize results into another log file
grep -f ./scripts	/measurements/darshini-concurrency.grep darshini-concurrency.log > darshini-concurrency-summary.log
