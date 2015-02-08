#!/bin/sh

TARGET=../app/src/main/res/drawable
if [ -f $1/$1.png ]
then
	convert  -resize 48x48 $1/$1.png $TARGET-mdpi/$1.png
	convert  -resize 72x72 $1/$1.png $TARGET-hdpi/$1.png
	convert  -resize 96x96 $1/$1.png $TARGET-xhdpi/$1.png
	convert  -resize 144x144 $1/$1.png $TARGET-xxhdpi/$1.png
fi
