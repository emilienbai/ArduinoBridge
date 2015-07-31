#include <TimerOne.h>

#define   wdt_disable()

// Constants
#define nSensors 16
#define loopDelay 3
#define commandSize 4
#define debug false
#define sensorMax 1023
#define sensorMin 0
#define numReadings 5
#define compressor 300

#define calibrationCommand "cal"
#define pingCommand "ping"
#define noiseThresholdCommand "nthr"
#define timeThresholdCommand "tthr"
#define sensorNumberCommand "setnb"
#define calibrationTimeSet "caltm"
#define calibrationValue "calva"
#define resetCommand "rst"

void(*resetFunc)(void) = 0;

unsigned long lastUpdate = 0;
unsigned long timeout = 40000; //watchdog reset after 40s


byte Pins[] = {A0,A1,A2,A3,A4,A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15};
//initial time threshold
int itt = 200;
//initial noise threshold
int iNt = 100;
int calibrationTime = 1000;
int timeThreshold[nSensors] ={itt, itt, itt, itt, itt, itt, itt, itt, itt, itt, itt, itt, itt, itt, itt, itt};
int noiseThreshold[nSensors]={iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt, iNt};
int lastValues[nSensors]={0};
int maxNoise[nSensors]={0};
int playing[nSensors]={false};
int nulled[nSensors]={true};
int displayLogs = false;

String input="";
String commands[commandSize];
int c;
boolean commandComplete = false;
int activeSensorNumber;
int count; //reading counter

void longWDT(void)
{
  if((millis()-lastUpdate)>timeout)
  {
    sei();

    Timer1.detachInterrupt();

    Serial.println("*rst");
    Serial.flush();

    resetFunc();
  }
}

void setup() {
  Serial.begin(230400);  // Such serial, many wow
  input.reserve(200);
  input="";
  c=0;
  activeSensorNumber = nSensors;
  int i;
  count = 0;
  Timer1.initialize(1000000); //wake up every second
  Timer1.attachInterrupt(longWDT);
}

void listenSerial(){
  while (Serial.available()) {
     // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the input String:
    if(inChar == ' '){  // Split arguments
        commands[c] = input;
        input = "";
        c++;
    }
    else if(inChar=='\n'){  // Ship
      commandComplete = true;
    }
    else{
      input += inChar;
    }
  }
  commands[c] = input;
}

void printBackCommand(){
  Serial.println("-Printing command back :");
  for(int i=0;i<commandSize;++i){
    if(commands[i] != ""){
      Serial.println(commands[i]);
    }
  } 
}

void resetCommands(){
  for(int i=0;i<commandSize;++i){
    if(commands[i] != ""){
      commands[i] = "";
    }
  }
}

// Commands
void ping(){
    Serial.println("-pong");
}

void setNoiseBarrier(int idx, int nv){
    noiseThreshold[idx] = nv;
    String s="-";
    s.concat(idx);
    s+=" : Seuil ajusté";
    Serial.println(s);
}

void setNoiseBarrierAll(int nv){
  Serial.println("-Réglage de tous les seuils");
  for(int i = 0; i<nSensors; i++){
    setNoiseBarrier(i, nv);
  }
  Serial.println("-Seuils réglés pour toutes les entrées");
}

void setTimeBarrier(int idx, int nv){
    timeThreshold[idx] = nv;
    String s = "-";
    s.concat(idx);
    s+=" Stabilisation réglée";
    Serial.println(s);
}

void setTimeBarrierAll(int nv){
  Serial.println("-Réglage de tous les temps de stabilisation");
  for (int i = 0; i<nSensors; i++){
    setTimeBarrier(i, nv);
  }
  Serial.println("-Stabilisation réglée pour tous");
}

void setSensorNumber (int nv){
  String s = "-Nouveau nombre d'entrées actives :";
  s.concat(nv);
  Serial.println(s);
  activeSensorNumber = nv;
}


void calibrateOne(int idx){
  String s = "-Calibration de : ";
  s.concat(idx);
  Serial.println(s);
  int i=0;
  int smax=0;
  for(;i<calibrationTime;i++){
    int newValue = analogRead(Pins[idx]);
    if(newValue > smax){
     smax = newValue; 
    }
    delay(1);
  }
  s="-Calibration terminée, max :";
  s.concat(smax);
  maxNoise[idx] = smax;
  Serial.println(s);
  s = ",";
  s.concat(idx);
  s += ",";
  s.concat(smax);
  s +=",";
  Serial.println(s);  
}

