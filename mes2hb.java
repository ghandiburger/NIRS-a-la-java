import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import readers.*;

public class mes2hb {

  public ArrayList<Double> mes2hb_hb = new ArrayList<Double>();
  public ArrayList<Double> mes2hb_hbo = new ArrayList<Double>();
  public ArrayList<Double> mes2hb_hbt = new ArrayList<Double>();

  public void mes2hb_go(ArrayList<ArrayList<Double>> mesData, int[] wavelength, int[] baseline) {
    //System.out.println("Number of rows = " + mesData.length); = 12447
    //System.out.println("Number of columns = " + mesData[0].length); = 2
    
    //System.out.println(mesData.get(0).get(0));
    
    //System.out.println(baseline[0]);
    //System.out.println(baseline[1]);
    
    all_e_coef e_coefs = new all_e_coef();
    e_coefs.read_all_e_coef();
    double[][] e_coef_matrix = new double[e_coefs.numOfRows][e_coefs.numOfCols];
    for (int i=0; i<e_coefs.numOfRows; i++) {
      for (int j=0; j<e_coefs.numOfCols; j++) {
        e_coef_matrix[i][j] = e_coefs.coef_matrix.get(i).get(j);
      }
    }
    
    int wlen_690 = wavelength[0];
    //System.out.println("wlen_690:" + wavelength[0]);
    int wlen_830 = wavelength[1];
    //System.out.println("wlen_830:" + wavelength[1]);
    
    //System.out.print("baseline[0]:" + baseline[0]);
    //System.out.println("\n");
    //System.out.print("baseline[1]:" + baseline[1]);
    //System.out.println("\n");
        
    double data_fit_690_sum = 0;
    double data_fit_690_n = 0;
    //System.out.println("mesData[baseline[0]-1][0]: ");
    //System.out.println(mesData[baseline[0]-1][0]);
    //System.out.println("mesData[baseline[1]-1][0]: ");
    //System.out.println(mesData[baseline[1]-1][0]);
    for (int i=baseline[0]-1; i<baseline[1]; i++) {
      //System.out.println("mesData["+i+"][0]: ");
      //System.out.println(mesData[i][0]);
      data_fit_690_sum = data_fit_690_sum + mesData.get(i).get(0);
      data_fit_690_n++;
    }
    double data_fit_690 = data_fit_690_sum/data_fit_690_n;
    //System.out.println("data_fit_690:" + data_fit_690);
    //System.out.println("\n");
    
    double data_fit_830_sum = 0;
    double data_fit_830_n = 0;
    //System.out.println("mesData[baseline[0]-1][1]: ");
    //System.out.println(mesData[baseline[0]-1][1]);
    //System.out.println("mesData[baseline[1]-1][1]: ");
    //System.out.println(mesData[baseline[1]-1][1]);
    //System.out.println("\n");
    for (int i=baseline[0]-1; i<baseline[1]; i++) {
      //System.out.println("mesData["+i+"][1]: ");
      //System.out.println(mesData[i][1]);
      data_fit_830_sum = data_fit_830_sum + mesData.get(i).get(1);
      data_fit_830_n++;
    }
    double data_fit_830 = data_fit_830_sum/data_fit_830_n;
    //System.out.println("data_fit_830:" + data_fit_830);
    //System.out.println("\n");
    //System.out.println("\n");
    //System.out.println("#######");
    
    String oxy = "oxy";
    String deoxy = "deoxy";
    
    double eoxy_690 = e_coefs.find_e(wlen_690, oxy);
    //System.out.println("eoxy_690: " + eoxy_690);
    double eoxy_830 = e_coefs.find_e(wlen_830, oxy);
    //System.out.println("eoxy_830: " + eoxy_830);
    double edeo_690 = e_coefs.find_e(wlen_690, deoxy);
    //System.out.println("edeo_690: " + edeo_690);
    double edeo_830 = e_coefs.find_e(wlen_830, deoxy);
    //System.out.println("edeo_830: " + edeo_830);
    //System.out.println("\n");
    
    ArrayList<Double> sign_690 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      if (mesData.get(i).get(0) > 0) {
        sign_690.add(1.0);
      } else if (mesData.get(i).get(0) == 0) {
        sign_690.add(0.0);
      } else if (mesData.get(i).get(0) < 0) {
        sign_690.add(-1.0);
      }
      Double temp = sign_690.get(i) * data_fit_690;
      sign_690.set(i, temp);
    }
    ArrayList<Integer> find_index_sign_690 = new ArrayList<Integer>();
    for (int i=0; i<mesData.size(); i++) {
      if (sign_690.get(i) > 0) {
        find_index_sign_690.add(i);
      }
    }
    ArrayList<Double> a_690 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      Double temp = Math.log(data_fit_690/mesData.get(find_index_sign_690.get(i)).get(0));
      a_690.add(temp);
    }

    ArrayList<Double> sign_830 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      if (mesData.get(i).get(1) > 0) {
        sign_830.add(1.0);
      } else if (mesData.get(i).get(1) == 0) {
        sign_830.add(0.0);
      } else if (mesData.get(i).get(1) < 0) {
        sign_830.add(-1.0);
      }
      Double temp = sign_830.get(i) * data_fit_830;
      sign_830.set(i, temp);
    }
    ArrayList<Integer> find_index_sign_830 = new ArrayList<Integer>();
    for (int i=0; i<mesData.size(); i++) {
      if (sign_830.get(i) > 0) {
        find_index_sign_830.add(i);
      }
    }
    ArrayList<Double> a_830 = new ArrayList<Double>();
    for (int i=0; i<mesData.size(); i++) {
      Double temp = Math.log(data_fit_830/mesData.get(find_index_sign_830.get(i)).get(1));
      a_830.add(temp);
    }
    
    // ***** Oxy Hb *****
    if (((eoxy_690*edeo_830)-(eoxy_830*edeo_690)) != 0) {
      //System.out.println("Oxy Hb");
      for (int i=0; i<a_690.size(); i++) {
        Double temp_a_690 = a_690.get(i) * edeo_830;
        Double temp_a_830 = a_830.get(i) * edeo_690;
        mes2hb_hbo.add((temp_a_690-temp_a_830)/((eoxy_690*edeo_830)-(eoxy_830*edeo_690)));
        
      }
    }
    // ***** Deoxy Hb *****
    if (((edeo_690*eoxy_830)-(edeo_830*eoxy_690)) != 0) {
      //System.out.println("Deoxy Hb");
      for (int i=0; i<a_690.size(); i++) {
        Double temp_a_690 = a_690.get(i) * eoxy_830;
        Double temp_a_830 = a_830.get(i) * eoxy_690;
        mes2hb_hb.add((temp_a_690-temp_a_830)/((edeo_690*eoxy_830)-(edeo_830*eoxy_690)));
      }
    }
    
    for (int i=0; i<mes2hb_hb.size(); i++) {
      mes2hb_hbt.add(mes2hb_hbo.get(i) + mes2hb_hb.get(i));
      //hbt[i] = (hbo[i] + hb[i]);
    }
    
    mes2hb_hb.subList((baseline[0]-1), (baseline[1])).clear();
    mes2hb_hbo.subList((baseline[0]-1), (baseline[1])).clear();
    mes2hb_hbt.subList((baseline[0]-1), (baseline[1])).clear();
  }

  public static void main(String[] args) {
  
  }

}