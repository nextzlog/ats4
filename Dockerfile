FROM openjdk:17-oracle AS build
COPY . /ats
WORKDIR /ats
RUN microdnf install findutils zip unzip
RUN curl -s https://get.sdkman.io | bash
RUN source $HOME/.sdkman/bin/sdkman-init.sh && sdk install sbt && sbt clean playUpdateSecret stage
FROM openjdk:17
COPY --from=build /ats/target/universal/stage /ats
WORKDIR /ats
