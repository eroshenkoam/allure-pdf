package io.eroshenkoam.allure.util;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import java.awt.*;
import java.util.Arrays;

import static io.eroshenkoam.allure.util.ColorUtils.HEADER_COLOR;

public final class PdfUtil {

    private PdfUtil() {
        throw new IllegalStateException("Do not instance");
    }

    public static void addToTable(final PdfPTable table, final String data, final Font font) {
        table.addCell(new Phrase(data, font));
    }

    public static void addEmptyLine(final Paragraph paragraph, final int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(Chunk.NEWLINE);
        }
    }

    public static void addTableHeaders(final PdfPTable table, final Font font, final String... headers) {
        Arrays.stream(headers)
                .map(header -> {
                    final PdfPCell pdfPCell = new PdfPCell(new Phrase(header, font));
                    pdfPCell.setBackgroundColor(HEADER_COLOR);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    return pdfPCell;
                })
                .forEach(table::addCell);
        table.setHeaderRows(1);
    }
}
