#!/bin/bash

#print a shell command before its execution
set -ex

# npm install -g csslint
# npm install --prefix ./ vnu-jar

mvn test -B
# mvn '-Dtest=unit.in.ac.bits.protocolanalyzer/**/*Test.java' test
# mvn '-Dtest=integration.in.ac.bits.protocolanalyzer/**/*Test.java' test
mvn cobertura:cobertura -Dcobertura.report.format=xml
