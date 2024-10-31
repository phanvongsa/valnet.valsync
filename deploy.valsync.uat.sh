app_file=valsync.war
source_file=/home/valro/wars/$app_file
destination_file=/usr/local/tomcat/webapps/$app_file
archive_directory=/home/valro/wars.archive
tmp_app_file=/tmp/$app_file
tmp_archive_file=/tmp/$app_file.$(date +%Y%m%d%H%M%S)
echo "===================="
echo "Running as (0) $(whoami)"
echo "===================="
printf ">> Checking Source file: "
if [[ -e $source_file ]]; then
  printf "OK\n"
  echo "copying $source_file ==> $tmp_app_file"
  cp $source_file $tmp_app_file
else
  printf "FAIL\n"
  echo "$app_file source file does not exists"
  exit 1
fi


sudo su - tomcat <<EOF
echo "===================="
printf "Running as (1) "
whoami
echo "===================="

if [[ -e $tmp_app_file ]]; then
  printf ">> Checking Destination file: "
  if [[ -e $destination_file ]]; then
    printf "OK (existing app, start archiving)\n"
    echo "temp archiving $destination_file ==> $tmp_archive_file"
    cp $destination_file $tmp_archive_file
  else
    printf "OK (no archiving required)\n"
  fi

  echo "copying $tmp_app_file ==> $destination_file"
  cp $tmp_app_file $destination_file
  echo "copying complete"
else
  echo "FAIL"
  exit 1
fi
EOF

echo "==================="
printf "Running as (2) "
whoami
echo "==================="
echo ">>>> cleanup war"
echo "archiving $tmp_archive_file ==> $archive_directory/"
cp $tmp_archive_file $archive_directory/
rm $tmp_app_file
rm $source_file

sudo su - tomcat <<EOF
echo "===================="
printf "Running as (3) "
whoami
echo "===================="
if [[ -e $tmp_archive_file ]]; then
  echo "removing $tmp_archive_file"
  rm $tmp_archive_file
fi
EOF

echo ">> Complete"