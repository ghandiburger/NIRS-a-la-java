package readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;

public class all_e_coef {

  public ArrayList<ArrayList<Double>> coef_matrix = new ArrayList<ArrayList<Double>>(); //init 2D arraylist
  public int numOfRows;
  public int numOfCols = 3;
  public int wavelength = 690;
  public double uc;
  public double lc;
  public int find_e_col = 0;

  public void read_all_e_coef() {

    int row_counter = 0;

    try {
      CsvReader coefs = new CsvReader("all_e_coef.csv"); //open csv file
      coefs.readHeaders();
      
      while(coefs.readRecord()) { //read one line at a time
        //get dem strings
        String wavelength = coefs.get("Wavelength");
        String deoxy = coefs.get("Deoxy");
        String oxy = coefs.get("Oxy");
        
        //parse dem strings to floats for all the accuracy
        double double_wavelength = new Double(wavelength);
        double double_deoxy = new Double(deoxy);
        double double_oxy = new Double(oxy);
        //System.out.println("!!! FLOATS !!!");
        //System.out.println(float_wavelength);
        //System.out.println(float_deoxy);
        //System.out.println(float_oxy);
        
        //make new row in coef_matrix for the current line
        coef_matrix.add(new ArrayList<Double>());
        
        //add floats to the row
        coef_matrix.get(row_counter).add(double_wavelength);
        coef_matrix.get(row_counter).add(double_deoxy);
        coef_matrix.get(row_counter).add(double_oxy);
        
        //increment row_counter so the loop makes a new row
        row_counter++;
      }
      coefs.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    numOfRows = row_counter;
  }

  public void print_all_e_coef() {
    //print out the row
    for (int i=0; i<coef_matrix.size(); i++) {
      for (int j=0; j<3; j++) {
        System.out.println(coef_matrix.get(i).get(j));
      }
    }
  }
  
  public double find_e(int input_wavelength, String oxydeoxy) {
    if (oxydeoxy.equals("oxy")) {
      //System.out.println("searching for oxy e_coef");
      find_e_col = 2;
    } else if (oxydeoxy.equals("deoxy")) {
      //System.out.println("searching for deoxy e_coef");
      find_e_col = 1;
    } else {
      System.out.println("You need to specify deoxy or oxy");
    }
    
    double l = input_wavelength;
    double u = input_wavelength + 1;
    
    for (int i=0; i<coef_matrix.size(); i++) {
      if (coef_matrix.get(i).get(0) == l) {
        lc = coef_matrix.get(i).get(find_e_col);
        //System.out.println("lc: " + lc);
      }
    }
    for (int i=0; i<coef_matrix.size(); i++) {
      if (coef_matrix.get(i).get(0) == u) {
        uc = coef_matrix.get(i).get(find_e_col);
        //System.out.println("uc: " + uc);
      }
    }
    
    double e = (((uc-lc)/(u-l))*(input_wavelength-l)) + lc;
    //System.out.println(e);
    return e;
  }

  public static void main(String[] args) {
    //all_e_coef coefs = new all_e_coef();
    //coefs.read_all_e_coef();
    //coefs.print_all_e_coef();
    
    //System.out.println("\n");
    //float number = coefs.coef_matrix.get(1).get(1);
    //System.out.println(number);
    
    //coefs.find_e(coefs.wavelength, "oxy");
    
  }
  
}