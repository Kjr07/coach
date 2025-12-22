package gr.harokopio.coach.io;

import gr.harokopio.coach.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TcxActivityReader {

    public List<Activity> readAll(File tcxFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(tcxFile);
            document.getDocumentElement().normalize();

            NodeList activityNodes = document.getElementsByTagName("Activity");
            List<Activity> activities = new ArrayList<>();

            for (int a = 0; a < activityNodes.getLength(); a++) {
                Element activityElement = (Element) activityNodes.item(a);
                String sportAttr = activityElement.getAttribute("Sport");
                SportType sport = parseSportType(sportAttr);

                String idText = activityElement
                        .getElementsByTagName("Id")
                        .item(0)
                        .getTextContent();

                    Instant startTime = Instant.parse(idText);

                    NodeList lapNodes = activityElement.getElementsByTagName("Lap");
                    List<Lap> laps = new ArrayList<>();

                for (int i = 0; i < lapNodes.getLength(); i++) {
                    Element lapElement = (Element) lapNodes.item(i);

                    String lapStart = lapElement.getAttribute("StartTime");
                    Instant lapStartTime = Instant.parse(lapStart);

                    NodeList trackNodes = lapElement.getElementsByTagName("Track");
                    List<Track> tracks = new ArrayList<>();

                    for (int t = 0; t < trackNodes.getLength(); t++) {
                        Element trackElement = (Element) trackNodes.item(t);

                        NodeList tpNodes = trackElement.getElementsByTagName("Trackpoint");
                        List<TrackPoint> points = new ArrayList<>();

                        for (int p = 0; p < tpNodes.getLength(); p++) {
                            Element tp = (Element) tpNodes.item(p);

                            Instant time = Instant.parse(getText(tp, "Time"));

                            Double lat = null;
                            Double lon = null;
                            Element pos = getChild(tp, "Position");
                            if (pos != null) {
                                lat = getDouble(pos, "LatitudeDegrees");
                                lon = getDouble(pos, "LongitudeDegrees");
                            }

                            Double alt = getDouble(tp, "AltitudeMeters");
                            Double dist = getDouble(tp, "DistanceMeters");

                            Integer hr = null;
                            Element hrEl = getChild(tp, "HeartRateBpm");
                            if (hrEl != null) {
                                hr = getInt(hrEl, "Value");
                            }

                            points.add(new TrackPoint(time, lat, lon, alt, dist, hr));
                        }

                        tracks.add(new Track(points));
                    }



                    Lap lap = new Lap(lapStartTime, tracks);
                    laps.add(lap);

                    System.out.println("Lap parsed with startTime = " + lapStartTime);
                }


                    activities.add(new Activity(sport, startTime, laps));

            }

            return activities;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read TCX file", e);
        }
    }


    public Activity read(File tcxFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(tcxFile);

            document.getDocumentElement().normalize();

            Node activityNode = document.getElementsByTagName("Activity").item(0);
            Element activityElement = (Element) activityNode;

            String sportAttr = activityElement.getAttribute("Sport");
            SportType sport = parseSportType(sportAttr);

            String idText = activityElement
                    .getElementsByTagName("Id")
                    .item(0)
                    .getTextContent();

            Instant startTime = Instant.parse(idText);

            NodeList lapNodes = activityElement.getElementsByTagName("Lap");
            List<Lap> laps = new ArrayList<>();

            for (int i = 0; i < lapNodes.getLength(); i++) {
                Element lapElement = (Element) lapNodes.item(i);

                String lapStart = lapElement.getAttribute("StartTime");
                Instant lapStartTime = Instant.parse(lapStart);

                NodeList trackNodes = lapElement.getElementsByTagName("Track");
                List<Track> tracks = new ArrayList<>();

                for (int t = 0; t < trackNodes.getLength(); t++) {
                    Element trackElement = (Element) trackNodes.item(t);

                    NodeList tpNodes = trackElement.getElementsByTagName("Trackpoint");
                    List<TrackPoint> points = new ArrayList<>();

                    for (int p = 0; p < tpNodes.getLength(); p++) {
                        Element tp = (Element) tpNodes.item(p);

                        Instant time = Instant.parse(getText(tp, "Time"));

                        Double lat = null;
                        Double lon = null;
                        Element pos = getChild(tp, "Position");
                        if (pos != null) {
                            lat = getDouble(pos, "LatitudeDegrees");
                            lon = getDouble(pos, "LongitudeDegrees");
                        }

                        Double alt = getDouble(tp, "AltitudeMeters");
                        Double dist = getDouble(tp, "DistanceMeters");

                        Integer hr = null;
                        Element hrEl = getChild(tp, "HeartRateBpm");
                        if (hrEl != null) {
                            hr = getInt(hrEl, "Value");
                        }

                        points.add(new TrackPoint(time, lat, lon, alt, dist, hr));
                    }

                    tracks.add(new Track(points));
                }



                Lap lap = new Lap(lapStartTime, tracks);
                laps.add(lap);

                System.out.println("Lap parsed with startTime = " + lapStartTime);
            }


            return new Activity(sport, startTime, laps);

        } catch (Exception e) {
            throw new RuntimeException("Failed to read TCX file", e);
        }
    }

    private SportType parseSportType(String sportAttr) {
        if (sportAttr == null) {
            return SportType.OTHER;
        }
        switch (sportAttr.toLowerCase()) {
            case "running":
                return SportType.RUNNING;
            case "cycling":
                return SportType.CYCLING;
            case "swimming":
                return SportType.SWIMMING;
            case "walking":
                return SportType.WALKING;
            default:
                return SportType.OTHER;
        }
    }
    private Element getChild(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        return (Element) list.item(0);
    }

    private String getText(Element parent, String tag) {
        Element el = getChild(parent, tag);
        if (el == null) return null;
        return el.getTextContent().trim();
    }

    private Double getDouble(Element parent, String tag) {
        String txt = getText(parent, tag);
        if (txt == null || txt.isEmpty()) return null;
        return Double.parseDouble(txt);
    }

    private Integer getInt(Element parent, String tag) {
        String txt = getText(parent, tag);
        if (txt == null || txt.isEmpty()) return null;
        return Integer.parseInt(txt);
    }

}
