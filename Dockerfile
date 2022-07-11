FROM ubuntu:22.04
COPY . /ats
WORKDIR /ats
RUN apt update && apt -y install curl zip unzip && ln -sf /bin/bash /bin/sh && curl -s "https://get.sdkman.io" | bash
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk install java 11.0.2-open && sdk install sbt && sbt clean playUpdateSecret dist
