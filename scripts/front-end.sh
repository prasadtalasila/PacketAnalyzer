#!/bin/bash

#print a shell command before its execution
set -xv

sudo cp -rf src/main/webapp/WEB-INF/view /opt/tomcat/webapps/protocolanalyzer/WEB-INF
