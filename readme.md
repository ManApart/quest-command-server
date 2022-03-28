# Quest Command Server

Build and push the jar from quest command to local maven. Make sure to use WSL or windows maven cache appropriately. If the jar doesn't show up you probably used WSL and then tried an intellij pointed at a windows cache.

Example calls
```
curl localhost:8080/history/0?start=1
curl -X POST localhost:8080/command/0?start=2 -d 'test and stuff' 
```