#!/bin/bash

###############################################################################
# This file is used to install some of the required files for the smartgridz
# applications
###############################################################################

# The location of these files must be kept in sync with the SystemDefaults.java
# file.

INSTALL_PATH="/opt/smartgridz"
CONFIG_PATH="$INSTALL_PATH/config"

CONFIG_FILE="$CONFIG_PATH/config.properties"
PRODUCT_UUID="$CONFIG_PATH/uuid"

function install() {
    # Create the directories if they do not exist.
    sudo mkdir $INSTALL_PATH
    sudo chmod a+rw $INSTALL_PATH
    sudo mkdir $CONFIG_PATH
    sudo chmod a+rw $CONFIG_PATH

    # Create the config file if it does not exist.
    if [[ ! -e $CONFIG_FILE ]]
    then
        sudo touch $CONFIG_FILE
        sudo chmod a+rw $CONFIG_FILE
    fi

    # Create the UUID for the product if it does not exisst.
    if [[ ! -e $PRODUCT_UUID ]]
    then
        sudo uuidgen > $PRODUCT_UUID
        sudo chmod a+rw $PRODUCT_UUID
    fi
}

install
