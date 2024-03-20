#!/bin/bash

#
# Copyright 2024 Broadcom. All rights reserved.
# SPDX-License-Identifier: Apache-2.0
#

gfsh -e "run --file=@samples-dir@/boot/configuration/build/resources/main/geode/bin/start-secure-cluster.gfsh"
