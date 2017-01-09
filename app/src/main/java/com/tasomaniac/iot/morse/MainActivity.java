/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tasomaniac.iot.morse;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import timber.log.Timber;

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 */
public class MainActivity extends Activity {

    private Gpio gpio;
    private boolean active;
    private final Handler handler = new Handler();
    private final Runnable toggle = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(toggle, 1000);
            toggle();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PeripheralManagerService manager = new PeripheralManagerService();

        try {
            gpio = manager.openGpio("IO13");
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            gpio.setActiveType(Gpio.ACTIVE_HIGH);

            toggle.run();
        } catch (IOException e) {
            Timber.w(e, "Unable to access GPIO");
        }
    }

    private void toggle() {
        active = !active;
        try {
            gpio.setValue(active);
        } catch (IOException e) {
            Timber.w(e, "Unable to toggle");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (gpio != null) {
            try {
                gpio.setValue(false);
                gpio.close();
                gpio = null;
            } catch (IOException e) {
                Timber.w(e, "Unable to close GPIO");
            }
        }
    }
}
