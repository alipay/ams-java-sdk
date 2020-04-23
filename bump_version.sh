#!/bin/bash

shopt -s expand_aliases
if [ ! -n "$1" ] ;then
	echo "Please enter a version"
 	exit 1
else
  	echo "The new version is $1 !"
fi

currentVersion=`sed -n '/<project /,/<name/p' pom.xml | grep version | cut -d '>' -f2 | cut -d '<' -f1`
echo "The current version is $currentVersion"

if [ `uname` == "Darwin" ] ;then
 	echo "This is OS X"
 	alias sed='sed -i ""'
else
 	echo "This is Linux"
 	alias sed='sed -i'
fi

sed "/<project /,/<name>/ s/<version>.*<\/version>/<version>$1<\/version>/" pom.xml
echo "DONE: pom.xml"

sed "s/sdkVersion                              = \".*\";/sdkVersion                              = \"$1.`date "+%Y%m%d"`\";/" src/main/java/com/alipay/ams/cfg/AMSSettings.java
echo "DONE: ./src/main/java/com/alipay/ams/cfg/AMSSettings.java"

sed "s/<version>.*<\/version>/<version>$1<\/version>/" README.md
sed "s/\"com.alipay.ams:ams-java:.*\"/\"com.alipay.ams:ams-java:$1\"/" README.md
echo "DONE: README.md"
