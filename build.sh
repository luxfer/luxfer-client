#!/bin/bash
rm luxfer.zip

mv dist luxfer

cp README.md luxfer
cp run.sh luxfer/

zip luxfer luxfer/*

rm luxfer/README.md
rm luxfer/run.sh

mv luxfer dist
