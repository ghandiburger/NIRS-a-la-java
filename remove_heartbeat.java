import java.io.FileNotFoundException;
import java.io.IOException;
import com.csvreader.CsvReader;
import java.util.ArrayList;
import readers.*;

public class remove_heartbeat {

  static double TR = 0.1;
  static int hb = 1;
  
  public double maxima = 0.0;
  public double minima = 0.0;
  public double extrema = 0.0;
  public string extremaType;

  public void go(double[][] data) {
    HBs2find _HBs2find = new HBs2find();
    _HBs2find.go(data);
    maxima = maxima._HBs2find;
    minima = minima._HBs2find;
    extrema = extrema._HBs2find;
    extremaType = extremaType._HBs2find;
    
    nirs2hbmodl_timewarp _nirs2hbmodl_timewarp = new nirs2hbmodl_timewarp();
    _nirs2hbmodl_timewarp.go(data, maxima, minima, extrema, extremaType);
    double[][] cleanData = new double[_nirs2hbmodl_timewarp.clean_rows][_nirs2hbmodl_timewarp.clean_cols];
    cleanData = cleanData._nirs2hbmodl_timewarp;
    
    nirs2hbmodl_removemeanresidual _nirs2hbmodl_removemeanresidual = new nirs2hbmodl_removemeanresidual();
    _nirs2hbmodl_removemeanresidual.go(clean, maxima);
    cleanData = cleanData._nirs2hbmodl_removemeanresidual;
  }
  
  public static void main(String[] args) {
  
  }

}