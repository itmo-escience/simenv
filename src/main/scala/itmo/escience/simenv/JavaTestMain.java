package itmo.escience.simenv;


import java.util.HashMap;

/**
 * Created by mikhail on 11.02.2016.
 */
public class JavaTestMain {
    public static void main(String[] args) {

        String wfPath = ".\\resources\\tplgs\\diamond.json";
        String envPath = ".\\resources\\envs\\blades.json";

        String seedSolution = ".\\resources\\solutions\\sol1.json";

        int localNet = 800; // bandwidth in rack in MB\sec
        int globNet = 800; // bandwidth between racks in MB\sec

        StormScheduler storm = new StormScheduler(wfPath, envPath, globNet, localNet, null);
        storm.initialization();

        Boolean needPrintAlgLog = true;

        HashMap<String, java.util.ArrayList<String>> result = storm.scheduleToMap(storm.run(needPrintAlgLog));
        System.out.println("Finished");
    }


}
