# Akka HTTP microservice Authenthication

To run it:
 
 * Install cassandra and run it.
 * Install redis and run it (`redis-server` & then check with `redis-cli`).
 * `sbt run` 
 
 This will get your auth service up on port 9000.

The service has:

* User routes, to read, write and delete Users.
* Auth routes to login and signup
* Security authenthication token.

Uses redis to store sessions.

Uses cassandra to store users.


## Testing

No tests yet


Joaquin Bucca
