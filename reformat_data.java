import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class reformat_data {
  
  public int[] log_timestamps = new int[5]; // array to hold timestamps from log file
  public ArrayList<ArrayList<Double>> first_condition = new ArrayList<ArrayList<Double>>(); // holds the rows for first condition
  public int first_con_start_index = 0; // first condition's start index
  public int first_con_end_index = 0; // first condition's end index
  public ArrayList<ArrayList<Double>> second_condition = new ArrayList<ArrayList<Double>>(); // holds the rows for second condition
  public int second_con_start_index = 0; // second condition's start index
  public int second_con_end_index = 0; // second condition's end index
  public ArrayList<ArrayList<Double>> third_condition = new ArrayList<ArrayList<Double>>(); // holds the rows for third condition
  public int third_con_start_index = 0; // third condition's start index
  public int third_con_end_index = 0; // third condition's end index
  public ArrayList<ArrayList<Double>> fourth_condition = new ArrayList<ArrayList<Double>>(); // holds the rows for fourth condition
  public int fourth_con_start_index = 0; // fourth condition's start index
  public int fourth_con_end_index = 0; // fourth condition's end index
  
  // length of each subtask within a trial, in rows
  public int preRestRows = 63;
  public int postRestRows = 63;
  public int memoryTaskRows = 125;
  public int responseTaskRows = 93;
  public int moralTaskRows = 125;
  public int trialRows = 469;
  
  // read timestamps from log file into log_timestamps[]
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
      for (int i=0; i<4; i++) {
        if (i == 0) {
          // calculate first condition start and end indexes
          // calculate gap in ms between end of baseline and start of first condition
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + 30000 + 5000);
          // convert ms to rows
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          // the data is 0-indexed, so the first condition starts at the index equal to the length of the first gap
          first_con_start_index = gap_length_rows;
          // first condition ends at first_con_start_index + length_of_condition_in_rows
          first_con_end_index = first_con_start_index + length_of_condition_in_rows;
          // end_curr_con gets the end index to use in the calculation of the next start index
          end_curr_con = first_con_end_index;
        } else if (i==1) {
          // calculate second condition start and end indexes
          // calculate gap in ms between end of first condition and start of second condition
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + (6*75000) + 5000);
          // convert ms to rows
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          // end_curr_con is the currently the end of the first condition, so add the gap length in rows to find second condition's starting index
          second_con_start_index = end_curr_con + gap_length_rows;
          // second condition ends at second_con_start_index + length_of_condition_in_rows
          second_con_end_index = second_con_start_index + length_of_condition_in_rows;
          // end_curr_con gets the end index to use in the calculation of the next start index
          end_curr_con = second_con_end_index;
        } else if (i==2) {
          // calculate third condition start and end indexes
          // calculate gap in ms between end of second condition and start of third condition
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + (6*75000) + 5000);
          // convert ms to rows
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
           // end_curr_con is the currently the end of the second condition, so add the gap length in rows to find third condition's starting index
          third_con_start_index = end_curr_con + gap_length_rows;
          // third condition ends at third_con_start_index + length_of_condition_in_rows
          third_con_end_index = third_con_start_index + length_of_condition_in_rows;
          // end_curr_con gets the end index to use in the calculation of the next start index
          end_curr_con = third_con_end_index;
        } else if (i==3) {
          // calculate fourth condition start and end indexes
          // calculate gap in ms between end of third condition and start of fourth condition
          gap_length_ms = log_timestamps[i+1] - (log_timestamps[i] + (6*75000) + 5000);
          // convert ms to rows
          gap_length_rows = (int) ((gap_length_ms/1000)/0.16);
          // end_curr_con is the currently the end of the third condition, so add the gap length in rows to find fourth condition's starting index
          fourth_con_start_index = end_curr_con + gap_length_rows;
          // fourth condition ends at fourth_con_start_index + length_of_condition_in_rows
          fourth_con_end_index = fourth_con_start_index + length_of_condition_in_rows;
          end_curr_con = fourth_con_end_index;
        }
      }
  }
  
  public void grab_conditions(double[][] mes2hb_data) {
    // grab each condition from mes2hb_data and place in appropriate arraylist
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
  
  // normalize each subtask
  public int normalize(int length, int starting_index, ArrayList<ArrayList<Double>> data) {
    
    double[] normalizing_values = new double[data.get(starting_index).size()];
    
    for (int i=0; i<data.get(starting_index).size(); i++) {
      normalizing_values[i] = data.get(starting_index).get(i);
    }
  
    for (int i=starting_index; i<(starting_index+length); i++) {
      for (int j=0; j<data.get(i).size(); j++) {
        double curr_value = data.get(i).get(j);
        double normalized_value = curr_value - normalizing_values[j];
        data.get(i).set(j, normalized_value);
      }
    }
    return (starting_index+length);
  
  }
  
  public void normalize_trials() {
    
    // set lengths of each subtask, in rows
    int[] task_lengths = new int[5];
    task_lengths[0] = 63;
    task_lengths[1] = 125;
    task_lengths[2] = 93;
    task_lengths[3] = 125;
    task_lengths[4] = 63;
    
    // normalize first condition
    int starting_index_1 = 0;
    for (int i=0; i<6; i++) {
      for (int j=0; j<5; j++) {
        starting_index_1 = normalize(task_lengths[j], starting_index_1, first_condition);
      }
    }
    
    // normalize second condition
    int starting_index_2 = 0;
    for (int i=0; i<6; i++) {
      for (int j=0; j<5; j++) {
        starting_index_2 = normalize(task_lengths[j], starting_index_2, second_condition);
      }
    }
    
    // normalize third condition
    int starting_index_3 = 0;
    for (int i=0; i<6; i++) {
      for (int j=0; j<5; j++) {
        starting_index_3 = normalize(task_lengths[j], starting_index_3, third_condition);
      }
    }
    
    // normalize fourth condition
    int starting_index_4 = 0;
    for (int i=0; i<6; i++) {
      for (int j=0; j<5; j++) {
        starting_index_4 = normalize(task_lengths[j], starting_index_4, fourth_condition);
      }
    }
    
  }
  
  public void print_conditions() {
    System.out.println(first_condition);
  }
  
  public void write_data(String type, int participant_num, String directory) {
    
    // set the name, location, and type of the output file
    String output_file = directory + "reformatted_S" + participant_num + "_" + type + ".csv";

    try {
      CsvWriter output = new CsvWriter(new FileWriter(output_file, true), ',');
      
      // write first condition
      for (int i=0; i<first_condition.size(); i++) {
        for (int j=0; j<first_condition.get(0).size(); j++) {
          String temp = Double.toString(first_condition.get(i).get(j));
          output.write(temp);
        }
        output.endRecord();
      }
      
      // write second condition
      for (int i=0; i<second_condition.size(); i++) {
        for (int j=0; j<second_condition.get(0).size(); j++) {
          String temp = Double.toString(second_condition.get(i).get(j));
          output.write(temp);
        }
        output.endRecord();
      }
      
      // write third condition
      for (int i=0; i<third_condition.size(); i++) {
        for (int j=0; j<third_condition.get(0).size(); j++) {
          String temp = Double.toString(third_condition.get(i).get(j));
          output.write(temp);
        }
        output.endRecord();
      }
      
      // write fourth condition
      for (int i=0; i<fourth_condition.size(); i++) {
        for (int j=0; j<fourth_condition.get(0).size(); j++) {
          String temp = Double.toString(fourth_condition.get(i).get(j));
          output.write(temp);
        }
        output.endRecord();
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
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
