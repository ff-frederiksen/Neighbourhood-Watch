/*******************************************************************************
 * Copyright (c) 2015 Thomas Telkamp and Matthijs Kooijman
 *
 * Permission is hereby granted, free of charge, to anyone
 * obtaining a copy of this document and accompanying files,
 * to do whatever they want with them without any restriction,
 * including, but not limited to, copying, modification and redistribution.
 * NO WARRANTY OF ANY KIND IS PROVIDED.
 *
 * This example sends a valid LoRaWAN packet with payload "Hello,
 * world!", using frequency and encryption settings matching those of
 * the The Things Network.
 *
 * This uses OTAA (Over-the-air activation), where where a DevEUI and
 * application key is configured, which are used in an over-the-air
 * activation procedure where a DevAddr and session keys are
 * assigned/generated for use with all further communication.
 *
 * Note: LoRaWAN per sub-band duty-cycle limitation is enforced (1% in
 * g1, 0.1% in g2), but not the TTN fair usage policy (which is probably
 * violated by this sketch when left running for longer)!

 * To use this sketch, first register your application and device with
 * the things network, to set or generate an AppEUI, DevEUI and AppKey.
 * Multiple devices can use the same AppEUI, but each device has its own
 * DevEUI and AppKey.
 *
 * Do not forget to define the radio type correctly in config.h.
 *
 *******************************************************************************/

#include <lmic.h>
#include <hal/hal.h>
#include <SPI.h>
#include <EEPROM.h>
#include <Wire.h>
#include <LiquidCrystal.h>
#include <Keypad.h>
#include "SipHash_2_4.h"


//////////////////////////////////////////////// Sensor setup ///////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////// Alarm Control Panel setup ///////////////////////////////////////////////////////////////////////////

#include <LiquidCrystal_I2C.h>
#include <Keypad.h>
#include "SipHash_2_4.h"

uint8_t hashKey[] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x00, 0x00};

const byte ROWS = 4;
const byte COLS = 4;

char hexaKeys[ROWS][COLS] = {
  {'1', '2', '3', 'A'},
  {'4', '5', '6', 'B'},
  {'7', '8', '9', 'C'},
  {'*', '0', '#', 'D'}
};

byte rowPins[ROWS] = {22, 24, 26, 28};
byte colPins[COLS] = {30, 32, 34, 36};

char keypadList[] = {'x','x', 'x', 'x'}; 
int keypadCnt = 0;

Keypad keypad = Keypad(makeKeymap(hexaKeys), rowPins, colPins, ROWS, COLS);

LiquidCrystal_I2C lcd(0x27, 1, 2);  

 

