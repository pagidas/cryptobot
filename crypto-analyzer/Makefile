SHELL=bash

CURRENT_DIR=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))

build-docker:
	@(cd ${CURRENT_DIR}/ && docker build -t="crypto-analyzer" .)