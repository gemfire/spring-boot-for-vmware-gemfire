#!/usr/bin/env bash

#if anything errors, bail.
set -e

sbdgPath="spring-boot-geode"

Clone() {
  # Clone SDG repo
  rm -rf "$sbdgPath"
  git clone https://github.com/spring-projects/spring-boot-data-geode.git $sbdgPath
}

Checkout() {
  # Checkout correct branch
  cd "$sbdgPath" || exit
  git checkout tags/$branch
}

CopyTestResources() {
  rm -rf $projectDir"/spring-gemfire-extensions/src/sbdg-test-read-only"
  rm -rf $projectDir"/spring-gemfire/src/sbdg-test-read-only"
  rm -rf $projectDir"/spring-gemfire-autoconfigure/src/sbdg-test-read-only"
  rm -rf $projectDir"/spring-gemfire-actuator/src/sbdg-test-read-only"
  rm -rf $projectDir"/spring-gemfire-actuator-autoconfigure/src/sbdg-test-read-only"
  rm -rf $projectDir"/spring-gemfire-starter-logging/src/sbdg-test-read-only"

  mkdir -p $projectDir"/spring-gemfire-extensions/src/sbdg-test-read-only"
  mkdir -p $projectDir"/spring-gemfire/src/sbdg-test-read-only"
  mkdir -p $projectDir"/spring-gemfire-autoconfigure/src/sbdg-test-read-only"
  mkdir -p $projectDir"/spring-gemfire-actuator/src/sbdg-test-read-only"
  mkdir -p $projectDir"/spring-gemfire-actuator-autoconfigure/src/sbdg-test-read-only"
  mkdir -p $projectDir"/spring-gemfire-starter-logging/src/sbdg-test-read-only"

  cp -R $sbdgPath"/apache-geode-extensions/src/test/" $projectDir"/spring-gemfire-extensions/src/sbdg-test-read-only"
  cp -R $sbdgPath"/spring-geode/src/test/" $projectDir"/spring-gemfire/src/sbdg-test-read-only"
  cp -R $sbdgPath"/spring-geode-autoconfigure/src/test/" $projectDir"/spring-gemfire-autoconfigure/src/sbdg-test-read-only"
  cp -R $sbdgPath"/spring-geode-actuator/src/test/" $projectDir"/spring-gemfire-actuator/src/sbdg-test-read-only"
  cp -R $sbdgPath"/spring-geode-starter-logging/src/test/" $projectDir"/spring-gemfire-starter-logging/src/sbdg-test-read-only"
  cp -R $sbdgPath"/spring-geode-actuator-autoconfigure/src/test/" $projectDir"/spring-gemfire-actuator-autoconfigure/src/sbdg-test-read-only"
}

RemoveClonedPath() {
  # Checkout correct branch
  cd  || exit
  rm -rf "$sbdgPath"
}

while [[ $# -gt 0 ]]; do
  case $1 in
  -l)
    sbdgPath="$2"
    shift # past argument
    shift # past value
    ;;
  -b)
    branch="$2"
    shift # past argument
    shift # past value
    ;;
  -t)
    projectDir="$2"
    shift # past argument
    shift # past value
    ;;
  -h)
    Help
    exit
    ;;
  -* | --*)
    echo "Unknown option $1"
    exit 1
    ;;
  esac
done

Clone
Checkout
CopyTestResources
RemoveClonedPath
