#!/bin/bash
#
#   File:       test.sh
#   Author:     Matteo Loporchio
#

# Run the Java program to reconstruct all logsBloom filters.
java -cp ".:lib/bcprov-jdk18on-171.jar:lib/gson-2.10.jar" Test data/blocks.json.gz data/output.csv
# Count the lines starting with "1,". Result should be equal to the number of blocks.
grep "^1," data/output.csv | wc -l