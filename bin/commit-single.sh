#!/bin/bash
cd ..
files=$(git status --porcelain | cut -b4-)
for file in $files; do
	echo $file
	git add $file
	git commit -m "edit code for compilation."
    # if [[ $file == *.java ]]; then
    #     echo $file
    # fi
done
