package itmo.escience.simenv;


import itmo.escience.simenv.tstorm.TstormAlg;

import java.util.HashMap;

/**
 * Created by mikhail on 11.02.2016.
 */
public class JavaTestMain {
    public static void main(String[] args) {

        String wfPath = ".\\resources\\tplgs\\rnd1.json";
        String envPath = ".\\resources\\envs\\env1.json";

        String seedSolution = ".\\resources\\solutions\\sol1.json";

        int localNet = 1000; // bandwidth in rack in MB\sec
        int globNet = 1000; // bandwidth between racks in MB\sec

        boolean perfFlag = true;

        StormScheduler storm = new StormScheduler(wfPath, envPath, globNet, localNet, null, perfFlag);
        storm.initialization();

        Boolean needPrintAlgLog = true;

        HashMap<String, java.util.ArrayList<String>> result = storm.scheduleToMap(storm.run(needPrintAlgLog));
        double fitness = storm.runFit();
        System.out.println(fitness);

        HashMap<String, java.util.ArrayList<String>> tStormResult = storm.scheduleToMap(TstormAlg.run(wfPath, envPath));

        System.out.println("Finished");
    }


}
