#!/bin/sh

TARGET=../app/src/main/res/drawable

for i in books courses launcher morning musics evening sitting
do
	j=ic_$i.png
	if [ -f images/$j ]
	then
		convert  -resize 48x48 images/$j $TARGET-mdpi/$j
		convert  -resize 72x72 images/$j $TARGET-hdpi/$j
		convert  -resize 96x96 images/$j $TARGET-xhdpi/$j
		convert  -resize 144x144 images/$j $TARGET-xxhdpi/$j
	fi
done

