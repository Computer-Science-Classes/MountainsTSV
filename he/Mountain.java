public class Mountain {
    private String name;
    private String country;
    private String type;
    private double latitude;
    private double longitude;
    private double elevation;
    private String elevationUnit;

    public Mountain(String name, String country, String type, double latitude, double longitude, double elevation,
            String elevationUnit) {
        this.name = name;
        this.country = country;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.elevationUnit = elevationUnit;
    }

    public boolean isValid() throws CustomException {
        if (latitude < -90 || latitude > 90) {
            throw new CustomException("Invalid Latitude");
        }
        if (longitude < -180 || longitude > 180) {
            throw new CustomException("Invalid Longitude");
        }
        if (elevation < 0) {
            throw new CustomException("Invalid Elevation");
        }
        if (!"m".equals(elevationUnit) && !"ft".equals(elevationUnit)) {
            throw new CustomException("Invalid Elevation Unit");
        }
        if ("??????".equals(name) || "??????".equals(country) || "??????".equals(type) ||
                "??????".equals(Double.toString(latitude)) || "??????".equals(Double.toString(longitude)) ||
                "??????".equals(Double.toString(elevation)) || "??????".equals(elevationUnit)) {
            throw new CustomException("Invalid Entry: therse nothing there");
        }
        return true;
    }

    public String getCountry() {
        return country;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public String getElevationUnit() {
        return elevationUnit;
    }
}
