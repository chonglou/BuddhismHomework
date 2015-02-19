#!/bin/sh
f=/tmp/1.sd

if [ ! -f $f ]
then
	mksdcard 2G $f
fi

alias em64="/opt/android-sdk/tools/emulator64-arm -avd galaxy -netspeed full -netdelay none -sdcard $f"

em64
#em64 -wipe-data 

