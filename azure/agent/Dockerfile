FROM ubuntu:24.04
ENV DEBIAN_FRONTEND=noninteractive AGENT_ALLOW_RUNASROOT=1 JAVA_HOME_21_X64=/usr/lib/jvm/java-21-openjdk-amd64 JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates curl git jq unzip zip openssl docker.io openjdk-21-jdk python3 libicu74 libssl3t64 libkrb5-3 zlib1g && rm -rf /var/lib/apt/lists/*
RUN curl -sL https://aka.ms/InstallAzureCLIDeb -o /tmp/install-azure-cli.sh && bash /tmp/install-azure-cli.sh && rm /tmp/install-azure-cli.sh
WORKDIR /azp
ARG AGENT_URL=https://download.agent.dev.azure.com/agent/5.281.1/vsts-agent-linux-x64-5.281.1.tar.gz
ARG AGENT_SHA256=c9579f76fd71a013e3b9010155454e20592d935134e4fcd04adc5283a820167e
RUN curl -fLsS "$AGENT_URL" -o /tmp/agent.tar.gz && echo "$AGENT_SHA256  /tmp/agent.tar.gz" | sha256sum -c - && tar -xzf /tmp/agent.tar.gz && rm /tmp/agent.tar.gz && ./bin/installdependencies.sh
COPY start.sh /start.sh
RUN chmod +x /start.sh
ENTRYPOINT ["/start.sh"]
