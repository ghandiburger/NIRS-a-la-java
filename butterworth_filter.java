public class butterworth_filter {

  // nquist = 1/TR/2;
  // meanHB = butterfilt(mean(data(:,goods),2), 
  //                      4, 
  //                      [.6 min(2,nquist)], <- an array containing .6 and 5
  //                      1/TR, 
  //                      'br');
  // function timeSeries = butterfilt(timeSeries,
  //                                    fpower,
  //                                    cutfreq,
  //                                    sf,
  //                                    ftype)
  // TR = 0.1
  // sf = 1/TR <- 10
  // freqN = .5*sf; <- 5
  // [b,a] = butter(fpower, <- 4
  //                  cutfreq/freqN, <- an array containing .6/5 and 5/5
  //                  'stop');
  // [B,A] = BUTTER(N,Wn,'stop') is a bandstop filter if Wn = [W1 W2].
  
  public void go(int fpower, double[] Wn, String varargin) {
  
    // NOT ANALOG
    int fs = 2;
    double[] u = new double[Wn.length];
    for (int i=0; i<Wn.length; i++)
    {
      u[i] = (2*fs*Math.tan((Math.PI*Wn[i])/fs));
    }

    // CONVERT TO LOW-PASS PROTOTYPE ESTIMATE
    // BANDSTOP
    double Bw = u[1] - u[0];
    double Wn_center_frequency = Math.sqrt(u[0]*u[1]);
    
    // GET N-TH ORDER BUTTERWORTH ANALOG LOWPASS PROTOTYPE
    System.out.println(Bw);
    System.out.println(Wn_center_frequency);
  }
  
  public static void main(String[] args) {
    butterworth_filter _butterworth_filter = new butterworth_filter();
    double[] Wn = new double[2];
    Wn[0] = .6;
    Wn[1] = 5;
    _butterworth_filter.go(4, Wn, "stop");
  }

}