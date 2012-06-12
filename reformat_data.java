import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class reformat_data {
  
  public int[] log_timestamps = new int[5];
  public ArrayList<ArrayList<Double>> first_condition = new ArrayList<ArrayList<Double>>();
  public int first_con_start_index = 0;
  public int first_con_end_index = 0;
  public ArrayList<ArrayList<Double>> second_condition = new ArrayList<ArrayList<Double>>();
  public int second_con_start_index = 0;
  public int second_con_end_index = 0;
  public ArrayList<ArrayList<Double>> third_condition = new ArrayList<ArrayList<Double>>();
  public int third_con_start_index = 0;
  public int third_con_end_index = 0;
  public ArrayList<ArrayList<Double>> fourth_condition = new ArrayList<ArrayList<Double>>();
  public int fourth_con_start_index = 0;
  public int fourth_con_end_index = 0;
  
  public void read_log_data(int participant_num, String directory) {
    try {      
      String log_filename = directory+"LOGS/LOG-"+participant_num+".txt";
      CsvReader log_data = new CsvReader(log_filename, '\n');
      int line_counter = 0;
      while (log_data.readRecord()) {
        StringTokenizer curr_line = new StringTokenizer(log_data.getRawRecord());
        while (curr_line.hasMoreTokens()) {
          String curr_token = curr_line.nextToken();
          if (line_counter == 0) {
            log_timestamps[0] = new Integer(curr_token);
          }
          if (line_counter == 1) {
            log_timestamps[1] = new Integer(curr_token);
          }
          if (line_counter == 2) {
            log_timestamps[2] = new Integer(curr_token);
          }
          if (line_counter == 3) {
            log_timestamps[3] = new Integer(curr_token);
          }
          if (line_counter == 4) {
            log_timestamps[4] = new Integer(curr_token);
          }
          //System.out.println(curr_token);
          line_counter++;
        }
      }
      log_data.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void calculate_condition_locations() {
      int gap_length_ms = 0;
      int gap_length_rows = 0;
      int length_of_condition_in_rows = (int) ((450000/1000)/0.16);
      int end_curr_con = 0;
      //System.out.println("length_of_condition_in_rows RAW: " + ((450000/1000)/0.16));
      //System.out.println("length_of_condition_in_rows ROUNDED: " + length_of_condition_in_rows);
      for (int i=0; i<4; i++) {
        if (i == 0) {
          // this function is called AFTER the baseline rows are trimmed from the data, 
          // so the starting row for the baseline/first-condition gap is 0
          //System.out.println("Condition 1");
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + 30000 + 5000);
          //System.out.println("gap_length_rows RAW: " + ((gap_length_ms/1000)/0.16));
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          //System.out.println("gap_length_rows ROUNDED: " + gap_length_rows);
          first_con_start_index = gap_length_rows;
          first_con_end_index = first_con_start_index + length_of_condition_in_rows;
          end_curr_con = first_con_end_index;
          //System.out.println("start index: " + first_con_start_index);
          //System.out.println("end index: " + first_con_end_index);
        } else if (i==1) {
          //System.out.println("Condition 2");
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + (6*75000) + 5000);
          //System.out.println("gap_length_rows RAW: " + ((gap_length_ms/1000)/0.16));
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          //System.out.println("gap_length_rows ROUNDED: " + gap_length_rows);
          second_con_start_index = end_curr_con + gap_length_rows;
          second_con_end_index = second_con_start_index + length_of_condition_in_rows;
          end_curr_con = second_con_end_index;
          //System.out.println("start index: " + second_con_start_index);
          //System.out.println("end index: " + second_con_end_index);
        } else if (i==2) {
          //System.out.println("Condition 3");
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + (6*75000) + 5000);
          //System.out.println("gap_length_rows RAW: " + ((gap_length_ms/1000)/0.16));
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          //System.out.println("gap_length_rows ROUNDED: " + gap_length_rows);
          third_con_start_index = end_curr_con + gap_length_rows;
          third_con_end_index = third_con_start_index + length_of_condition_in_rows;
          end_curr_con = third_con_end_index;
          //System.out.println("start index: " + third_con_start_index);
          //System.out.println("end index: " + third_con_end_index);
        } else if (i==3) {
          //System.out.println("Condition 4");
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + (6*75000) + 5000);
          //System.out.println("gap_length_rows RAW: " + ((gap_length_ms/1000)/0.16));
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          //System.out.println("gap_length_rows ROUNDED: " + gap_length_rows);
          fourth_con_start_index = end_curr_con + gap_length_rows;
          fourth_con_end_index = fourth_con_start_index + length_of_condition_in_rows;
          end_curr_con = fourth_con_end_index;
          //System.out.println("start index: " + fourth_con_start_index);
          //System.out.println("end index: " + fourth_con_end_index);
        }
      }
      //System.out.println("first_con_start_index: " + first_con_start_index);
      //System.out.println("first_con_end_index: " + first_con_end_index);
      //System.out.println("second_con_start_index: " + second_con_start_index);
      //System.out.println("second_con_end_index: " + second_con_end_index);
      //System.out.println("third_con_start_index: " + third_con_start_index);
      //System.out.println("third_con_end_index: " + third_con_end_index);
      //System.out.println("fourth_con_start_index: " + fourth_con_start_index);
      //System.out.println("fourth_con_end_index: " + fourth_con_end_index);
  }
  
  public void grab_conditions(double[][] mes2hb_data) {
    for (int i=0; i<4; i++) {
        if (i == 0) {
          int arraylist_row_counter = 0;
          for (int a=first_con_start_index; a<first_con_end_index+2; a++) {
            first_condition.add(new ArrayList<Double>());
            for (int j=0; j<mes2hb_data[0].length; j++) {
              first_condition.get(arraylist_row_counter).add(mes2hb_data[a][j]);
            }
            arraylist_row_counter++;
          }
        } else if (i==1) {
          int arraylist_row_counter = 0;
          for (int a=second_con_start_index; a<second_con_end_index+2; a++) {
            second_condition.add(new ArrayList<Double>());
            for (int j=0; j<mes2hb_data[0].length; j++) {
              second_condition.get(arraylist_row_counter).add(mes2hb_data[a][j]);
            }
            arraylist_row_counter++;
          }
        } else if (i==2) {
          int arraylist_row_counter = 0;
          for (int a=third_con_start_index; a<third_con_end_index+2; a++) {
            third_condition.add(new ArrayList<Double>());
            for (int j=0; j<mes2hb_data[0].length; j++) {
              third_condition.get(arraylist_row_counter).add(mes2hb_data[a][j]);
            }
            arraylist_row_counter++;
          }
        } else if (i==3) {
          int arraylist_row_counter = 0;
          for (int a=fourth_con_start_index; a<fourth_con_end_index+2; a++) {
            fourth_condition.add(new ArrayList<Double>());
            for (int j=0; j<mes2hb_data[0].length; j++) {
              fourth_condition.get(arraylist_row_counter).add(mes2hb_data[a][j]);
            }
            arraylist_row_counter++;
          }
        }
      }
  }
  
  // 1: normalize within each trial
  public void normalize_trials() {
    
    //System.out.println("trial_length_rows: " + (75/0.16));
    //System.out.println("pre_rest_length_rows: " +(10/0.16));
    //System.out.println("mem_task_length_rows: " + (20/0.16));
    //System.out.println("response_length_rows: " + (15/0.16));
    //System.out.println("dilemma_length_rows: " + (20/0.16));
    //System.out.println("post_rest_length_rows: " + (10/0.16));
    //System.out.println("sum of sub rows: " + (pre_rest_length_rows+mem_task_length_rows+response_length_rows+dilemma_length_rows+post_rest_length_rows));
    System.out.println("first_condition.size(): " + first_condition.size());
    System.out.println("second_condition.size(): " + second_condition.size());
    System.out.println("third_condition.size(): " + third_condition.size());
    System.out.println("fourth_condition.size(): " + fourth_condition.size());
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
    reformat_data data = new reformat_data();
    data.read_log_data(1, "E3/");
  }
    
}
