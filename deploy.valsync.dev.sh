#! /bin/bash

app_file=valsync.war
source_file=~/wars/$app_file
destination_file=/usr/local/tomcat/webapps/$app_file
archive_file=~/wars.archive/$app_file

echo "===== Deploying Valsync Dev Environment ====="

echo ">> Running as $(whoami) user"

cd ~

printf ">> Checking Source file: "
if [[ -e $source_file ]]; then
  printf "OK\n"
  printf ">> Checking Destination file: " 
  if [[ -e $destination_file ]]; then
    printf "OK (existing app, start archiving)\n"
    mv $destination_file "$archive_file.$(date +%Y%m%d%H%M%S)"
  else
    printf "OK (no archiving required)\n"
  fi

  echo "copying source to destination"
  cp $source_file $destination_file
  echo "copying complete"
else
  printf "FAIL\n"
  echo "$app_file source file does not exists"
  exit 1
fi

echo ">>>> cleanup war"

rm ~/wars/$app_file

echo ">> Complete"

exit