package org.tywrapstudios.ctd.compat;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("GrazieInspection")
public class Xaero {
    public static String convertWayPointMessage(String message) {
        List<String> vars = Arrays.stream(message.split(":")).toList();
        if (!message.startsWith("xaero-waypoint")) {
            return message;
        }

        // xaero-waypoint : Point : X : -906 : 64 : -2790 : 12 : false : 0 : Internal-overworld-waypoints
        //          0         1     2     3     4     5      6    7      8       9

        String name = vars.get(1);
        String x = vars.get(3);
        String y = vars.get(4);
        String z = vars.get(5);
        String dimension = getDimension(vars);

        return String.format("Shared Waypoint \"%s\" with Coordinates %s %s %s in %s.", name, x, y, z, dimension);
    }

    private static String getDimension(List<String> vars) {
        String[] parts = vars.get(9).split("-");
        return parts[parts.length - 2];
    }
}
