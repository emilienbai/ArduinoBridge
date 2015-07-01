const int sensorNumber = 16; //Number of analog input used
int cycles=150;  //debounce time
int threshold=300; //threshold for the sensor value
int val;  //value of the analog input
int i;  //counter
boolean active[sensorNumber];  //Is the specified sensor active
int timer_sensor[sensorNumber];  //Timer for each sensor  
boolean dataExist = false;

void setup()
{
  for(i=0; i<sensorNumber ; i++){  //initialize data in tables    
    active[i] = false;
    timer_sensor[i] = 0;
  }
  Serial.begin(115200);    //initialize port speed       
}

void loop()
{
  for(i=0; i<sensorNumber; i++){ // for each sensor
    val = analogRead(i);  //the analog input is read
    if(val>threshold){    //if the input signal is over the threshold
      if(active[i]==false){  //and the sensor has debounced
        active[i]=true;      //the sensor is activated
        Serial.print(i);
        Serial.print("-");  //it prints data on serial
        Serial.print(val);
        Serial.print("-");
        dataExist = true;        
        timer_sensor[i]=0; //timer goes back to zero
      }
      else{ 
        timer_sensor[i]=timer_sensor[i]+1;//if not debounced, timer is incremented
        if(timer_sensor[i]>cycles){  //if now debounced
          active[i]=false;  //  sensor is deactivated
        }         
      }
    }
    else if(active[i]==true){  //if the sensor has not debounced
      timer_sensor[i]=timer_sensor[i]+1;  //timer is incremented
      if(timer_sensor[i]>cycles){  //if now debounced
        active[i]=false;  //sensor is deactivated
      }         
    }
    if(dataExist){
      Serial.println("");
      dataExist = false;
    }
  }  
}




