#!/bin/bash
#######################
# Purpose: measure the impact of concurrency on BroIDS, tshark and ntopng
# 	   this is a wrapper script around "tools-run.sh"
# Date: 01-Aug-2017
# Dependencies: "tools_run.sh" script must exist in the PWD
#		dependencies of "tools_run.sh" must be satisfied
# Invocation: $./tools_concurrency.sh as root user
# Output: results in "performance.log"
######################

service ntopng stop
truncate -s 0 concurrency.log
truncate -s 0 performance.log

echo "performance of tools on single processor"  >> concurrency.log
echo "--------------------------------------" >> concurrency.log

#turn off three processors
echo 0 > /sys/devices/system/cpu/cpu3/online
echo 0 > /sys/devices/system/cpu/cpu2/online
echo 0 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run all tools on single processor
./tools_run.sh
#save the performance.log to concurrency.log
cat performance.log >> concurrency.log
truncate -s 0 performance.log

echo -e "\n\n\n" >> concurrency.log
echo "--------------------------------------" >> concurrency.log
echo "performance of tools on two processors" >> concurrency.log
echo "--------------------------------------" >> concurrency.log

#turn off two processors
echo 0 > /sys/devices/system/cpu/cpu3/online
echo 0 > /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run all tools on two processors
./tools_run.sh
#save the performance.log to concurrency.log
cat performance.log >> concurrency.log
truncate -s 0 performance.log


echo -e "\n\n\n" >> concurrency.log
echo "--------------------------------------" >> concurrency.log
echo "performance of tools on three processors" >> concurrency.log
echo "--------------------------------------" >> concurrency.log

#turn off one processor
echo 0 > /sys/devices/system/cpu/cpu3/online
echo 1 > /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run all tools on three processors
./tools_run.sh
#save the performance.log to concurrency.log
cat performance.log >> concurrency.log
truncate -s 0 performance.log


echo -e "\n\n\n" >> concurrency.log
echo "--------------------------------------" >> concurrency.log
echo "performance of tools on four processors" >> concurrency.log
echo "--------------------------------------" >> concurrency.log

#keep all four processors on
echo 1 > /sys/devices/system/cpu/cpu3/online
echo 1 > /sys/devices/system/cpu/cpu2/online
echo 1 > /sys/devices/system/cpu/cpu1/online
sleep 10
#run all tools on four processors
./tools_run.sh
#save the performance.log to concurrency.log
cat performance.log >> concurrency.log
truncate -s 0 performance.log

#remove error messages from tshark (come because of running tshark as root)
grep -v -E "Error during loading" concurrency.log | grep -v "Wireshark" | grep -v "root" > temp.log
mv temp.log concurrency.log
rm performance.log
