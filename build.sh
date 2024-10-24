#! /bin/bash

if [ "$#" -lt 2 ]; then
    echo "Usage: $0 <action build|upload|deploy|release> <profile local|dev|uat|prod>"
    exit 1
fi


. ./build.conf

action=$1
profile=$2
target_file="/mnt/source/$project_name/target/$app_name-$profile.war"
release_file="/mnt/release/$app_name-$profile.war"
war_directory=$war_directory
local_war_directory="/mnt/wars"
### BUILD
if [[ "$action" == "build" || "$action" == "release" ]]; then
  if [[ -e "$release_file" ]]; then
    echo "Cleaning up old release files"
    rm "$release_file"
  fi

  # > Build
  echo "Builing Project"
  mvn package clean package -P$profile

  # > Copy to Release folder
  if [[ -e "$target_file" ]]; then
    echo "Build Success"  
    cp "$target_file" "$release_file"
    
    if [[ -e "$release_file" ]]; then
      echo "Copy to realese folder success"
    else
      echo "Copy to realease folder error"
      exit 1
    fi

  else
    echo "Build Error"
    exit 1
  fi
fi

### UPLOAD
if [[ "$action" == "upload" || "$action" == "release" ]]; then  
  echo "Uploading ==> WAR Server ($war_server)"
  
  if [[ -e "$release_file" ]]; then    
sshpass -p ${war_pw} sftp -oBatchMode=no -b - ${war_un}@${war_server} <<EOF
  cd ${war_directory}
  put ${release_file}
  bye
EOF
  else
    echo "Release file $release_file does not exists, please build first"
    exit 1
  fi  
fi

### DEPLOY
# --> 0.  download war file war server
# --> 1.  scp into web server /home/account/wars
# --> 2.  ssh into web server
# -->   2.1  swicth to web server account (tomcat)
# -->   2.2  back up existing war file
# -->   2.3  copy /home/account/wars/app.war to webapps/

if [[ "$action" == "deploy" || "$action" == "release" ]]; then
  echo "Deploying Application ==> WEB Server ($profile)"  

  ## 0
  echo "  ==> Applying profile server settings"
  case $profile in
    "local")
        web_app_server=$local_server
        web_app_un=$local_un
        web_app_pw=$local_pw
        web_war_directory=$local_wars_directory
        web_app_directory=$local_webapp_directory
        ;;  
    *)
        echo "Unknown profile $profile."
        exit 1
        ;;
  esac
  echo "  ==> Retrieving application from WAR Server"
  sshpass -p ${war_pw} sftp -oBatchMode=no -b - ${war_un}@${war_server} <<EOF  
    get $war_directory/$app_name-$profile.war $local_war_directory
    bye
EOF

  echo "  ==> Uploading to Web App Server"
  echo "local: $local_war_directory/$app_name-$profile.war"
  echo "remote: $web_app_un@$web_app_server:$web_war_directory"
  sshpass -p "$web_app_pw" scp "$local_war_directory/$app_name-$profile.war" "$web_app_un@$web_app_server:$web_war_directory"

#   local_war_file="$local_war_directory/$app_name-$profile.war"
#   remote_war_file="$web_app_directory/$app_name.war"
# sshpass -p ${web_app_pw} sftp -oBatchMode=no -b - ${web_app_un}@${web_app_server} <<EOF  
#   put $local_war_file $remote_war_file
#   bye
#EOF

fi
#cd $war_directory
#get $app_name-$profile.war /root