import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MountainData {
    private int validCount = 0;
    private int errorCount = 0;

    private String shortestMountain = "";
    private String tallestMountain = "";
    private double shortestMountainElevation = Double.MAX_VALUE;
    private double tallestMountainElevation = Double.MIN_VALUE;

    public static void main(String[] args) throws Exception {
        MountainData processor = new MountainData();
        String folder = "data/";
        String inputFile = folder + "mountains_db.tsv";
        String cleanFile = folder + "mountains_clean.tsv";
        String errFile = folder + "mountains_err.tsv";

        try (BufferedReader reader = processor.getBufferedReader(inputFile);
                BufferedWriter cleanWriter = processor.getBufferedWriter(cleanFile);
                BufferedWriter errWriter = processor.getBufferedWriter(errFile)) {

            cleanWriter.write("Country\tType\tMountain\tLatitude\tLongitude\tElevation\n");
            errWriter.write("Country\tType\tMountain\tLatitude\tLongitude\tElevation\tError\n");

            reader.lines().skip(1) // Skip header line
                    .parallel()
                    .forEachOrdered(line -> {
                        try {
                            double elevation = processor.validateRecord(line);
                            try {
                                cleanWriter.write(line + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            processor.validCount++;
                            processor.updateExtremeMountains(line, elevation);
                        } catch (CustomException e) {
                            try {
                                errWriter.write(line + "\t" + e.getErrorType() + "\n");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            processor.errorCount++;
                        }
                    });
            System.out.println(processor.getMetadata());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedReader getBufferedReader(String inputFile) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));
    }

    private BufferedWriter getBufferedWriter(String inputFile) throws IOException {
        return new BufferedWriter(new FileWriter(inputFile));
    }

    public double validateRecord(String line) throws CustomException {
        // Validate column amount
        String[] columns = line.split("\t", -1);
        if (columns.length != 6) {
            // System.out.println("Problematic line: " + line);
            // System.out.println("Actual number of columns: " + columns.length);
            throw new CustomException("IncorrectNumberOfColumns");
        }

        // Validate latitude
        try {
            Double latitude = Double.parseDouble(columns[3]);
            if (latitude < -90 || latitude > 90) {
                throw new CustomException("InvalidLatitude");
            }
        } catch (NumberFormatException e) {
            throw new CustomException("NonNumericLatitude");
        }

        // Validate longitude
        try {
            Double latitude = Double.parseDouble(columns[4]);
            if (latitude < -180 || latitude > 180) {
                throw new CustomException("InvalidLongitude");
            }
        } catch (NumberFormatException e) {
            throw new CustomException("NonNumericLongitude");
        }

        String elevationData = columns[5].trim();
        String elevationValueStr = elevationData.replaceAll("[^0-9.]", "");
        String elevationUnit = elevationData.replaceAll("[0-9.]", "").trim();
        double elevation;

        try {
            // Validate elevation value
            elevation = Double.parseDouble(elevationValueStr);
            if (elevation < 0) {
                throw new CustomException("InvalidElevation");
            }

            // Validate elevation unit
            if (!elevationUnit.equals("m") && !elevationUnit.equals("ft") && !elevationUnit.equals("")) {
                throw new CustomException("InvalidElevationUnit");
            }
        } catch (NumberFormatException e) {
            throw new CustomException("NonNumericElevation");
        }

        // Cover to meters if feet
        if (elevationUnit.equals("ft")) {
            elevation *= 0.3048;
        }
        return elevation;
    }

    public void updateExtremeMountains(String line, double elevation) throws CustomException {
        String[] columns = line.split("\t");

        // Updating the shortest and tallest mountains variables
        if (elevation < shortestMountainElevation) {
            shortestMountainElevation = elevation;
            shortestMountain = columns[2]; // Mountain name
        }

        if (elevation > tallestMountainElevation) {
            tallestMountainElevation = elevation;
            tallestMountain = columns[2]; // Mountain name
        }
    }

    public String getMetadata() {
        return "Valid Records: " + validCount + "\n" +
                "Corrupted Records: " + errorCount + "\n" +
                "Shortest Records: " + shortestMountain + "\n" +
                "Tallest Mountain: " + tallestMountain;

    }

}