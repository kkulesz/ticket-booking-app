sbt assembly

# start postgres
docker compose down --remove-orphans # reset tables' state
docker compose up --build -d

# kill the app if it is already running, so its ports is free
running_app_pid=`ps -aux | grep "java -jar target/scala-2.13/app.jar" | grep -v grep | awk '{print $2}'`
if [[ "" !=  "$running_app_pid" ]]; then
  echo "Killing previous api process ${running_app_pid}..."
  kill -9 $running_app_pid
fi

java -jar target/scala-2.13/app.jar 