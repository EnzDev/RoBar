#include <ArduinoBLE.h>

enum Mode {
  TIME = 0xf, START = 0x1, STOP = 0x0
};
const int MULTIPLIER = 1000;

BLEService RobarService("19B10000-E8F2-537E-4F6C-D104768A1214");
BLECharacteristic RobarDataCharacteristic("19B10001-E8F2-537E-4F6C-D104768A1214", BLERead | BLEWrite, 5, 0);

// Reset the Control Pin states
void resetState() {
  for (int pin = 2; pin <= 5; pin++)
    digitalWrite(pin, LOW);
}

void setup() {
  // Connect Serial
  Serial.begin(9600);
  while (!Serial);

  // Init PINs and reset to LOW
  for (int pin = 2; pin <= 5; pin++)
    pinMode(pin, OUTPUT);
  resetState();

  // Start BLE
  if (!BLE.begin()) {
    Serial.println("starting BLE failed!");

    pinMode(LED_BUILTIN, OUTPUT);

    // Blink as a sign of error
    while (1) {
      digitalWrite(LED_BUILTIN, HIGH);
      delay(1000);
      digitalWrite(LED_BUILTIN, LOW);
      delay(1000);
    }
  }

  // Initialize Name, Service, and Characteristic
  BLE.setLocalName("RoBar :)");

  BLE.setAdvertisedService(RobarService);
  RobarService.addCharacteristic(RobarDataCharacteristic);
  BLE.addService(RobarService);

  // Start advertising
  BLE.advertise();
}

// Perform the requested action (open/close the relay)
void permform(int mode, int component, int serve_time) {
  resetState();
  if (mode == TIME  && serve_time > 0) {
    Serial.print("TIME ");
    Serial.println(serve_time);

    digitalWrite(component, HIGH);
    Serial.print(component);
    Serial.println(" : HIGH");

    delay(serve_time * MULTIPLIER);

    digitalWrite(component, LOW);
    Serial.print(component);
    Serial.println(" : LOW");
  }

  if (mode == START && serve_time > 0) {
    Serial.print("START ");
    Serial.print(serve_time);
    Serial.print("\t");
        
    digitalWrite(component, HIGH);
    Serial.print(component);
    Serial.println(" : HIGH");
  }

  if (mode == STOP && serve_time > 0) {
    Serial.print("STOP ");
    Serial.print(serve_time);
    Serial.print("\t");

    digitalWrite(component, LOW);
    Serial.print(component);
    Serial.println(" : LOW");
  }
}

/**
 *                      .
 *                    __|_
 *                   (o o))
 *                    |-- |
 *                   /==== \
 *                  o)  7 (o))
 *                   |_____|
 *    (o>            /__/__\
 *    /))           (--(oooo)
 * ====#========================
 *
 * Main Loop
 *  - Wait for a central to connect
 *  - Wait for the characteristic to be altered
 *  - Check 0xDEAD signal or perform signal
 *  - Wait for next event or disconnection
 */
void loop() {
  // listen for BLE peripherals to connect:
  BLEDevice central = BLE.central();

  //   ^_^        D:   ,------------- Mode (See enum)
  //          :p       |  ,---------- Rhum
  //  ;)   o_O         |  |  ,------- Coca-Cola
  //             :\    |  |  |  ,---- Ananas
  //   :o   `^`        |  |  |  |  ,- Orange
  byte robar_data[] = {0, 0, 0, 0, 0};

  // When a central is connected
  if (central) {
    Serial.print("Connected to central: ");
    // print the central's MAC address:
    Serial.println(central.address());

    // Exclusive access, another peripheral must leave to join
    while (central.connected()) {
      if (RobarDataCharacteristic.written()) {
        // Extract the characteristic's data
        int read = RobarDataCharacteristic.readValue(robar_data, 5);

        // When only 4 bytes read, check for the 0xDEAD signal
        if (read == 4) {
          if (robar_data[0] == 0xD && robar_data[1] == 0xE &&
              robar_data[2] == 0xA && robar_data[3] == 0xD) {
            Serial.println("Check passed");
          } else {
            Serial.println("Check FAILED");
          }
        }

        // When the 5 bytes are present
        if (read == 5) {
          // Get the mode
          int mode = robar_data[0];

          Serial.println("Serving...");

          // Perform the fill sequentially
          for (int component = 1; component <= 4; component++)
            permform(mode, component, (int) robar_data[component]);

          Serial.println("Done Serving !");
        }
      }
    }

    // When the central disconnects
    Serial.print(F("Disconnected from central: "));
    Serial.println(central.address());
  }
}
