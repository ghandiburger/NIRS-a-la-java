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
      // see readers/new_participant_data.java
      new_participant_data curr_data = new new_participant_data();
      curr_data.read_participant_data(z, directory, baseline);
    
      totalRows = totalRows + curr_data.data_matrix.size();
      totalColumns = totalColumns + curr_data.data_matrix.get(0).size();
      
      // determine the number of rows that will be output from mes2hb
      int outputSize = curr_data.data_matrix.size() - baseline[1];
      
      // make arrays to hold deoxy, oxy, and totalHb from mes2hb
      double[][] deoxyChannels = new double[outputSize][numChannels];
      double[][] oxyChannels = new double[outputSize][numChannels];
      double[][] totalHb = new double[outputSize][numChannels];
      
      for (int i=0; i<numChannels; i++) {
        // 2D array list to hold participant data for each channel
        ArrayList<ArrayList<Double>> channel = new ArrayList<ArrayList<Double>>();
        if (i < numChannels/2) {
          for (int j=0; j<curr_data.data_matrix.size(); j++) {
            // add a new row to channel
            channel.add(new ArrayList<Double>());
            // grab participant data for the channels
            // 690 wavelength
            channel.get(j).add(curr_data.data_matrix.get(j).get(i+pairOffset));
            // 830 wavelength
            channel.get(j).add(curr_data.data_matrix.get(j).get(i));
          }
        } else {
          for (int j=0; j<curr_data.data_matrix.size(); j++) {
            // add a new row to channel
            channel.add(new ArrayList<Double>());
            // grab participant data for the channels
            // 690 wavelength
            channel.get(j).add(curr_data.data_matrix.get(j).get(i+chOffset+pairOffset));
            // 830 wavelength
            channel.get(j).add(curr_data.data_matrix.get(j).get(i+chOffset));
          }
        }
        
        // call Xu Cui's mes2hb conversion function, passing channel arraylist, wavelength array, and baseline array
        // see mes2hb.java
        mes2hb thing = new mes2hb();
        thing.mes2hb_go(channel, wavelength, baseline);
        
        // mes2hb_go object contains 3 arraylists for oxy, deoxy, and totalHb data
        // mes2hb operates on 1 channel at a time, so these arrays hold the channel that was just converted by mes2hb
        Double [] temp_mes2hb_hb = thing.mes2hb_hb.toArray(new Double[thing.mes2hb_hb.size()]);
        Double [] temp_mes2hb_hbo = thing.mes2hb_hbo.toArray(new Double[thing.mes2hb_hbo.size()]);
        Double [] temp_mes2hb_hbt = thing.mes2hb_hbt.toArray(new Double[thing.mes2hb_hbt.size()]);
        
        // put the deoxy values in the appropriate column by current channel
        for (int a=0; a<temp_mes2hb_hb.length; a++) {
          deoxyChannels[a][i] = temp_mes2hb_hb[a];
        }
        // put the oxy values in the appropriate column by current channel
        for (int a=0; a<temp_mes2hb_hbo.length; a++) {
          oxyChannels[a][i] = temp_mes2hb_hbo[a];
        }
        // put the total values in the appropriate column by current channel
        for (int a=0; a<temp_mes2hb_hbt.length; a++) {
          totalHb[a][i] = temp_mes2hb_hbt[a];
        }
      }
      
      // write the deoxy, oxy, and totalHb data to spearate .csv files
      // see readers/data_writer.java
      data_writer write = new data_writer();
      write.write_data(deoxyChannels, oxyChannels, totalHb, z, directory);
      
      // see reformat_data.java for details on this section
      double[][] reformatted_deoxyChannels = new double[outputSize][numChannels];
      double[][] reformatted_oxyChannels = new double[outputSize][numChannels];
      
      // OXY
      reformat_data oxy_reformat_data = new reformat_data();
      oxy_reformat_data.read_log_data(z, directory);
      oxy_reformat_data.calculate_condition_locations();
      oxy_reformat_data.grab_conditions(oxyChannels);
      oxy_reformat_data.write_data("oxy", z, "E3/pre-normalized/");
      oxy_reformat_data.normalize_trials();
      oxy_reformat_data.write_data("oxy", z, "E3/post-normalized/");
      
      // DEOXY
      reformat_data deoxy_reformat_data = new reformat_data();
      deoxy_reformat_data.read_log_data(z, directory);
      deoxy_reformat_data.calculate_condition_locations();
      deoxy_reformat_data.grab_conditions(deoxyChannels);
      deoxy_reformat_data.write_data("deoxy", z, "E3/pre-normalized/");
      deoxy_reformat_data.normalize_trials();
      deoxy_reformat_data.write_data("deoxy", z, "E3/post-normalized/");
    }
  
  }

  public static void main(String[] args) {
    // this junk is just to time the function
    // and I was curious about how many numbers the program was actually working with
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