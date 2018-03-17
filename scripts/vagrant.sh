#!/bin/bash

#print a shell command before its execution
set -xv

echo "PS1='\[\e[0;31m\]\u\[\e[m\] \[\e[1;34m\]\w\[\e[m\] \$ '" >> /home/ubuntu/.bashrc
echo "cd /home/ubuntu/darshini" >>  /home/ubuntu/.bashrc
source /home/ubuntu/.bashrc
cd /home/ubuntu/darshini

#run the installation script
bash scripts/install.sh
