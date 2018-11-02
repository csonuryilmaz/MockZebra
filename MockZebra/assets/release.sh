#!/bin/bash

VERSION="1.0.0.0"
RELEASE_PATH="../release/MockZebra-$VERSION"

if [ -d "$RELEASE_PATH" ]; then
  rm -Rf "$RELEASE_PATH"
  rm -f "$RELEASE_PATH.tar.gz"
fi
mkdir -p "$RELEASE_PATH"
echo "[*] Listing ../dist/ directory ... "
ls -lh ../dist/
echo "[*] Packaging ... "
cp -R ../dist/* "$RELEASE_PATH/"
cp ./MockZebra.run "$RELEASE_PATH/"
cat "$RELEASE_PATH/MockZebra.run" "$RELEASE_PATH/MockZebra.jar" > "$RELEASE_PATH/MockZebra.final" && chmod a+x "$RELEASE_PATH/MockZebra.final"
rm -f "$RELEASE_PATH/MockZebra.run"
rm -f "$RELEASE_PATH/MockZebra.jar"
mv "$RELEASE_PATH/MockZebra.final" "$RELEASE_PATH/MockZebra.run"
cp ./install.sh "$RELEASE_PATH/"
cp ./uninstall.sh "$RELEASE_PATH/"
cd "$RELEASE_PATH/../"
tar -zcvf "MockZebra-$VERSION.tar.gz" "MockZebra-$VERSION"
rm -Rf "MockZebra-$VERSION"
mkdir "MockZebra-$VERSION"
mv "MockZebra-$VERSION.tar.gz" "MockZebra-$VERSION/"
printf "[\xE2\x9C\x94] Release ready for distribution. \o/"
echo ""
# todo copy documentation vs.
