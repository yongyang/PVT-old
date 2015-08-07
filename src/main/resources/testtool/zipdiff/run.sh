#!/bin/sh
#load run.properties
if [ -f run.properties ];then
  . run.properties
fi

#Set variable
_workspace=$PWD
_workspace_new=$_workspace/new
_workspace_old=$_workspace/old
_archive_old=`basename $ARCHIVE_URL_OLD`
_archive_new=`basename $ARCHIVE_URL_NEW`

echo "debug: $_archive_old"

FETCH_CMD="wget --no-check-certificate" 
#Download archive and  test dependencies
$FETCH_CMD $ARCHIVE_URL_OLD
$FETCH_CMD $ARCHIVE_URL_NEW
for i in $(echo $DEPENDENCY_LIST|tr "," "\n"); 
do
  $FETCH_CMD $i;
done;

#Run test
unzip -q $_archive_old -d $_workspace_old
unzip -q $_archive_new -d $_workspace_new
java -jar dist-diff2-0.2.0-jar-with-dependencies.jar -a $_workspace_old -b $_workspace_new -i