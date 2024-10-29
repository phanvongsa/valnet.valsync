#!/bin/bash
##> ./__tools.sh -p local|dev|uat|prod -a build|deploy #default (build-deploy)
while getopts a:p: flag
do
    case "${flag}" in
        p) prf=${OPTARG};;
        a) act=${OPTARG};;
    esac
done


[ -z "$act" ] && act="build-deploy"
act=${act,,}
echo "Action: ${act^^} >> Profile: ${prf^^}";


if [[ $act == *"build"* ]]; then
  echo "Building WAR"
  __sh="mvn -f ./datasync/pom.xml clean package -P${prf}"
  eval " $__sh"
fi


if [[ $act == *"deploy"* ]]; then
  destfilename=valsync
  __srcwar="./target/${destfilename}-${prf}.war"
  __dirwar="/mnt/c/GDRIVE/dev/docker/docker-data/services.tomcat/${destfilename}.war"

  echo "Deploying WAR ${__srcwar} ==> ${__dirwar}"
  __sh="cp ${__srcwar} ${__dirwar}"
  echo "    copying to docker war dir"
  eval " $__sh"
  __sh="docker cp ${__dirwar} tomcat:/usr/local/tomcat/webapps/${destfilename}.war"
  echo "    deploying to Tomcat server"
  eval " $__sh"
fi


# cp datasync/target/*.war /mnt/c/GDRIVE/dev/docker/docker-data/services.tomcat/wars/
# docker cp /mnt/c/GDRIVE/dev/docker/docker-data/services.tomcat/wars/valsync-local.war tomcat:/usr/local/tomcat/webapps/valsync.war