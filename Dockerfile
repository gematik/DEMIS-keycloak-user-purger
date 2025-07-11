# Declare Source Digest for the Base Image
ARG SOURCE_DIGEST=774e11e01fc289479c0f26f525c816ebdc7582cb7bd69a7467707a991dff7095
FROM gematik1/osadl-alpine-openjdk21-jre:1.0.0@sha256:${SOURCE_DIGEST}

# Redeclare Source Digest to be used in the build context
# https://docs.docker.com/engine/reference/builder/#understand-how-arg-and-from-interact
ARG SOURCE_DIGEST=774e11e01fc289479c0f26f525c816ebdc7582cb7bd69a7467707a991dff7095

# The STOPSIGNAL instruction sets the system call signal that will be sent to the container to exit
# SIGTERM = 15 - https://de.wikipedia.org/wiki/Signal_(Unix)
STOPSIGNAL SIGTERM

# Defining Healthcheck
HEALTHCHECK --interval=15s \
            --timeout=10s \
            --start-period=30s \
            --retries=3 \
            CMD ["/bin/pidof", "java"]

# Default USERID and GROUPID
ARG USERID=10000
ARG GROUPID=10000

# Run as User (not root)
USER $USERID:$USERID

COPY --chown=$USERID:$GROUPID target/keycloak-user-purger.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Git Args
ARG COMMIT_HASH
ARG VERSION

###########################
# Labels
###########################
LABEL de.gematik.vendor="gematik GmbH" \
      maintainer="software-development@gematik.de" \
      de.gematik.app="DEMIS Keycloak-User-Purger" \
      de.gematik.git-repo-name="https://gitlab.prod.ccs.gematik.solutions/git/demis/services/keycloak-user-purger" \
      de.gematik.commit-sha=$COMMIT_HASH \
      de.gematik.version=$VERSION \
      de.gematik.source.digest=$SOURCE_DIGEST
