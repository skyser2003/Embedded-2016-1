void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial1.begin(9600);
}

void loop() {
  char buffer[1024];
  
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
