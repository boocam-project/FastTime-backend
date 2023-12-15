#!/usr/bin/env bash
echo " __
|  |--.-----.-----.----.---.-.--------.
|  _  |  _  |  _  |  __|  _  |        |
|_____|_____|_____|____|___._|__|__|__|
"

echo "update application profiles from boocam-be-vaults..."
git submodule init
git submodule update --remote

echo "done!"

echo "remove old application profiles"
rm ./src/main/resources/application.yaml
rm ./src/main/resources/application-*.yaml

echo "copy application profiles to application resources path"
cp ./boocam-be-vaults/profiles/* ./src/main/resources
