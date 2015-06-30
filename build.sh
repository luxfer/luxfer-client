#!/bin/bash
rm luxfer.zip

mv dist luxfer

cp README.txt luxfer/
cp run.sh luxfer/

zip luxfer luxfer/* luxfer/**/*

rm luxfer/README.txt
rm luxfer/run.sh

mv luxfer dist
