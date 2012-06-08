package readers;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class data_writer {

  public void write_data(double[][] deoxy, double[][] oxy, double[][] totalHb, int participant_num, String directory) {
    double[][] read_deoxy = new double[deoxy.length][deoxy[0].length];
    double[][] read_oxy = new double[oxy.length][oxy[0].length];
    double[][] read_totalHb = new double[totalHb.length][totalHb[0].length];
    
    for (int i=0; i<deoxy.length; i++) {
      for (int j=0; j<deoxy[0].length; j++) {
        read_deoxy[i][j] = deoxy[i][j];
      }
    }
    for (int i=0; i<oxy.length; i++) {
      for (int j=0; j<oxy[0].length; j++) {
        read_oxy[i][j] = oxy[i][j];
      }
    }
    for (int i=0; i<totalHb.length; i++) {
      for (int j=0; j<totalHb[0].length; j++) {
        read_totalHb[i][j] = totalHb[i][j];
      }
    }
    
    String deoxy_output_file = directory + "S" + participant_num + "_deoxy.csv";
    String oxy_output_file = directory + "S" + participant_num + "_oxy.csv";
    String totalHb_output_file = directory + "S" + participant_num + "_total.csv";
    
    
    try {
      CsvWriter deoxy_output = new CsvWriter(new FileWriter(deoxy_output_file, true), ',');
      CsvWriter oxy_output = new CsvWriter(new FileWriter(oxy_output_file, true), ',');
      CsvWriter totalHb_output = new CsvWriter(new FileWriter(totalHb_output_file, true), ',');
      
      for (int i=0; i<read_deoxy.length; i++) {
        for (int j=0; j<read_deoxy[0].length; j++) {
          String temp = Double.toString(read_deoxy[i][j]);
          deoxy_output.write(temp);
        }
        deoxy_output.endRecord();
      }
      for (int i=0; i<read_oxy.length; i++) {
        for (int j=0; j<read_oxy[0].length; j++) {
          String temp = Double.toString(read_oxy[i][j]);
          oxy_output.write(temp);
        }
        oxy_output.endRecord();
      }
      for (int i=0; i<read_totalHb.length; i++) {
        for (int j=0; j<read_totalHb[0].length; j++) {
          String temp = Double.toString(read_totalHb[i][j]);
          totalHb_output.write(temp);
        }
        totalHb_output.endRecord();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}