#include "DHT11.h"
#include "Maxbotix.h"


Maxbotix rangeSensorPW(8, Maxbotix::PW, Maxbotix::LV);
#ifdef MAXBOTIX_WITH_SOFTWARE_SERIAL
  Maxbotix rangeSensorTX(6, Maxbotix::TX, Maxbotix::LV);
#endif
Maxbotix rangeSensorAD(A3, Maxbotix::AN, Maxbotix::LV);

int pin=4;
DHT11 dht11(pin); 
void setup()
{
   Serial.begin(9600);
  while (!Serial) {
      ; // wait for serial port to connect. Needed for Leonardo only
    }
  Serial1.begin(9600);
}

void loop()
{
  
  char buffer[1024];
  int err;
  byte tempHi, humiHi,tempLo, humiLo;
  if((err=dht11.read(humiHi,humiLo, tempHi,tempLo))==0)
  {
    Serial.print("temperature:");
    Serial.print(tempHi);
    Serial.print(" humidity:");
    Serial.print(humiHi);
    Serial.println();
  }
  else
  {
    Serial.println();
    Serial.print("Error No :");
    Serial.print(err);
    Serial.println();    
  }
  delay(5000); //delay for reread

  int illu = analogRead(A0);
  int dis1 = analogRead(A1);
  int dis2 = analogRead(A2);
  int pw = rangeSensorPW.getRange();
  int ad = rangeSensorAD.getRange();

  Serial1.write((byte)(illu / 256));
  Serial.print((byte)(illu/256));
  Serial1.write((byte)(illu % 256));
  Serial1.write((byte)(dis1 / 256));
  Serial1.write((byte)(dis1 % 256));
  Serial1.write((byte)(dis2 / 256));
  Serial1.write((byte)(dis2 % 256));
  Serial1.write((byte)(pw / 256));
  Serial1.write((byte)(pw % 256));
  Serial1.write((byte)(ad / 256));
  Serial1.write((byte)(ad % 2560));
  Serial1.write(tempHi);
  Serial1.write(tempLo);
  Serial1.write(humiHi);
  Serial1.write(humiLo);

  Serial.print("illu : ");
  Serial.print(illu);
  Serial.print(" dis1 : ");
  Serial.print(dis1);
  Serial.print(" dis2 : ");
  Serial.print(dis2);
  Serial.print(" pw : ");
  Serial.print(pw);
  Serial.print(" ad : ");
  Serial.print(ad);
  
  if(Serial1.available() > 0) {
    Serial.print("Received : ");

    while(Serial1.available()) {
      char o = Serial1.read();
      Serial.print(o);
      delay(10);
    }

    Serial.println();
  }

  

  if(Serial.available() > 0) {
    int i = 0;
    Serial.print("Sent : ");

    while(Serial.available()) {
      char o = Serial.read();
      Serial.print(o);
      buffer[i] = o;
      ++i;
      delay(10);
    }

    for(int j=0;j!=i;++j) {
      Serial1.write(buffer[j]);
    }

    Serial.println();
  }
}



