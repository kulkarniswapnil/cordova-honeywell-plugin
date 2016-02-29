# cordova-honeywell
Cordova Plugin to receive input from a Honeywell scanners (CT50, D75E, others?).

# Device Setup
- No special device setup is required.

# Usage
This plugin uses the `setKeepCallback` feature of the `PluginResult` so that you don't have to continually register to listen for scans. Wire up an scan event listener like this:

```javascript
plugins.honeywell.listenForScans(function(data) {
  // do something with 'data'
    console.log('You scanned: ' + data);
  }, function(error) {
    // do something with 'errir'
   console.log('Something went wrong: ' + error);
  });
```

Subsequent calls to `listenForScans` will replace the previously set callbacks.