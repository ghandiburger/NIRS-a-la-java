
import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class new_participant_data {
  
  public ArrayList<ArrayList<Double>> data_matrix = new ArrayList<ArrayList<Double>>(); //init 2D arraylist
  public ArrayList<ArrayList<Double>> final_data_matrix = new ArrayList<ArrayList<Double>>();
  public int numOfRows;
  
  public void read_participant_data(int participant_num, String directory, int[] baseline) {
  
    int row_counter = 0;
    
    try {
      String filename = directory + "S"+participant_num+".txt"; 
      CsvReader csv_data = new CsvReader(filename, '\t'); //open csv file
      while (csv_data.readRecord()) {
        StringTokenizer curr_line = new StringTokenizer(csv_data.getRawRecord());
        if (row_counter > 107) {
          //make a new row in data_matrix for the curr_line
          data_matrix.add(new ArrayList<Double>());
          while (curr_line.hasMoreTokens()) {
            //grab a piece of the curr_line
            String curr_token = curr_line.nextToken();
            //System.out.println(curr_token);
            //parse to a float
            Double float_curr_token = new Double(curr_token);
            //add current token/float (float_curr_token) to a slot in the matrix
            data_matrix.get(row_counter-108).add(float_curr_token);
            //System.out.println("participant_data added: " + curr_token);
          }
          row_counter++;
        } else {
          row_counter++;
        }
      }
      csv_data.close();
      
      // Find the marker! Remove rows prior to marker!
      // marker column is index 30
      for (int i=0; i<data_matrix.size(); i++) {
        if (data_matrix.get(i).get(30) == 1) {
          data_matrix.subList(0,i).clear();
        }
      }
      
      //System.out.println("Before removal");
      //System.out.println(data_matrix.get(0));
      // Remove unnecessary columns
      for (int i=0; i<data_matrix.size(); i++) {
        data_matrix.get(i).subList(0,10).clear();
        data_matrix.get(i).subList(8,20).clear();
        data_matrix.get(i).subList(9,17).clear();
        data_matrix.get(i).subList(17,25).clear();
        //System.out.println(data_matrix.get(i));
      }
      //System.out.println("After removal");
      //System.out.println(data_matrix.get(0));
      
      // Now remove the marker column!
      for (int i=0; i<data_matrix.size(); i++) {
        data_matrix.get(i).subList(8,9).clear();
        //System.out.println(data_matrix.get(i));
      }
      
      String log_filename = directory+"LOGS/LOG-"+participant_num+".txt";
      CsvReader log_data = new CsvReader(log_filename, '\n');
      int log_baseline = 0;
      int first_condition = 0;
      int second_condition = 0;
      int third_condition = 0;
      int fourth_condition = 0;
      int line_counter = 0;
      while (log_data.readRecord()) {
        StringTokenizer curr_line = new StringTokenizer(log_data.getRawRecord());
        while (curr_line.hasMoreTokens()) {
          String curr_token = curr_line.nextToken();
          if (line_counter == 0) {
            log_baseline = new Integer(curr_token);
          }
          if (line_counter == 1) {
            first_condition = new Integer(curr_token);
          }
          if (line_counter == 2) {
            second_condition = new Integer(curr_token);
          }
          if (line_counter == 3) {
            third_condition = new Integer(curr_token);
          }
          if (line_counter == 4) {
            fourth_condition = new Integer(curr_token);
          }
          //System.out.println(curr_token);
          line_counter++;
        }
      }
      log_data.close();
      
      //System.out.println(log_baseline);
      //System.out.println(first_condition);
      //System.out.println(second_condition);
      //System.out.println(third_condition);
      //System.out.println(fourth_condition);
      //System.out.println(baseline[0]);
      //System.out.println(baseline[1]);
      
      int gap = first_condition - (baseline[0] + 30000 + 5000);
      int rowIndex = baseline[1] + 1;
      double num_rows_to_remove = (gap/1000)/0.16;
      System.out.println(num_rows_to_remove);
            
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
    int[] baseline = new int[2];
    baseline[0] = 1;
    baseline[1] = 2;
    int baselineDuration = 30;
    double samplingRate = 0.16;
    double temp = Math.ceil(baselineDuration/samplingRate);
    int baseline_calc = (int) (temp);
    baseline[1] = baseline[0] + baseline_calc;
    new_participant_data data = new new_participant_data();
    data.read_participant_data(1, "E3/", baseline);
    //data.print_participant_data();
    //System.out.println(data.data_matrix.size());
  }
    
}
