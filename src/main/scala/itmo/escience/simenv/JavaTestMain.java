package itmo.escience.simenv;


import java.util.HashMap;

/**
 * Created by mikhail on 11.02.2016.
 */
public class JavaTestMain {
    public static void main(String[] args) {

        String wfPath = ".\\resources\\tplgs\\843_tp.json";
        String envPath = ".\\resources\\envs\\843.json";

        String seedSolution = ".\\resources\\solutions\\sol1.json";

        int localNet = 1000; // bandwidth in rack in MB\sec
        int globNet = 1000; // bandwidth between racks in MB\sec

        StormScheduler storm = new StormScheduler(wfPath, envPath, globNet, localNet, null);
        storm.initialization();

        Boolean needPrintAlgLog = true;

        HashMap<String, java.util.ArrayList<String>> result = storm.scheduleToMap(storm.run(needPrintAlgLog));
        double fitness = storm.runFit();
        System.out.println(fitness);
        System.out.println("Finished");
    }


}
