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

# run the app in background
java -jar target/scala-2.13/app.jar &

sleep_for=10
echo "Sleeping for ${sleep_for} seconds in order to wait for app to start properly"
sleep "${sleep_for}"
echo "Starting..."
#################################################################################
screening_time="2023-09-22T12:30:00"
resp1=$(curl -s --request GET \
  --url "http://localhost:8081/screenings?time=${screening_time}")
echo -e "\nPoint nr 2 response:"
echo "$resp1" | jq '.' --color-output 


screening_id=$(echo "$resp1" | jq -r '.[0].id')
resp2=$(curl -s --request GET \
  --url "http://localhost:8081/screenings/${screening_id}")
echo -e "\nPoint nr 4 response:"
echo "$resp2" | jq --color-output

seat1=$(echo "$resp2" | jq '.freeSeats[0]')
seat2=$(echo "$resp2" | jq '.freeSeats[1]')
resp3=$(curl -s --request POST \
  --url http://localhost:8081/screenings \
  --header 'Content-Type: application/json' \
  --data "{
	\"name\": \"Konrad\",
	\"surname\": \"Kulesza\",
	\"screeningId\": \"$screening_id\",
	\"seats\": [
		{
			\"seat\": $seat1,
			\"ticketType\": \"Adult\"
		},
		{
			\"seat\": $seat2,
			\"ticketType\": \"Child\"
		}
	]
}")
echo -e "\nPoint nr 6 response:"
echo "$resp3" | jq '.' --color-output

echo -e "\nDemo finished."

