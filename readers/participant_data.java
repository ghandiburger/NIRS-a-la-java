package readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class participant_data {
  
  public ArrayList<ArrayList<Double>> data_matrix = new ArrayList<ArrayList<Double>>(); //init 2D arraylist
  public int numOfRows;
  
  public void read_participant_data(int participant_num) {
  
    int row_counter = 0;
  
    try {
      String filename = "S"+participant_num+".txt"; 
      
      CsvReader csv_data = new CsvReader(filename, '\t'); //open csv file

      while (csv_data.readRecord()) {
        StringTokenizer curr_line = new StringTokenizer(csv_data.getRawRecord());
        //make a new row in data_matrix for the curr_line
        data_matrix.add(new ArrayList<Double>());
        while (curr_line.hasMoreTokens()) {
          //grab a piece of the curr_line
          String curr_token = curr_line.nextToken();
          //parse to a float
          Double float_curr_token = new Double(curr_token);
          //add current token/float (float_curr_token) to a slot in the matrix
          data_matrix.get(row_counter).add(float_curr_token);
          //System.out.println("participant_data added: " + curr_token);
        }
        row_counter++;
      }
        //data_matrix.add(new ArrayList<Float>());
        //System.out.println(csv_data.getRawRecord());
      csv_data.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    numOfRows = row_counter;
  }
  
  public void print_participant_data() {
    System.out.println(data_matrix);
  }
    
  public static void main(String[] args) {
    //participant_data data = new participant_data();
    //data.read_participant_data(1);
    //data.print_participant_data();
  }
    
}
