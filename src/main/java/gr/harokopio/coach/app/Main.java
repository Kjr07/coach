package gr.harokopio.coach.app;

import gr.harokopio.coach.io.TcxActivityReader;
import gr.harokopio.coach.model.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class Main {

    public static void main(String[] args) {

        Double weight = null;
        List<String> files = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ("-w".equals(args[i]) && i + 1 < args.length) {
                weight = Double.parseDouble(args[++i]);
            } else {
                files.add(args[i]);
            }
        }

        if (files.isEmpty()) {
            System.out.println("Usage: java -jar coach.jar [-w weightKg] file1.tcx file2.tcx ...");
            return;
        }

        TcxActivityReader reader = new TcxActivityReader();

         for (String path : files) {
             File tcxFile = new File(path);

             List<Activity> activities = reader.readAll(tcxFile);

             for (Activity activity : activities) {

                 System.out.println("Total distance (m): " +
                         ActivityStats.totalDistanceMeters(activity));
                 System.out.println("Total duration (s): " +
                         ActivityStats.totalDurationSeconds(activity));
                 System.out.println("Avg HR: " +
                         ActivityStats.averageHeartRate(activity));
                 System.out.println("Max HR: " +
                         ActivityStats.maxHeartRate(activity));
                 System.out.printf("Avg speed (km/h): %.2f%n",
                         ActivityStats.avgSpeedKmh(activity));
                 System.out.printf("Avg pace (min/km): %.2f%n",
                         ActivityStats.avgPaceMinPerKm(activity));

                 long secs = ActivityStats.totalDurationSeconds(activity);
                 System.out.println(
                         "Total duration: " + (secs / 60) + "m " + (secs % 60) + "s"
                 );

                 int points = activity.getLaps().stream()
                         .flatMap(l -> l.getTracks().stream())
                         .mapToInt(t -> t.getPoints().size())
                         .sum();

                 System.out.println("Total trackpoints: " + points);
                 System.out.println("Sport: " + activity.getSport());
                 System.out.println("Laps: " + activity.getLaps().size());
                 System.out.println("--------------------");
             }
        }
    }

}
