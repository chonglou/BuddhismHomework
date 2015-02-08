#!/bin/sh

TARGET=../app/src/main/res/drawable
convert  -resize 48x48 logo.png $TARGET-mdpi/ic_launcher.png
convert  -resize 72x72 logo.png $TARGET-hdpi/ic_launcher.png
convert  -resize 96x96 logo.png $TARGET-xhdpi/ic_launcher.png
convert  -resize 144x144 logo.png $TARGET-xxhdpi/ic_launcher.png
