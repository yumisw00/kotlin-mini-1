#!/usr/bin/env bash

set -e

echo "==============================="
echo "Compile Kotlin RPG OOP"
echo "==============================="

kotlinc Main.kt -include-runtime -d rpg.jar

echo ""
echo "==============================="
echo "Menjalankan program..."
echo "==============================="

java -jar rpg.jar