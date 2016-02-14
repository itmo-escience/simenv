package itmo.escience.simenv;


import java.util.HashMap;

/**
 * Created by mikhail on 11.02.2016.
 */
public class JavaTestMain {
    public static void main(String[] args) {

        String wfPath = ".\\resources\\tplgs\\tplg1.json";
        String envPath = ".\\resources\\envs\\env1.json";

        String seedSolution = ".\\resources\\solutions\\sol1.json";

        int localNet = 5000;
        int globNet = 5;

        StormScheduler storm = new StormScheduler(wfPath, envPath, globNet, localNet, null);
        storm.initialization();
        HashMap<String, java.util.ArrayList<java.util.ArrayList<Object>>> result = storm.scheduleToMapList(storm.run());
        System.out.println("Finished");
    }


}
