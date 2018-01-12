# prX2jason

The JSON Validation Service (JVS) is a validator that allows every users to check JSON objects  

## Start  

1. Build image:  `./gradlew docker`
2. Run: `docker run -t --rm -p 80:80 validator:0.1`
3. Send json: `curl -s --upload-file new.json http://localhost/`

## Handling the Responses  

(JSON) ? OK : ERR

```
ERR 
{
 "errorCode"  : 12345,
 "errorMessage" : ["verbose, plain language description of the problem with hints about how to fix it]",
 "errorPlace" : ["highlight the point where error has occurred"],
 "resource"   : ["filename"],
 "request-id" : ["the request id generated by the API for easier tracking of errors"],
}
```
