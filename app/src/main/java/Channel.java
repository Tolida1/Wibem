package com.example.yektv;

public class Channel {
    public String name;
    public String url;
    public String group;    // Kategori (isteğe bağlı)
    public String logo;     // Logo URL (isteğe bağlı)

    // Manuel liste için ek alanlar:
    public String referer;
    public String origin;
    public String userAgent;

    public Channel() {}

    public Channel(String name, String url, String group, String logo) {
        this.name = name;
        this.url = url;
        this.group = group;
        this.logo = logo;
    }
}
