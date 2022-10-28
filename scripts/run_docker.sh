#!/bin/bash
helpFunction()
{
   echo ""
   echo "Usage: $0 -p port -n container_name(default: facogen_sdd_gen_cont)"
   echo -e "\t-p port"
   echo -e "\t-n container_name"
   exit 1 # Exit script after printing help
}

image_name="facogen_ssd_gen:latest"  # image name, check build_docker.sh for details
container_name="facogen_sdd_gen_cont"  # default container name

while getopts "p:" opt
do
   case "$opt" in
      p ) port="$OPTARG" ;;
      n ) container_name="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Print helpFunction in case parameters are empty
if [ -z "$port" ]
then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

echo "Stopping docker container '${container_name}' if it is running"

if [ ! "$(docker ps -q -f name=$container_name)" ]; then
    if [ "$(docker ps -aq -f status=exited -f name=$container_name)" ]; then
        # cleanup
        docker stop "$container_name" || true
        docker rm "$container_name" || true
    fi
fi

echo "Check http://localhost:9080/api/ws/ to access facogen SSD gen api. Use username and password admin/admin to login"

docker run --rm -ti \
      --name "$container_name" \
      -p $port:9080 \
      "$image_name" bash
