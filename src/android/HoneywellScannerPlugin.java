package com.icsfl.rfsmart.honeywell;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;

public class HoneywellScannerPlugin extends CordovaPlugin implements BarcodeReader.BarcodeListener {
    private static final String TAG = "HoneywellScanner";

    private static BarcodeReader barcodeReader;
    private AidcManager manager;
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Context context = cordova.getActivity().getApplicationContext();
        AidcManager.create(context, new CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
                if(barcodeReader != null){
                                  Map<String, Object> properties = new HashMap<String, Object>();
                    // Set Symbologies On/Off
                    properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
                    properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_EAN_8_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
                    // Set Max Code 39 barcode length
                    properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
                    // Turn on center decoding
                    properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, false);
                    // Disable bad read response, handle in onFailureEvent
                    properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);

                    // Also send the EAN-13 and EAN-8 check digit within the payload.
                    properties.put(BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_EAN_8_CHECK_DIGIT_TRANSMIT_ENABLED, true);
                    properties.put(BarcodeReader.PROPERTY_UPC_A_CHECK_DIGIT_TRANSMIT_ENABLED, true);

                    // If this is not set to true, an EAN-13 starting with 00 will have the first zero removed.
                    properties.put(BarcodeReader.PROPERTY_UPC_A_TRANSLATE_EAN13, true);

                    // Apply the settings
                    barcodeReader.setProperties(properties);
                    barcodeReader.addBarcodeListener(HoneywellScannerPlugin.this);
                    try {
                        barcodeReader.claim();
                    } catch (ScannerUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("listenForScans")){
            this.callbackContext = callbackContext;
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
        return true;
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        if(this.callbackContext!=null)
        {
            PluginResult result = new PluginResult(PluginResult.Status.OK, barcodeReadEvent.getBarcodeData());
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        NotifyError("Scan failed");
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                NotifyError("Scanner unavailable");
            }
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager.close();
        }
    }

    private void NotifyError(String error){
        if(this.callbackContext!=null)
        {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, error);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }
}