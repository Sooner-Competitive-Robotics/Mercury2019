#include "RobotLib.h"
#include <Wire.h>

Motor motorA;
Motor motorB;

int number = 0;

void setup() {
    Wire.begin(0x8);                // join i2c bus with address #8
    Wire.onReceive(receiveEvent); // register event

    motorA.begin(4,5,3);
    motorB.begin(7,8,6);
}

void loop() {
    delay(100);

}

void receiveEvent(int howMany) {
    while(Wire.available()) {
        number = Wire.read();
        Serial.print("data received: ");
        Serial.println((number - 128) / 128.0);

        float motorOutput = (number - 128) / 128.0;
        motorA.output(motorOutput);
        motorB.output(motorOutput);
    }
}