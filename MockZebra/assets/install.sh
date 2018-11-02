#!/bin/bash

strIndex()
{
    x="${1%%$2*}"
    [[ "$x" = "$1" ]] && echo -1 || echo "${#x}"
}

tryGetMockZebraInstalledPath()
{
    echo "[*] Finding MockZebra already installed path ..."
    MOCKZEBRA_INSTALLED_PATH=$(ls -lh /usr/local/bin | grep MockZebra | awk '{print $(NF-1), $NF}' | tail -1)
    [[ !  -z  $MOCKZEBRA_INSTALLED_PATH  ]] && echo $MOCKZEBRA_INSTALLED_PATH || echo "Not found."
}

tryRemoveInstalledMockZebra()
{
    echo "[*] Removing already installed MockZebra ..."
    sudo rm -f /usr/local/bin/MockZebra

    FIRST_INDEX_OF_SLASH=$(strIndex "${MOCKZEBRA_INSTALLED_PATH}" "/")
    RUN_FILE="${MOCKZEBRA_INSTALLED_PATH##*/}"
    LAST_INDEX_OF_SLASH=$(strIndex "${MOCKZEBRA_INSTALLED_PATH}" "/${RUN_FILE}")

    FOLDER=$(echo "${MOCKZEBRA_INSTALLED_PATH}" | cut -c $FIRST_INDEX_OF_SLASH-$LAST_INDEX_OF_SLASH)
    echo $FOLDER
    sudo rm -Rf $FOLDER
}

install()
{
    echo "[*] Installing new version of MockZebra ... "
    sudo ln -s $PWD/MockZebra.run /usr/local/bin/MockZebra && sudo chmod +x /usr/local/bin/MockZebra && echo "Installed successfully. \o/"
    exit 0
}

tryGetMockZebraInstalledPath
[[ ! -z  $MOCKZEBRA_INSTALLED_PATH  ]] && tryRemoveInstalledMockZebra

echo "[*] Checking whether java is installed? ..."
if type -p java; then
    echo "Found java executable in PATH."
    JAVA_EXECUTABLE=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "Found java executable in JAVA_HOME."
    JAVA_EXECUTABLE="$JAVA_HOME/bin/java"
else
    echo "You will need Java installed on your system!"
    exit -1
fi

echo "[*] Checking whether java version is 1.8+ ? ..."
if [[ "$JAVA_EXECUTABLE" ]]; then
    version=$("$JAVA_EXECUTABLE" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" > "1.8" ]]; then
        install
    else
        echo "Java version 1.8 or later required!"
        exit -1
    fi
fi
