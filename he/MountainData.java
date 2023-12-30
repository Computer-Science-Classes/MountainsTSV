import java.io.*;
import java.util.ArrayList;

public class MountainData {
    private ArrayList<Mountain> mountains = new ArrayList<>();
    private ArrayList<String> errorLines = new ArrayList<>();
    private int validCount = 0;
    private int errorCount = 0;

    public static void main(String[] args) throws CustomException {
        MountainData mountainData = new MountainData();
        mountainData.readData("data/mountains_db.tsv");
        mountainData.performAnalysis();
        mountainData.writeCleanData("data/mountains_clean.tsv");
        mountainData.writeErrorData("data/mountains_error.tsv");
    }

    public void readData(String filename) throws CustomException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\t");
                if (tokens.length == 6) {
                    String name = tokens[2];
                    String country = tokens[0];
                    String type = tokens[1];
                    String latitudeStr = tokens[3];
                    String longitudeStr = tokens[4];
                    String elevationStr = tokens[5];

                    if ("??????".equals(name) || "??????".equals(country) || "??????".equals(type) ||
                            "??????".equals(latitudeStr) || "??????".equals(longitudeStr)
                            || "??????".equals(elevationStr)) {
                        errorLines.add(line + "\t" + "Contains Question Marks");
                        errorCount++;
                        continue; // Skip this iteration and move to the next line
                    }

                    double latitude = Double.parseDouble(latitudeStr);
                    double longitude = Double.parseDouble(longitudeStr);

                    String[] elevationData = tokens[5].split(" ");
                    double elevation = Double.parseDouble(elevationData[0]);
                    String elevationUnit = elevationData.length > 1 ? elevationData[1] : "m";

                    Mountain mountain = new Mountain(name, country, type, latitude, longitude, elevation,
                            elevationUnit);
                    if (mountain.isValid()) {
                        mountains.add(mountain);
                        validCount++;
                    } else {
                        errorCount++;
                    }
                } else {
                    errorLines.add(line + "\t" + "Incorrect number of columns");
                    errorCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void performAnalysis() {
        Mountain shortestMountain = null;
        Mountain tallestMountain = null;
        double shortestElevationInMeters = Double.MAX_VALUE;
        double tallestElevationInMeters = Double.MIN_VALUE;

        for (Mountain mountain : mountains) {
            double elevationInMeters = mountain.getElevation();
            if ("ft".equals(mountain.getElevationUnit())) {
                elevationInMeters *= 0.3048;
            }

            if (elevationInMeters < shortestElevationInMeters) {
                shortestElevationInMeters = elevationInMeters;
                shortestMountain = mountain;
            }

            if (elevationInMeters > tallestElevationInMeters) {
                tallestElevationInMeters = elevationInMeters;
                tallestMountain = mountain;
            }
        }

        if (shortestMountain != null) {
            System.out.println(
                    "Shortest Mountain: " + shortestMountain.getName() + " (" + shortestElevationInMeters + " meters)");
        }

        if (tallestMountain != null) {
            System.out.println(
                    "Tallest Mountain: " + tallestMountain.getName() + " (" + tallestElevationInMeters + " meters)");
        }
        System.out.println("Total Valid Mountains: " + validCount);
        System.out.println("Total Invalid Mountains: " + errorCount);
    }

    public void writeCleanData(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Mountain mountain : mountains) {
                writer.write(String.format("%s\\t%s\\t%s\\t%f\\t%f\\t%f %s\\n",
                        mountain.getName(),
                        mountain.getCountry(),
                        mountain.getType(),
                        mountain.getLatitude(),
                        mountain.getLongitude(),
                        mountain.getElevation(),
                        mountain.getElevationUnit()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeErrorData(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String errorLine : errorLines) {
                writer.write(errorLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
