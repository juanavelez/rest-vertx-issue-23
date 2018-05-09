# rest-vertx-issue-23

Reproducer for [issue 23 of rest.vertx](https://github.com/zandero/rest.vertx/issues/23)

The committed code registers a Proxy (pass-through) to the LoginController instance. 

If you register the loginController instance directly instead of the ```proxy``` instance,
you get a ```true``` response when using curl/postman to do a ```POST``` with header
```Content-Type: application/json``` to
```http://localhost:8980/user/login``` using the following ```Credentials``` (JSON payload):

```json
{
	"email": "a@b.com",
	"password": "changeme"
}
```

Leaving the code as is, when making the same call you get

```html
<html>
    <body>
        <h1>Resource not found</h1>
    </body>
</html>
```