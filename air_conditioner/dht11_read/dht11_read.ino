#include <DHT11.h>
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
  float temp, humi;
  if((err=dht11.read(humi, temp))==0)
  {
    Serial.print("temperature:");
    Serial.print(temp);
    Serial.print(" humidity:");
    Serial.print(humi);
    Serial.println();
  }
  else
  {
    Serial.println();
    Serial.print("Error No :");
    Serial.print(err);
    Serial.println();    
  }
  delay(DHT11_RETRY_DELAY); //delay for reread

  int illu = analogRead(A0);
  int dis1 = analogRead(A1);
  int dis2 = analogRead(A2);
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



