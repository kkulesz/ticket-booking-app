# ticket-booking-app

1. To run application: `./run_app.sh` - starts postgres and runs app. Postgres runs on `5434!` port and app on `8081`
1. To run demo: `./run_app_demo.sh` - runs basic business scenario making some curl calls and printing responses


### Some assumption I made and other project decisions
1. Each room is a rectangle (each row has the same number of seats)
1. Seats in each row are indexed from 0
1. Validating time before screening is commented out for demo reasons.
1. When queried for screenings, system responses with all screenings within margin (T-30, T+30)

### api
- GET `/screenings?time={RRRR-MM-DDTHH:mm:SS}` - list all screenings for given time T within margin (T-30, T+30)
- GET `/screenings/{UUID}` - get detailed information for specified `id`
- POST `/screenings` - reserve ticket, body:
```
{
	"name": string,
	"surname": string,
	"screeningId": uuid,
	"seats": [
		{
			"seat": [int, int],
			"ticketType": "Adult"|"Child"|"Student"
		}
	]
}
```

### Stack used
- Scala 2.13
- sbt
- docker (with compose)
- PosgreSQL
- ZIO
- Doobie
- devcontainers / Visual Studio Code
