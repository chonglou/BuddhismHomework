#!/bin/sh
f=/tmp/1.sd

if [ ! -f $f ]
then
	mksdcard 2G $f
fi

/opt/android-sdk/tools/emulator64-arm -avd galaxy -netspeed full -netdelay none -wipe-data -sdcard $f
