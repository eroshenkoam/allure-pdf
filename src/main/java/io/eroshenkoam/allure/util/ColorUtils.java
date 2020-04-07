package io.eroshenkoam.allure.util;

import io.qameta.allure.model.Status;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ColorUtils {

    public static final Color HEADER_COLOR = new Color(236, 239, 241);

    private static final Map<Status, Color> STATUS_TEXT_COLORS = new HashMap<Status, Color>() {{
        put(Status.PASSED, new Color(120, 182, 60));
        put(Status.FAILED, new Color(255, 38, 2));
        put(Status.BROKEN, new Color(254, 190, 13));
        put(Status.SKIPPED, new Color(136, 136, 136));
    }};

    private ColorUtils() {
        throw new IllegalStateException("Do not instance");
    }

    public static Color statusTextColor(final Status status) {
        return Optional.ofNullable(status)
                .map(STATUS_TEXT_COLORS::get)
                .orElseGet(() -> new Color(191, 152, 166));
    }

}
