import ddf.minim.analysis.*;
import ddf.minim.*;

Minim       minim;
AudioPlayer jingle;
FFT         fft;

void setup()
{
  size(512, 200, P3D);
  
  minim = new Minim(this);
  
  // specify that we want the audio buffers of the AudioPlayer
  // to be 1024 samples long because our FFT needs to have 
  // a power-of-two buffer size and this is a good size.
  jingle = minim.loadFile("jasonelrod_snoring.aif", 1024);
  
  // loop the file indefinitely
  jingle.loop();
  
  // create an FFT object that has a time-domain buffer 
  // the same size as jingle's sample buffer
  // note that this needs to be a power of two 
  // and that it means the size of the spectrum will be half as large.
  fft = new FFT( jingle.bufferSize(), jingle.sampleRate() );
  println("BufferSize:"+jingle.bufferSize()+" SampleRate:"+jingle.sampleRate() );
  println("fft analysis  start");
  int now = millis();

  int trackLength = jingle.length();//length in millis
  int trackSeconds= (int)trackLength/1000; //length in seconds
  int specSize    = fft.specSize();  //how many fft bands
  float[][] fftData = new float[trackSeconds][specSize];//store fft bands here, for each time step
  for(int t = 0 ; t < trackSeconds; t++){//time step in seconds
    fft.forward(jingle.mix);//analyse fft
    for(int b = 0; b < specSize; b++){//loop through bands
      fftData[t][b] = fft.getBand(b);//store each band
      //println("timestep:"+t+" band:"+b);
    }
  } 

  println("fft analysis end, took: " + (millis()-now) + " ms");
  
}

void draw()
{
  background(0);
  stroke(255);
  
  // perform a forward FFT on the samples in jingle's mix buffer,
  // which contains the mix of both the left and right channels of the file
  fft.forward( jingle.mix );
  
  for(int i = 0; i < fft.specSize(); i++)
  {
    // draw the line for frequency band i, scaling it up a bit so we can see it
    line( i, height, i, height - fft.getBand(i)*100 );
  }
}
