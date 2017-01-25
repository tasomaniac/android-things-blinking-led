package com.tasomaniac.iot.morse;

import android.os.Handler;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

import timber.log.Timber;

class BlinkingLed {

    private static final String LED_PORT = "IO13";

    private final Gpio gpio;
    private final Handler handler;

    private boolean active;

    static BlinkingLed create(PeripheralManagerService manager, Handler handler)
            throws IOException {
        Gpio gpio = manager.openGpio(LED_PORT);
        gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        gpio.setActiveType(Gpio.ACTIVE_HIGH);
        return new BlinkingLed(gpio, handler);
    }

    private BlinkingLed(Gpio gpio, Handler handler) {
        this.gpio = gpio;
        this.handler = handler;
    }

    void setBlinking(boolean value) {
        if (value) {
            repeatCommand.run();
        } else {
            setActive(false);
            handler.removeCallbacks(repeatCommand);
        }
    }

    private final Runnable repeatCommand = new Runnable() {
        @Override
        public void run() {
            toggle();
            handler.postDelayed(repeatCommand, 1000);
        }
    };

    private void toggle() {
        setActive(!active);
    }

    private void setActive(boolean active) {
        try {
            gpio.setValue(active);
            this.active = active;
        } catch (IOException e) {
            Timber.w(e, "Unable to change the light status");
        }
    }

    public void destroy() {
        try {
            gpio.setValue(false);
            gpio.close();
        } catch (IOException e) {
            Timber.w(e, "Unable to close GPIO");
        }
    }
}
