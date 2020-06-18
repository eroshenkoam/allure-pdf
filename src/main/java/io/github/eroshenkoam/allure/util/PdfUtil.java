package io.github.eroshenkoam.allure.util;

import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public final class PdfUtil {

    private PdfUtil() {
        throw new IllegalStateException("Do not instance");
    }

    public static void addEmptyLine(final Paragraph paragraph, final int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(createEmptyLine());
        }
    }

    public static Element createEmptyLine() {
        return new Phrase("\n");
    }


}
