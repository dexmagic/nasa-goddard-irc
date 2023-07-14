#!/bin/bash


files=$(find . -type f -name "*.jar" -print)
for file in $files; do
	echo $file
	tar tvf $file | grep acplt
done


