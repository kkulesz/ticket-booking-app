FROM spikerlabs/scala-sbt:scala-2.13.2-sbt-1.3.13

WORKDIR /app

COPY build.sbt .
COPY project/Dependencies.scala ./project/
COPY project/build.properties ./project/
COPY src/ src/

RUN sbt compile
CMD ["sbt", "run"]