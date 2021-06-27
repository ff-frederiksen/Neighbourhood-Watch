//////////////////////////////////////////////// LMIC and common setup ///////////////////////////////////////////////////////////////////////////

#include <EEPROM.h>
#include <Wire.h> 

int cal_cnt = 0;
int refDist = -1;
int distCnt = 0;
int pwFlag = 0;
int panicFlag = 0;
int armFlag = 0;
int alarmFlag = 0;

#include <lmic.h>
// MIT License
// https://github.com/gonzalocasas/arduino-uno-dragino-lorawan/blob/master/LICENSE
// Based on examples from https://github.com/matthijskooijman/arduino-lmic
// Copyright (c) 2015 Thomas Telkamp and Matthijs Kooijman


#include <hal/hal.h>


// Initialise short and long payloads. The last byte is dead and is never received.
uint8_t shortMessage[] = {1, 1, 1, 99};
uint8_t longMessage[] = {1, 1, 1, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 99}; // unused by sensor nodes

int t_start = 0;
int t_wait = 10000; // time in ms to wait until sensors activate to guarantee correct calibration

/////////////////////////////////////// LMIC and common setup /////////////////////
// This EUI must be in little-endian format, so least-significant-byte
// first. When copying an EUI from ttnctl output, this means to reverse
// the bytes. For TTN issued EUIs the last bytes should be 0xD5, 0xB3,
// 0x70.
static const u1_t PROGMEM APPEUI[8]={ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
void os_getArtEui (u1_t* buf) { memcpy_P(buf, APPEUI, 8);}

// This should also be in little endian format, see above.
//static const u1_t PROGMEM DEVEUI[8]={ 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
static const u1_t PROGMEM DEVEUI[8]={ 0x6d, 0x83, 0xea, 0x8e, 0x1a, 0x35, 0x5c, 0x3e };
void os_getDevEui (u1_t* buf) { memcpy_P(buf, DEVEUI, 8);}

// This key should be in big endian format (or, since it is not really a
// number but a block of memory, endianness does not really apply). In
// practice, a key taken from ttnctl can be copied as-is.
// The key shown here is the semtech default key.
//static const u1_t PROGMEM APPKEY[16] = { 0x2B, 0x7E, 0x15, 0x16, 0x28, 0xAE, 0xD2, 0xA6, 0xAB, 0xF7, 0x15, 0x88, 0x09, 0xCF, 0x4F, 0x3C };
static const u1_t PROGMEM APPKEY[16] = { 0x54, 0x5a, 0x02, 0x37, 0xee, 0xda, 0xd2, 0xa5, 0xad, 0xfd, 0x97, 0x24, 0xe9, 0xd9, 0x1f, 0x62 };
void os_getDevKey (u1_t* buf) {  memcpy_P(buf, APPKEY, 16);}

static uint8_t mydata[] = "Hello, world!";
static osjob_t sendjob;


// Delay in seconds between TX ending and starting new TX
int TX_delay = 15;

// Pin mapping for using the Dragino Shield
const lmic_pinmap lmic_pins = {
    .nss = 10,
    .rxtx = LMIC_UNUSED_PIN,
    .rst = 9,
    .dio = {2, 6, 7},
};

void onEvent (ev_t ev) {
    Serial.print("ev: ");
    Serial.println(ev);
    switch(ev) {
      case EV_TXCOMPLETE:
        Serial.println("TX + RX windows complete");
        Serial.println(LMIC.seqnoUp);
        if (LMIC.dataLen) {
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

            armFlag = 1;
          }
          else if (LMIC.frame[LMIC.dataBeg+0] == 1) {
            armFlag = 0;
          }

          if (LMIC.frame[LMIC.dataBeg+1] == 1) {
            panicFlag = 0;
          }
            
        }   
        eepromUpdate();
        // Set time for next TX
        os_setTimedCallback(&sendjob, os_getTime()+sec2osticks(TX_delay), do_send);
        break;
      default:
        Serial.print("ev: ");
        Serial.println(ev);
        break;
    }
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

void eepromUpdate() {
  // Convert frame counters up and down to two byte each so they can be stored in EEPROM
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
}

//////////////////////////////////////////////// LMIC and common setup ///////////////////////////////////////////////////////////////////////////

int Sensor = 12;
int LED = 5;

void setup(){
  Serial.begin(115200);
  Serial.println("booting");

  pinMode (Sensor, INPUT);
  pinMode (LED, OUTPUT);
  
  t_start = millis();

  // Initialise LMIC
  os_init();
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

  // Send first message
  do_send(&sendjob);

/////////// Device specific setup here ///////////

} 

void loop(){
  os_runloop_once();

  
 if ( millis() > t_start + t_wait) {

     ////////////////// Sensor specific loop code here //////////////////

    int motion_value =  digitalRead(Sensor);
    Serial.println(motion_value);
    digitalWrite(LED, motion_value);
    if (motion_value && armFlag) {
      Serial.println("ALARM!");
      alarmFlag = 1;
    }
    if (not armFlag) {
      alarmFlag = 0;
    }
    delay(100);
  }
  //////////////////////////////////////////////////////////////////// 
}
