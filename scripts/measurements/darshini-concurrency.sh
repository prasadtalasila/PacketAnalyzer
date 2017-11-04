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

truncate -s 0 data/log/darshini-concurrency.log

echo "performance of darshini on single processor"  >> data/log/darshini-concurrency.log
echo "-------------------------------------------" >> data/log/darshini-concurrency.log

#turn off three processors
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run darshini on single processor
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat data/log/darshini-test.log >> data/log/darshini-concurrency.log

echo -e "\n\n\n" >> data/log/darshini-concurrency.log
echo "-----------------------------------------" >> data/log/darshini-concurrency.log
echo "performance of darshini on two processors" >> data/log/darshini-concurrency.log
echo "-----------------------------------------" >> data/log/darshini-concurrency.log

#turn off two processors
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run darshini on two processors
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat data/log/darshini-test.log >> data/log/darshini-concurrency.log


echo -e "\n\n\n" >> data/log/darshini-concurrency.log
echo "-------------------------------------------" >> data/log/darshini-concurrency.log
echo "performance of darshini on three processors" >> data/log/darshini-concurrency.log
echo "-------------------------------------------" >> data/log/darshini-concurrency.log

#turn off one processor
sudo bash -c 'echo 0 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run darshini on three processors
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat data/log/darshini-test.log >> data/log/darshini-concurrency.log


echo -e "\n\n\n" >> data/log/darshini-concurrency.log
echo "------------------------------------------" >> data/log/darshini-concurrency.log
echo "performance of darshini on four processors" >> data/log/darshini-concurrency.log
echo "------------------------------------------" >> data/log/darshini-concurrency.log

#keep all four processors on
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu3/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu2/online'
sudo bash -c 'echo 1 > /sys/devices/system/cpu/cpu1/online'
sleep 10
#run darshini on four processors
./scripts/measurements/darshini-test.sh
#save the darshini-test.log to darshini-concurrency.log
cat data/log/darshini-test.log >> data/log/darshini-concurrency.log

#summarize results into another log file
grep -f ./scripts	/measurements/darshini-concurrency.grep data/log/darshini-concurrency.log > data/log/	darshini-concurrency-summary.log
