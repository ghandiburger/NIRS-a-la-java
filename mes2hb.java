import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import readers.*;

public class mes2hb {

  // these arraylists hold mes2hb_go's final output
  public ArrayList<Double> mes2hb_hb = new ArrayList<Double>();
  public ArrayList<Double> mes2hb_hbo = new ArrayList<Double>();
  public ArrayList<Double> mes2hb_hbt = new ArrayList<Double>();

  public void mes2hb_go(ArrayList<ArrayList<Double>> mesData, int[] wavelength, int[] baseline) {
    // read in the e_coefs from a .csv
    // see readers/all_e_coef.java
    all_e_coef e_coefs = new all_e_coef();
    e_coefs.read_all_e_coef();
    double[][] e_coef_matrix = new double[e_coefs.numOfRows][e_coefs.numOfCols];
    for (int i=0; i<e_coefs.numOfRows; i++) {
      for (int j=0; j<e_coefs.numOfCols; j++) {
        e_coef_matrix[i][j] = e_coefs.coef_matrix.get(i).get(j);
      }
    }
    
    // give values from wavelength array to new variables - easier to read and understand
    int wlen_690 = wavelength[0];
    int wlen_830 = wavelength[1];
    
    // calculate the average of baseline rows in the 690 wavelength
    double data_fit_690_sum = 0;
    double data_fit_690_n = 0;
    for (int i=baseline[0]-1; i<baseline[1]; i++) {
      data_fit_690_sum = data_fit_690_sum + mesData.get(i).get(0);
      data_fit_690_n++;
    }
    // average for 690 baseline
    double data_fit_690 = data_fit_690_sum/data_fit_690_n;

    // calculate the average of baseline rows in the 690 wavelength
    double data_fit_830_sum = 0;
    double data_fit_830_n = 0;
    for (int i=baseline[0]-1; i<baseline[1]; i++) {
      data_fit_830_sum = data_fit_830_sum + mesData.get(i).get(1);
      data_fit_830_n++;
    }
    // average for 830 baseline
    double data_fit_830 = data_fit_830_sum/data_fit_830_n;
    
    // find e_coefs in the e_coef matrix for each wavelength in both oxy and deoxy
    String oxy = "oxy";
    String deoxy = "deoxy";
    double eoxy_690 = e_coefs.find_e(wlen_690, oxy);
    double eoxy_830 = e_coefs.find_e(wlen_830, oxy);
    double edeo_690 = e_coefs.find_e(wlen_690, deoxy);
    double edeo_830 = e_coefs.find_e(wlen_830, deoxy);
    
    // mesData is the channel arraylist passed from oxiplex_convertToHb
    // the first column in mesData is the 690 wavelength
    // sign_690 has a 1 at a certain index if the value at that index in mesData is > 0
    // sign_690 has a 0 at a certain index if the value at that index in mesData is == 0
    // sign_690 has a -1 at a certain index if the value at that index in mesData is < 0
    ArrayList<Double> sign_690 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      if (mesData.get(i).get(0) > 0) {
        sign_690.add(1.0);
      } else if (mesData.get(i).get(0) == 0) {
        sign_690.add(0.0);
      } else if (mesData.get(i).get(0) < 0) {
        sign_690.add(-1.0);
      }
      // sign_690 is an arraylist of double because of this next line
      // each value in sign_690 is multiplied by the average for the 690 baseline
      Double temp = sign_690.get(i) * data_fit_690;
      sign_690.set(i, temp);
    }
    // this finds the indexes of values in sign_690 that are greater than 0
    ArrayList<Integer> find_index_sign_690 = new ArrayList<Integer>();
    for (int i=0; i<mesData.size(); i++) {
      if (sign_690.get(i) > 0) {
        // add the index of the value > 0 to find_index_sign_690
        find_index_sign_690.add(i);
      }
    }
    // use the values of find_index_sign_690 to get the index of the rows in mesData that we want
    ArrayList<Double> a_690 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      // grab that value from mesData and perform some math on it
      Double temp = Math.log(data_fit_690/mesData.get(find_index_sign_690.get(i)).get(0));
      a_690.add(temp);
    }

    // mesData is the channel arraylist passed from oxiplex_convertToHb
    // the second column in mesData is the 830 wavelength
    // sign_830 has a 1 at a certain index if the value at that index in mesData is > 0
    // sign_830 has a 0 at a certain index if the value at that index in mesData is == 0
    // sign_830 has a -1 at a certain index if the value at that index in mesData is < 0
    ArrayList<Double> sign_830 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      if (mesData.get(i).get(1) > 0) {
        sign_830.add(1.0);
      } else if (mesData.get(i).get(1) == 0) {
        sign_830.add(0.0);
      } else if (mesData.get(i).get(1) < 0) {
        sign_830.add(-1.0);
      }
      // sign_830 is an arraylist of double because of this next line
      // each value in sign_830 is multiplied by the average for the 690 baseline
      Double temp = sign_830.get(i) * data_fit_830;
      sign_830.set(i, temp);
    }
    // this finds the indexes of values in sign_830 that are greater than 0
    ArrayList<Integer> find_index_sign_830 = new ArrayList<Integer>();
    for (int i=0; i<mesData.size(); i++) {
      if (sign_830.get(i) > 0) {
        // add the index of the value > 0 to find_index_sign_830
        find_index_sign_830.add(i);
      }
    }
    // use the values of find_index_sign_830 to get the index of the rows in mesData that we want
    ArrayList<Double> a_830 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      // grab that value from mesData and perform some math on it
      Double temp = Math.log(data_fit_830/mesData.get(find_index_sign_830.get(i)).get(1));
      a_830.add(temp);
    }
    
    // ***** Oxy Hb *****
    if (((eoxy_690*edeo_830)-(eoxy_830*edeo_690)) != 0) {
      for (int i=0; i<a_690.size(); i++) {
        Double temp_a_690 = a_690.get(i) * edeo_830;
        Double temp_a_830 = a_830.get(i) * edeo_690;
        mes2hb_hbo.add((temp_a_690-temp_a_830)/((eoxy_690*edeo_830)-(eoxy_830*edeo_690)));
        
      }
    }
    // ***** Deoxy Hb *****
    if (((edeo_690*eoxy_830)-(edeo_830*eoxy_690)) != 0) {
      for (int i=0; i<a_690.size(); i++) {
        Double temp_a_690 = a_690.get(i) * eoxy_830;
        Double temp_a_830 = a_830.get(i) * eoxy_690;
        mes2hb_hb.add((temp_a_690-temp_a_830)/((edeo_690*eoxy_830)-(edeo_830*eoxy_690)));
      }
    }
    // TOTALHB
    for (int i=0; i<mes2hb_hb.size(); i++) {
      mes2hb_hbt.add(mes2hb_hbo.get(i) + mes2hb_hb.get(i));
    }
    
    // remove the baseline rows from each 
    mes2hb_hb.subList((baseline[0]-1), (baseline[1])).clear();
    mes2hb_hbo.subList((baseline[0]-1), (baseline[1])).clear();
    mes2hb_hbt.subList((baseline[0]-1), (baseline[1])).clear();
  }

  public static void main(String[] args) {
  
  }

}