package com.example.compostify;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {

    public static List<User> filterByProximity(List<User> users, String city, double cityLatitude, double cityLongitude, double radiusInKm) {
        List<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getCityName().equalsIgnoreCase(city)) {
                double distance = calculateDistance(user.getLatLng().getLatitude(), user.getLatLng().getLongitude(), cityLatitude, cityLongitude);
                if (distance <= radiusInKm) {
                    filteredUsers.add(user);
                }
            }
        }

        return filteredUsers;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for calculating distance between two points
        double R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kilometers
    }
}
