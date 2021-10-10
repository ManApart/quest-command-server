# Quest Command Server

In intellij go to `quest-command-server.main` module and add a dependency on module `quest-command.main`

Example calls
```
curl localhost:8080/history/0?start=1
curl -X POST localhost:8080/command/0?start=2 -d 'test and stuff' 
```