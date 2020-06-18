package io.github.eroshenkoam.allure;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

import java.awt.*;
import java.io.IOException;

public final class FontHolder {

    private static final String ARIAL_FONT = "/assets/fonts/arial.ttf";

    private BaseFont baseFont;

    private FontHolder() {
    }

    public static FontHolder loadArialFont() throws IOException {
        return load(ARIAL_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    public static FontHolder load(final String name, final String encoding, final boolean embedded) throws IOException {
        final FontHolder fontHolder = new FontHolder();
        fontHolder.baseFont = BaseFont.createFont(name, encoding, embedded);
        return fontHolder;
    }

    public Font normal() {
        return normal(null);
    }

    public Font normal(final Color color) {
        return new Font(baseFont, 8, Font.NORMAL, color);
    }

    public Font bold() {
        return bold(null);
    }

    public Font bold(final Color color) {
        return new Font(baseFont, 8, Font.BOLD, color);
    }

    public Font header4() {
        return header4(null);
    }

    public Font header4(final Color color) {
        return new Font(baseFont, 10, Font.BOLD, color);
    }

    public Font header3() {
        return header3(null);
    }

    public Font header3(final Color color) {
        return new Font(baseFont, 14, Font.BOLD, color);
    }

    public Font header2() {
        return header2(null);
    }

    public Font header2(final Color color) {
        return new Font(baseFont, 18, Font.BOLD, color);
    }

    public Font header1() {
        return header1(null);
    }

    public Font header1(final Color color) {
        return new Font(baseFont, 22, Font.BOLD, color);
    }
}
