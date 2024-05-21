#!/bin/bash
image_name="ssd_gen:latest"

echo "Building image: ${image_name}"
docker build -t $image_name .
