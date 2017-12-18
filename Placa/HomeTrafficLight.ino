#ifdef ESP8266
#define Presencia_GPIO D2
#else
#define Presencia_GPIO 4
#endif
#include <Adafruit_NeoPixel.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

#define LED_GPIO LED_BUILTIN

#ifdef __AVR__
#include <avr/power.h>
#endif

//esta conectado al pinD2 que es equivalente al 0
#define PIN            D3

// Tenemos 8 pixels en nuestro led
#define NUMPIXELS     8

const char* ssid = "NOMBRE_RED";
const char* password = "PASS_RED";
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

int delayval = 500; // delay for half a second

bool val = 0;
bool old_val = 0;

void setup() {
  Serial.begin(115200);
  pinMode (Presencia_GPIO, INPUT);
  pinMode (LED_GPIO, OUTPUT);
  old_val = digitalRead(Presencia_GPIO);

  pixels.begin(); // This initializes the NeoPixel library.
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {

    delay(1000);
    Serial.print("Connecting..");

  }
  Serial.flush();
}

void loop() {
  apagarLed();
  if (WiFi.status() == WL_CONNECTED) {//Check WiFi connection status

    HTTPClient http;  //Declare an object of class HTTPClient


    http.begin( "http://158.49.245.82:8081/HomeTrafficLight/rest/moviles/");//Specify request destination
    int httpCode = http.GET();                                             //Send the request

    if (httpCode > 0) { //Check the returning code
      val = digitalRead(Presencia_GPIO);
      if (old_val != val) {
        if (val) {
          Serial.println("DETECTADO CUERPO EN MOVIMIENTO");
          String payload = http.getString();//Get the request response payload
          Serial.println(payload);                     //Print the response payload


          StaticJsonBuffer<1000> JSONBuffer;   //Memory pool
          JsonArray& array = JSONBuffer.parseArray(payload);
          int arraySize = array.size();   //get size of JSON Array
          Serial.print("\nSize of value array: ");
          Serial.println(arraySize);
          for (int i = 0; i < arraySize; i++) {
            int bateria = array[i]["bateria"];
            if(bateria>=50){
              encenderLedVerde(i);
            }
            if(bateria<50 && bateria >=20){
              encenderLedAmarillo(i);
            }
            if(bateria<20){
              encenderLedRojo(i);
            }
            Serial.println(bateria);

          }
                delay(5000);
        } else {
          Serial.println("TODO TRANQUILO");
          apagarLed();

        }

#ifdef ESP8266
        digitalWrite(LED_GPIO, !val); //lógica inversa del ESP8266
#else
        digitalWrite(LED_GPIO, val);
#endif
        old_val = val;
      }


    }
    http.end();   //Close connection

  }

}

void encenderLedAmarillo(int led) {
  for (int i = 2 * led; i <= 2 * led + 1; i++) {
    pixels.setPixelColor(i, pixels.Color(255, 255, 0)); // Amarillo
  pixels.show();
  delay(delayval);
  
  }
}

void encenderLedRojo(int led) {
  for (int i = 2 * led; i <= 2 * led + 1; i++) {
    pixels.setPixelColor(i, pixels.Color(150, 0, 0)); // ROJO
  pixels.show();
  delay(delayval);
  }
}


void encenderLedVerde(int led) {
  for (int i = 2 * led; i <= 2 * led + 1; i++) {
    pixels.setPixelColor(i, pixels.Color(0, 150, 0)); // VERDE
    pixels.show();
    delay(delayval);
  }
}

void apagarLed() {

  for (int i = 0; i < NUMPIXELS; i++) {

    // pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
    pixels.setPixelColor(i, pixels.Color(0, 0, 0)); // Moderately bright green color.

    pixels.show(); // This sends the updated pixel color to the hardware.

    delay(delayval); // Delay for a period of time (in milliseconds).

  }

}

