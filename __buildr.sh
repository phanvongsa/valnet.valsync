#! /bin/bash

if [ "$#" -lt 2 ]; then
    echo "Usage: $0 <action build|upload|deploy|release> <profile local|dev|uat|prod>"
    exit 1
fi


. ./.__buildr.conf

action=$1
profile=$2
target_file="/app/source/$project_name/target/$app_name-$profile.war"
release_file="/app/release/$app_name-$profile.war"

### BUILD
if [[ -e "$release_file" ]]; then
  echo "Cleaning up old release files"
  rm "$release_file"
fi

if [[ "$action" == "build" || "$action" == "release" ]]; then
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

### UPLOAD (for property.nsw)
if [[ "$action" == "upload" || "$action" == "release" ]]; then
  if [[ $profile == "local" ]]; then
    echo "Copying Compile Project ==> WAR Directory"
    cp $release_file $war_directory
  else
    echo "Uploading Compile Project ==> WAR Server"
  fi
fi

### DEPLOY
if [[ "$action" == "deploy" || "$action" == "release" ]]; then
  echo "Deploying Project"
fi