/////////////////////////////////////// LMIC and common setup /////////////////////
// This EUI must be in little-endian format, so least-significant-byte
// first. When copying an EUI from ttnctl output, this means to reverse
// the bytes. For TTN issued EUIs the last bytes should be 0xD5, 0xB3,
// 0x70.
static const u1_t PROGMEM APPEUI[8]={ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
void os_getArtEui (u1_t* buf) { memcpy_P(buf, APPEUI, 8);}

// This should also be in little endian format, see above.
//static const u1_t PROGMEM DEVEUI[8]={ 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
static const u1_t PROGMEM DEVEUI[8]={0xe1, 0xc8, 0x13, 0xc6, 0xa5, 0x76, 0x89, 0xa1};
void os_getDevEui (u1_t* buf) { memcpy_P(buf, DEVEUI, 8);}

// This key should be in big endian format (or, since it is not really a
// number but a block of memory, endianness does not really apply). In
// practice, a key taken from ttnctl can be copied as-is.
// The key shown here is the semtech default key.
//static const u1_t PROGMEM APPKEY[16] = { 0x2B, 0x7E, 0x15, 0x16, 0x28, 0xAE, 0xD2, 0xA6, 0xAB, 0xF7, 0x15, 0x88, 0x09, 0xCF, 0x4F, 0x3C };
static const u1_t PROGMEM APPKEY[16] = {0xfc, 0xf8, 0xb3, 0x7c, 0x1b, 0x35, 0x2d, 0xb2, 0x6d, 0x4a, 0x80, 0xf5, 0xc5, 0x3c, 0x76, 0xfb};
void os_getDevKey (u1_t* buf) {  memcpy_P(buf, APPKEY, 16);}

static uint8_t mydata[] = "Hello, world!";
static osjob_t sendjob;

// Schedule TX every this many seconds (might become longer due to duty
// cycle limitations).
const unsigned TX_INTERVAL = 30;

// Pin mapping for using the Dragino Shield
const lmic_pinmap lmic_pins = {
    .nss = 10,
    .rxtx = LMIC_UNUSED_PIN,
    .rst = 9,
    .dio = {2, 6, 7},
};

int cal_cnt = 0;
int refDist = -1;
int distCnt = 0;
int pwFlag = 0;
int panicFlag = 0;
int armFlag = 0;
int alarmFlag = 0;

// Initialise short and long payloads. The last byte is dead and is never received.
uint8_t shortMessage[] = {1, 1, 1, 99};
uint8_t longMessage[] = {1, 1, 1, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 99}; // unused by sensor nodes

int t_start = 0;
int t_wait = 10000; // time in ms to wait until sensors activate to guarantee correct calibration



void onEvent (ev_t ev) {
    Serial.print(os_getTime());
    Serial.print(": ");
    switch(ev) {
        case EV_SCAN_TIMEOUT:
            Serial.println(F("EV_SCAN_TIMEOUT"));
            break;
        case EV_BEACON_FOUND:
            Serial.println(F("EV_BEACON_FOUND"));
            break;
        case EV_BEACON_MISSED:
            Serial.println(F("EV_BEACON_MISSED"));
            break;
        case EV_BEACON_TRACKED:
            Serial.println(F("EV_BEACON_TRACKED"));
            break;
        case EV_JOINING:
            Serial.println(F("EV_JOINING"));
            break;
        case EV_JOINED:
            Serial.println(F("EV_JOINED"));

            // Disable link check validation (automatically enabled
            // during join, but not supported by TTN at this time).
            LMIC_setLinkCheckMode(0);
            break;
        case EV_RFU1:
            Serial.println(F("EV_RFU1"));
            break;
        case EV_JOIN_FAILED:
            Serial.println(F("EV_JOIN_FAILED"));
            break;
        case EV_REJOIN_FAILED:
            Serial.println(F("EV_REJOIN_FAILED"));
            break;
            break;
        case EV_TXCOMPLETE:
            Serial.println(F("EV_TXCOMPLETE (includes waiting for RX windows)"));
            if (LMIC.txrxFlags & TXRX_ACK)
              Serial.println(F("Received ack"));
            if (LMIC.dataLen) {
              //Serial.println(F("Received "));
              //Serial.println(LMIC.dataLen);
              //Serial.println(F(" bytes of payload"));
              Serial.print(F("Data received - "));
              Serial.print("frame: ");
              Serial.write(LMIC.frame, 2);
              Serial.print(", dataBeg: ");
              Serial.print(LMIC.dataBeg);
              Serial.print(", len: ");
              Serial.println(LMIC.dataLen);
              Serial.print("all: ");
              Serial.write(LMIC.frame + LMIC.dataBeg, LMIC.dataLen);
              Serial.println();
              Serial.println(LMIC.frame[LMIC.dataBeg+0]);
              Serial.println(LMIC.frame[LMIC.dataBeg+1]);
              Serial.println(LMIC.frame[LMIC.dataBeg+2]);
          
              // The value 0 introduces weird errors when using TTN. For payload 1 is false and 2 is true.
              if (LMIC.frame[LMIC.dataBeg+0] == 2) {

                if (armFlag == 0) {
                  lcd.setCursor(0,1);
                  lcd.print("Armed           ");

                  // Calibrate sensor
                }
                armFlag = 1;
              }
              else if (LMIC.frame[LMIC.dataBeg+0] == 1) {
                    if (armFlag == 1) {
                  lcd.setCursor(0,1);
                  lcd.print("Disarmed        ");
                }
                armFlag = 0;
              }

              if (LMIC.frame[LMIC.dataBeg+1] == 1) {
              panicFlag = 0;
              }
            }
            eepromUpdate();
            // Schedule next transmission
            os_setTimedCallback(&sendjob, os_getTime()+sec2osticks(TX_INTERVAL), do_send);
            break;
        case EV_LOST_TSYNC:
            Serial.println(F("EV_LOST_TSYNC"));
            break;
        case EV_RESET:
            Serial.println(F("EV_RESET"));
            break;
        case EV_RXCOMPLETE:
            // data received in ping slot
            Serial.println(F("EV_RXCOMPLETE"));
            break;
        case EV_LINK_DEAD:
            Serial.println(F("EV_LINK_DEAD"));
            break;
        case EV_LINK_ALIVE:
            Serial.println(F("EV_LINK_ALIVE"));
            break;
         default:
            Serial.println(F("Unknown event"));
            break;
    }
}

void eepromUpdate() {
  // Convert frame counters up and down to two byte each so they can be stored in EEPROM
  Serial.print("eeprom start");
  byte upHigh = (LMIC.seqnoUp & 0xFF00) / 256;
  byte upLow = LMIC.seqnoUp & 0x00FF;
  byte dnHigh = (LMIC.seqnoDn & 0xFF00) / 256;
  byte dnLow = LMIC.seqnoDn & 0x00FF;
  EEPROM.write(0, upHigh);
  EEPROM.write(1, upLow);
  EEPROM.write(2, dnHigh);
  EEPROM.write(3, dnLow);
  Serial.println("EEPROM:");
  Serial.println(upHigh);
  Serial.println(upLow);
  Serial.println(dnHigh);
  Serial.println(dnLow);
  Serial.print("eeprom end");
}

void do_send(osjob_t* j){
    // Update uplink TX payload
    
    // Update iff there isn't currently a TX happening
    if (!(LMIC.opmode & OP_TXRXPEND)) {
        // The value 0 introduces weird errors when using TTN. For payload 1 is false and 2 is true.
        
        // Arm status
        shortMessage[0] = armFlag + 1;
        longMessage[0] = armFlag + 1;

        // Panic status
        shortMessage[1] = panicFlag + 1;
        longMessage[1] = panicFlag + 1;

        // Alarm status
        shortMessage[2] = alarmFlag + 1;
        longMessage[2] = alarmFlag + 1;
        
        // Set payload message to include hash of the entered password, if '#' has been pressed on the keypad
        if (pwFlag == 1) {
          LMIC_setTxData2(1, longMessage, sizeof(longMessage)-1, 0);
          pwFlag = 0;
        }
        // Otherwise just use the short payload format to reduce airtime
        else {
          LMIC_setTxData2(1, shortMessage, sizeof(shortMessage)-1, 0);
        }
        Serial.println("Sending uplink TX");
    }
}

int Contact = 12;
void setup() {
    Serial.begin(115200);
    Serial.println("");
    Serial.println(F("Starting"));

    pinMode(A0, INPUT);
    pinMode(A1, INPUT);
    pinMode(Contact, INPUT);
  
    t_start = millis();

    // LMIC init
    os_init();
    // Reset the MAC state. Session and pending data transfers will be discarded.
    LMIC_reset();

    // This is increased from 1/100 to avoid losing downlink payload
    LMIC_setClockError(MAX_CLOCK_ERROR * 10 / 100); 

    // Downlink datarate. TTN uses SF9 for the RX2 window
    LMIC.dn2Dr = DR_SF9;

    // Uplink datarate is set to SF7 to reduce airtime as much as possible
    LMIC_setDrTxpow(DR_SF12,14);


    // Read frame counters from non-volatile memory
    byte eeprom0 = EEPROM.read(0);
    byte eeprom1 = EEPROM.read(1);
    byte eeprom2 = EEPROM.read(2);
    byte eeprom3 = EEPROM.read(3);

    // Update frame counters up and down iff EEPROM hasn't been reset
    if (eeprom0 != 255 && eeprom1 != 255 && eeprom2 != 255 && eeprom3 != 255) {
      Serial.println("Reading frame counters from EEPROM");
      LMIC.seqnoUp = eeprom0*256 + eeprom1;
      LMIC.seqnoDn = eeprom2*256 + eeprom3;
    }    
    
    // Start job (sending automatically starts OTAA too)
    do_send(&sendjob);

 /////////// Device specific setup here ///////////

    // Initialise lcd
    lcd.init();
    lcd.backlight();
    lcd.setCursor(0,0);
    lcd.print("test");
    //lcdReset();
    Serial.println("Test is displayed?");

    // Initialise keypad
    keypad.addEventListener(keypadPress);
   
}



void loop() {
    os_runloop_once();
    if ( millis() > t_start + t_wait) {
  
      ////////////////// Sensor specific loop code here //////////////////

   int contact_value = digitalRead(Contact);
    
    if (contact_value != 1 && armFlag == 1) {
      Serial.println("ALARM!");
      alarmFlag = 1;
    }
    

  }
    ////////////////////////////////////////////////////////////////////
    
    // Use keypad
    char customKey = keypad.getKey();  
  
    // Use panic button 
    if (panicButton() && panicFlag == 0) {
      panicFlag = 1;
          
      // Write to lcd
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("Panic mode");
      lcd.setCursor(0,1);
      lcd.print("Activated");
    }
}


////////////////// Alarm Control Panel functions for LCD and keypad //////////////////

void lcdReset() {
    // Reset lcd
    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("Enter pw:        ");
    lcd.setCursor(0,1);
    lcd.print("Disarmed         ");
}

void keypadPress(KeypadEvent kpp) {
    switch(keypad.getState()) {
      case PRESSED:
      char kpInput = kpp;
      lcd.setCursor(14, 2); 
      lcd.print(kpInput);
      Serial.print(keypadCnt);
      Serial.print(", ");
      Serial.println(kpInput);
  
      switch(kpInput) {
        case '*':
          // Delete entered password
          keypadCnt = 0;
          lcd.setCursor(9,0);
          lcd.print("       ");
          break;
        case '#':
          // Submit password
          if (keypadCnt == 4) {
            hashKey[14] = (byte) EEPROM.read(0);
            hashKey[15] = (byte) EEPROM.read(1);
            sipHash.initFromRAM(hashKey);
  
            Serial.println("Keypad input as int: ");
            
            for (int i=0; i<4;i++) {
              sipHash.updateHash((byte)keypadList[i]); // update hash with each byte of msg
              Serial.println((byte)keypadList[i]);
            }
            sipHash.finish();
            for(int i=0; i<16;i++) {
              longMessage[i+3] = sipHash.result[i];
            }
            
            pwFlag = 1;
  
            // Write to lcd
            lcd.setCursor(0,1);
            lcd.print("Waiting for ack.");
  
            // reset entered password
            keypadCnt = 0;
            lcd.setCursor(9,0);
            lcd.print("       ");
          }
          break;
        case 'A':
          break;
        case 'B':
          break;
        case 'C':
          break;
        case 'D':
          break;
        case '0' ... '9':
          if (keypadCnt <= 3) {
            keypadList[keypadCnt] = kpInput;
            lcd.setCursor(9+keypadCnt,0);
            lcd.print('*');
            keypadCnt++;
          }
          break;
      }
    }
}

bool panicButton() {
  if (analogRead(1) > 900) {
    Serial.println("Panic button pressed");
    return true;
  }
  else {
    return false;
  }
}


////////////////// Sensor specific non-loop code here //////////////////
 
