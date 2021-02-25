# Original plugin 
https://www.npmjs.com/package/cordova-honeywell-scanner-simplified

I have just copied all the code from above source and added extra properties as per my need. Original plugin was not ale to scan EAN-13 and some more. I enabled almost everyting.

**Tested on CT60 9.0**

**Works with Capacitor and React project as well**

**So all credit goes to the original one.**

Install
-------

Ionic: `ionic cordova plugin add cordova-honeywell-plugin`

Cordova: `cordova plugin add cordova-honeywell-plugin`

Capacitor: `npm i cordova-honeywell-plugin` 

Usage & implementation exactly like original one
-----

Call `.listen` to capture scans using the device's physical buttons. Call `.scan` within your application to enable a "software" triggerd scan. You can disable the capturing by calling `.release` and enable it back by calling `.claim` followed by `.listen` method. You can also simulate a softare button to enable the reader behaving in the same way as the hardware scan button(s).

TIP: In Ionic, in order to access the `window` property, you may need to add `window: any = window` just above your constructor.

### Enable listener
```javascript
window.plugins.honeywell.listen(function(data) {
  console.log('Scanned: ' + data);
}, function (error) => {
  console.log('Error occured: ' + error);
});
```

### Disable listener
```javascript
function disable() {
  window.plugins.honeywell.release();
}
```

### Re-enable listener after being disabled (.release)
```javascript
function enable() {
  window.plugins.honeywell.claim(function(){
    window.plugins.honeywell.listen(function(data) {
      console.log('Scanned: ' + data);
    }, function (error) => {
      console.log('Error occured: ' + error);
    });
  });
}
```
