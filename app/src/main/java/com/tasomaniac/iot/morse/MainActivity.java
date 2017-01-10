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

import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import timber.log.Timber;

public class MainActivity extends Activity {

    private BlinkingLed blinkingLed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());
        try {
            blinkingLed = BlinkingLed.create(new PeripheralManagerService(), new Handler());
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to the Led", e);
        }
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean lightsOn = intent.getBooleanExtra("lightsOn", false);
        blinkingLed.setBlinking(lightsOn);
    }

    @Override
    protected void onDestroy() {
        blinkingLed.destroy();
        super.onDestroy();
    }

}
