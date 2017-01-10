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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import timber.log.Timber;

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
        Timber.plant(new Timber.DebugTree());
        PeripheralManagerService manager = new PeripheralManagerService();

        try {
            gpio = manager.openGpio("IO13");
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            gpio.setActiveType(Gpio.ACTIVE_HIGH);

            handleIntent(getIntent());
        } catch (IOException e) {
            Timber.w(e, "Unable to access GPIO");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (gpio == null) {
            return;
        }
        boolean lightsOn = intent.getBooleanExtra("lightsOn", false);
        if (lightsOn) {
            toggle.run();
        } else {
            handler.removeCallbacks(toggle);
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
