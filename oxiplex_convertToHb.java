import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import readers.*;

public class oxiplex_convertToHb {

  // INIT VARS - do not change unless oxiplex configuration changed
  static int[] baseline = new int[2]; // start and stop indices of baseline measurement
  static int leftProbe = 0; // offset (num cols) to first DC col (left probe)
  static int markerCol = 31; // column index of marker recording
  static int numChannels = 8; // DC channels
  static int chOffset = numChannels/2;
  static int pairOffset = 4; // column separation between ~690/830 pairs
  static int rightProbe = 39; // offset (num cols) to first DC col (right probe)
  static double samplingRate = 0.16; // seconds
  static int[] wavelength = new int[2];
  static String directory = "E3/";
  
  public int totalRows = 0;
  public int totalColumns = 0;
  
  public void go() {
  
    wavelength[0] = 690;
    wavelength[1] = 830;
  
    baseline[0] = 1;
    baseline[1] = 2;
  
    // EXPERIMENT-SPECIFIC VARS - set before running script
    int baselineDuration = 30;
    int numParticipants = 1;
    
    for (int z=1; z<(numParticipants+1); z++) {
      // calculate baseline end index
      double temp = Math.ceil(baselineDuration/samplingRate);
      int baseline_calc = (int) (temp);
      baseline[1] = baseline[0] + baseline_calc;
      
      // read in participant data
      new_participant_data curr_data = new new_participant_data();
      curr_data.read_participant_data(z, directory, baseline);
    
      totalRows = totalRows + curr_data.data_matrix.size();
      totalColumns = totalColumns + curr_data.data_matrix.get(0).size();
      
      int outputSize = curr_data.data_matrix.size() - baseline[1];

      double[][] deoxyChannels = new double[outputSize][numChannels];
      double[][] oxyChannels = new double[outputSize][numChannels];
      double[][] totalHb = new double[outputSize][numChannels];
      
      for (int i=0; i<numChannels; i++) {
        //System.out.println("@@@@@@@@@@     channel " + i + " started     @@@@@@@@@@");
        ArrayList<ArrayList<Double>> channel = new ArrayList<ArrayList<Double>>();
        if (i < numChannels/2) {
          for (int j=0; j<curr_data.data_matrix.size(); j++) {
            channel.add(new ArrayList<Double>());
            channel.get(j).add(curr_data.data_matrix.get(j).get(i+pairOffset));
            channel.get(j).add(curr_data.data_matrix.get(j).get(i));
            //System.out.println("Channel: " + i + "   Row: " + j + "   Column: " + (i+pairOffset) + "   curr_data: " + curr_data.data_matrix.get(j).get(i+pairOffset) + "   channel: " + channel.get(j).get(0));
            //System.out.println("Channel: " + i + "   Row: " + j + "   Column: " + (i) + "   curr_data: " + curr_data.data_matrix.get(j).get(i) + "   channel: " + channel.get(j).get(1));
          }
          //System.out.println("##########     channel " + i + " finished     ##########");
        } else {
          for (int j=0; j<curr_data.data_matrix.size(); j++) {
            channel.add(new ArrayList<Double>());
            channel.get(j).add(curr_data.data_matrix.get(j).get(i+chOffset+pairOffset));
            channel.get(j).add(curr_data.data_matrix.get(j).get(i+chOffset));
            //System.out.println("Channel: " + i + "   Row: " + j + "   Column: " + (i+chOffset+pairOffset) + "   curr_data: " + curr_data.data_matrix.get(j).get(i+chOffset+pairOffset) + "   channel: " + channel.get(j).get(0));
            //System.out.println("Channel: " + i + "   Row: " + j + "   Column: " + (i+chOffset) + "   curr_data: " + curr_data.data_matrix.get(j).get(i+chOffset) + "   channel: " + channel.get(j).get(1));
          }
          //System.out.println("##########     channel " + i + " finished     ##########");
        }
        // Xu Cui's mes2hb conversion function
        //System.out.println("**********     mes2hb running     **********");
        mes2hb thing = new mes2hb();
        thing.mes2hb_go(channel, wavelength, baseline);
        
        Double [] temp_mes2hb_hb = thing.mes2hb_hb.toArray(new Double[thing.mes2hb_hb.size()]);
        Double [] temp_mes2hb_hbo = thing.mes2hb_hbo.toArray(new Double[thing.mes2hb_hbo.size()]);
        Double [] temp_mes2hb_hbt = thing.mes2hb_hbt.toArray(new Double[thing.mes2hb_hbt.size()]);
        
        //System.out.println("***** deoxyChannels[][] *****");
        for (int a=0; a<temp_mes2hb_hb.length; a++) {
          deoxyChannels[a][i] = temp_mes2hb_hb[a];
          //System.out.println(deoxyChannels[a][i]);
        }
        //System.out.println("***** oxyChannels[][] *****");
        for (int a=0; a<temp_mes2hb_hbo.length; a++) {
          oxyChannels[a][i] = temp_mes2hb_hbo[a];
          //System.out.println(oxyChannels[a][i]);
        }
        //System.out.println("***** totalHb[][] *****");
        for (int a=0; a<temp_mes2hb_hbt.length; a++) {
          totalHb[a][i] = temp_mes2hb_hbt[a];
          //System.out.println(totalHb[a][i]);
        }
      }
      
      data_writer write_that_shit = new data_writer();
      write_that_shit.write_data(deoxyChannels, oxyChannels, totalHb, z, directory);
      // System.out.println(curr_data.data_matrix.get(0).get(0));
      
      reformat_data _reformat_data = new reformat_data();
      _reformat_data.read_log_data(z, directory);
      _reformat_data.calculate_condition_locations();
      _reformat_data.grab_conditions(oxyChannels);
      _reformat_data.normalize_trials();
    }
  
  }

  public static void main(String[] args) {
    final long startTime = System.nanoTime();
    final long endTime;
    final int total;
    oxiplex_convertToHb _oxiplex_convertToHb = new oxiplex_convertToHb();
    try {
      _oxiplex_convertToHb.go();
    } finally {
      endTime = System.nanoTime();
      total = _oxiplex_convertToHb.totalRows * _oxiplex_convertToHb.totalColumns;
    }
    final long duration = endTime - startTime;
    System.out.println("Elapsed Time: " + duration + " nanoseconds");
    System.out.println("Numbers crunched: " + total);
  }
}