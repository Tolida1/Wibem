package com.example.yektv;

import java.util.*;
import java.util.regex.*;

public class M3UParser {
    public static List<Channel> parse(String m3uContent) {
        List<Channel> channelList = new ArrayList<>();
        String[] lines = m3uContent.split("\n");
        Channel current = null;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#EXTINF")) {
                current = new Channel();
                // Grup ve logo bilgisi için regex
                Matcher m = Pattern.compile("group-title=\"([^\"]*)\"").matcher(line);
                if (m.find()) current.group = m.group(1);

                m = Pattern.compile("tvg-logo=\"([^\"]*)\"").matcher(line);
                if (m.find()) current.logo = m.group(1);

                // Kanal adı
                int idx = line.indexOf(",");
                if (idx != -1 && idx < line.length() - 1) {
                    current.name = line.substring(idx + 1).trim();
                } else {
                    current.name = "Bilinmeyen Kanal";
                }
            } else if (line.startsWith("http") && current != null) {
                current.url = line;
                channelList.add(current);
                current = null;
            }
        }
        return channelList;
    }
}
