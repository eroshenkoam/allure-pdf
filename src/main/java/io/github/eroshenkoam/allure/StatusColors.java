package io.github.eroshenkoam.allure;

import io.qameta.allure.model.Status;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StatusColors {

    private final Map<Status, String> statusColors;

    public StatusColors() {
        statusColors = new HashMap<Status, String>() {{
            put(Status.PASSED, "#97cc64");
            put(Status.FAILED, "#fd5a3e");
            put(Status.BROKEN, "#ffd050");
            put(Status.SKIPPED, "#aaaaaa");
        }};
    }

    public void setStatusColors(final Status status, String color) {
        statusColors.put(status, color);
    }

    public Color getStatusColor(final Status status) {
        final String hex = Optional.ofNullable(status)
                .map(statusColors::get)
                .orElse("#BF98A6FF");
        return Color.decode(hex);
    }


}
