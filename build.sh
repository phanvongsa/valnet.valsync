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
local_war_directory="/mnt/wars"
deploy_file="/mnt/source/$project_name/deploy.$app_name.$profile"
KNOWN_HOSTS_FILE="/root/.ssh/known_hosts"
### add known hosts
if ! grep -q "$web_app_server" "$KNOWN_HOSTS_FILE"; then
  echo "Adding $web_app_server to known_hosts..."
  ssh-keyscan $web_app_server >> "$KNOWN_HOSTS_FILE"
fi

if ! grep -q "$war_server" "$KNOWN_HOSTS_FILE"; then
  echo "Adding $war_server to known_hosts..."
  ssh-keyscan $war_server >> "$KNOWN_HOSTS_FILE"
fi

### BUILD
if [[ "$action" =~ "build" || "$action" == "release" ]]; then
  if [[ -e "$release_file" ]]; then
    echo "Cleaning up old release files"
    rm "$release_file"
  fi

  # > Build
  echo "Building Project"
  mvn package clean package -P$profile

  # > Copy to Release folder
  if [[ -e "$target_file" ]]; then
    echo "Build Success"  
    cp "$target_file" "$release_file"
    
    if [[ -e "$release_file" ]]; then
      echo "Copy to release folder success"
    else
      echo "Copy to release folder error"
      exit 1
    fi

  else
    echo "Build Error"
    exit 1
  fi
<<<<<<< HEAD
fi
### UPLOAD
if [[ "$action" =~ "upload" || "$action" == "release" ]]; then
=======

fi
### UPLOAD
if [[ "$action" == "upload" || "$action" == "release" ]]; then  
>>>>>>> dev
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
# --> 1.  scp into web server /home/{account}/wars
# --> 2.  ssh into web server
# -->   2.1  swicth to web server account (tomcat)
# -->   2.2  back up existing war file
# -->   2.3  copy /home/{account}/wars/app.war to webapps/

<<<<<<< HEAD
if [[ "$action" =~ "deploy" || "$action" == "release" ]]; then
=======
if [[ "$action" == "deploy" || "$action" == "release" ]]; then
>>>>>>> dev
  echo "Deploying Application ==> WEB Server ($profile)"
  
  echo "  ==> Applying profile server settings"
  case $profile in
    "local")
        web_app_server=$server_local
        web_app_un=$un_local
        web_app_pw=$pw_local
        web_app_war_directory=$server_war_directory_local
        web_app_directory=$webapp_directory_local        
        tomcat_un=$tomcat_un_local
        tomcat_pw=$tomcat_pw_local
        ;;
    "dev")
        web_app_server=$server_dev
        web_app_un=$un_dev
        web_app_pw=$pw_dev
        web_app_war_directory=$server_war_directory_dev
        web_app_directory=$webapp_directory_dev  
        tomcat_un=$tomcat_un_dev
        tomcat_pw=$tomcat_pw_dev
        ;;
    "uat")
        web_app_server=$server_uat
        web_app_un=$un_uat
        web_app_pw=$pw_uat
        web_app_war_directory=$server_war_directory_uat
        web_app_directory=$webapp_directory_uat
        tomcat_un=$tomcat_un_uat
        tomcat_pw=$tomcat_pw_uat
        ;;
    *)
        echo "Unknown profile $profile."
        exit 1
        ;;
  esac
<<<<<<< HEAD

=======
  
>>>>>>> dev
  ## 0.  download war file war server
  printf "  Retrieving application from WAR Server  => \n"
  sshpass -p ${war_pw} scp ${war_un}@${war_server}:$war_directory/$app_name-$profile.war $local_war_directory
  
  local_war_file="$local_war_directory/$app_name-$profile.war"
  if [ ! -f $local_war_file ]; then
    echo "Fail"
    exit 1  
  fi
  echo "OK"

  ## 1. copy file to web server
  remote_war_file="$web_app_war_directory/$app_name.war"  
  printf "  Putting application on Web Server  => \n"
  sshpass -p ${web_app_pw} scp $local_war_file $web_app_un@$web_app_server:${remote_war_file}
  echo "OK"
  
  if [ -f $deploy_file ]; then
    printf "  Putting deploy.$app_name.$profile on Web Server  => \n"
    sshpass -p ${web_app_pw} scp $deploy_file "$web_app_un@$web_app_server:~/deploy.$app_name"
    echo "OK"
  fi
    
  if [[ $profile == "local" || $profile == "dev" ]]; then
    sshpass -p ${web_app_pw} ssh $web_app_un@$web_app_server 'bash ~/deploy.valsync'
  else  
    echo "Run ~/deploy.$app_name on $profile server as $web_app_un"  
  fi  
fi