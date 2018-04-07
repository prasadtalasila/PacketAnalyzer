#!/bin/bash

#print a shell command before its execution
set -ex

javadoc -version -author -private -d ../docs -classpath /home/ubuntu/darshini/src/main/java/in/ac/bits/protocolanalyzer/utils/* -sourcepath . -subpackages .