void calibrateAll(){
  Serial.println("-Calibration de tous les capteurs");
  for(int i=0;i<nSensors;++i){
    calibrateOne(i);
  }
  Serial.println("-Calibration effectuée");
}

void setCalibrationValue (int idx, int nv){
  maxNoise[idx] = nv;
}

void setCalibrationTime(int newTime){
  String s = "-Nouveau temps de Calibration : ";
  s.concat(newTime);
  s+= " s";
  Serial.println(s);
  calibrationTime = newTime*1000;  
}

void pushReset() 
{ 
  asm volatile (" jmp 0");
}


void loop() {
  // Listen for instructions
  listenSerial();
  // at the end of the command
  if(commandComplete){
    commandComplete = false;
    c=0;
    input="";
    
    if(debug){
      printBackCommand();
    }
    
    // Ping
    if(commands[0] == pingCommand){
     ping(); 
    }
    //noise threshold form : nthr 500 2
    else if(commands[0] == noiseThresholdCommand){
      if(commands[2] != "" && commands[2].toInt() < nSensors){
        setNoiseBarrier(commands[2].toInt(), commands[1].toInt());
      }
      else{
        setNoiseBarrierAll(commands[1].toInt());
      }
      
    }
    // time cooldown form : tthr 300 4
    else if(commands[0] == timeThresholdCommand){
      if(commands[2] != "" && commands[2].toInt() < nSensors){
        setTimeBarrier(commands[2].toInt(), commands[1].toInt());
      }
      else{
        setTimeBarrierAll(commands[1].toInt());
      }
    }
    // Calibration
    else if(commands[0] == calibrationCommand){
       // Calibrating only one sensor, by id
      if(commands[1] != "" && commands[1].toInt() < nSensors){
        calibrateOne(commands[1].toInt());
      }
      // Or launching general calibration
      else{
        calibrateAll();
      } 
    }
    else if(commands[0] == sensorNumberCommand){
      if(commands[1] != "" && commands[1].toInt() <= nSensors){
        setSensorNumber(commands[1].toInt());
      }
    }
    else if(commands[0] == calibrationTimeSet){
      setCalibrationTime(commands[1].toInt());
    }
    else if (commands[0] == calibrationValue){
      setCalibrationValue(commands[2].toInt(), commands[1].toInt());
    }
    else if(commands[0] == resetCommand){
      pushReset();
    }
    
    resetCommands(); //Empty the command Line
  }
  else{
    // Check sensors
    // -------------
    String signal="";
    for (int x = 0; x < activeSensorNumber; ++x){  // For each sensor
      // Read new value (center and normalize)
      //int max =0;
      int total = 0;
      for(count = 0; count<numReadings; count++)
      {
        total = total + analogRead(Pins[x]);
      }
      total = total/numReadings;
      int newValue = max((map(total, maxNoise[x], sensorMax, sensorMin, sensorMax)), 0);
      
      // Check for threshold
        int timeDiff = millis() - playing[x];
      if(newValue >= noiseThreshold[x] && timeDiff >= timeThreshold[x]){
        if(abs(newValue-lastValues[x])>compressor)
        {
          lastValues[x]=newValue;
          newValue = ((double) newValue)*0.7;
        } else 
        {
          lastValues[x]=newValue;
        }
        signal.concat(x);
        signal+="-";
        signal.concat(newValue);
        signal+="-";
        playing[x] = millis();
        nulled[x] = false;
      }
      else if (newValue < noiseThreshold[x] && timeDiff > timeThreshold[x] && !nulled[x]){
        signal.concat(x);
        signal+="-0-";
        lastValues[x] = 0;
        nulled[x]=true;        
      }
    }
    
    if(!signal.equals("")){
      signal+="\n";
      Serial.print(signal);
      Serial.flush();
      lastUpdate = millis();
    }
    // -------------
  }
  
  delay(loopDelay);        // delay in between reads for stability
}
