#!/bin/bash

set -euo pipefail

function installTravisTools {
  mkdir -p ~/.local
  curl -sSL https://github.com/SonarSource/travis-utils/tarball/v48 | tar zx --strip-components 1 -C ~/.local
  source ~/.local/bin/install
}

installTravisTools
source ~/.local/bin/installMaven35

export DEPLOY_PULL_REQUEST=true
regular_mvn_build_deploy_analyze

./check-license-compliance.sh
