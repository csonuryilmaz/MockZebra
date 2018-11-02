#!/bin/bash

echo "[*] Removing current installation of MockZebra ..."
sudo rm -f /usr/local/bin/MockZebra
echo "Checking ..."
if [ -f /usr/local/bin/MockZebra ] ; then
	echo "Can't be removed!"
	exit 1
fi
echo "Uninstalled successfully. \o/"
exit 0