package io.github.eroshenkoam.allure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import io.github.eroshenkoam.allure.util.PdfUtil;
import io.qameta.allure.model.Attachment;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.model.TestResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.eroshenkoam.allure.FontHolder.loadArialFont;
import static io.github.eroshenkoam.allure.util.PdfUtil.addEmptyLine;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;

public class AllurePDFGenerator {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    private final String reportName;
    private final Path reportPath;
    private final Map<String, String> filter;
    private final StatusColors statusColors;

    public AllurePDFGenerator(final String reportName, final Path reportPath, final StatusColors statusColors) {
        this.filter = new HashMap<>();
        this.reportName = reportName;
        this.reportPath = reportPath;
        this.statusColors = statusColors;
    }

    public void filter(final Map<String, String> tags) {
        if (tags != null) {
            this.filter.putAll(tags);
        }
    }

    public void generate(final Path outputPath) throws IOException {
        if (!Files.isDirectory(reportPath)) {
            log("Input [%s] is not directory", reportPath.toAbsolutePath());
            return;
        }
        if (Files.notExists(reportPath)) {
            log("Results directory [%s] does not exists", reportPath.toAbsolutePath());
            return;
        }

        if (Files.notExists(outputPath)) {
            log("Creating output file [%s] ...", outputPath.toAbsolutePath());
            Files.createFile(outputPath);
        }

        final FontHolder fontHolder = loadArialFont();
        final List<Path> files = Files.walk(reportPath)
                .filter(s -> s.toString().endsWith("-result.json"))
                .collect(Collectors.toList());
        log("Found [%s] rest results ...", files.size());

        try (final Document document = new Document(PageSize.A4)) {
            PdfWriter.getInstance(document, Files.newOutputStream(outputPath));
            document.newPage();
            document.open();

            addTitlePage(document, reportName, DATE_FORMAT, fontHolder);

            document.newPage();
            final Paragraph tableHeader = new Paragraph("Test Details", fontHolder.header2());
            addEmptyLine(tableHeader, 2);
            document.add(tableHeader);

            for (Path path : files) {
                final TestResult result = new ObjectMapper().readValue(path.toFile(), TestResult.class);
                final Map<String, List<String>> labels = result.getLabels().stream().collect(
                        Collectors.groupingBy(Label::getName, Collectors.mapping(Label::getValue, Collectors.toList()))
                );
                boolean shouldSkip = Objects.nonNull(filter)
                        && !filter.isEmpty()
                        && !filter.entrySet().stream().allMatch(e -> labels.getOrDefault(e.getKey(), new ArrayList<>()).contains(e.getValue()));
                if (shouldSkip) continue;
                printTestResultDetails(document, result, fontHolder);
            }
        }
    }

    private void addTitlePage(final Document document,
                              final String exportName,
                              final DateFormat dateFormat,
                              final FontHolder fontHolder) {
        final Paragraph preface = new Paragraph();
        addEmptyLine(preface, 5);
        preface.add(new Phrase("Allure Report", fontHolder.header1()));
        addEmptyLine(preface, 3);
        preface.add(new Phrase(exportName, fontHolder.header3()));
        addEmptyLine(preface, 2);
        preface.add(new Phrase("Date: ", fontHolder.bold()));
        preface.add(new Phrase(dateFormat.format(new Date()), fontHolder.normal()));
        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);
    }

    private void printTestResultDetails(final Document document,
                                        final TestResult testResult,
                                        final FontHolder fontHolder) {
        final Paragraph details = new Paragraph();
        details.add(PdfUtil.createEmptyLine());
        addTestResultHeader(testResult, fontHolder, details);
        details.add(PdfUtil.createEmptyLine());
        addCustomFieldsSection(testResult, fontHolder, details);
        details.add(PdfUtil.createEmptyLine());
        addSteps(testResult, fontHolder, details);
        document.add(details);
    }

    private void addTestResultHeader(final TestResult testResult, final FontHolder fontHolder,
                                     final Paragraph details) {
        final Chunk testResultName = new Chunk(testResult.getName(), fontHolder.header3());
        final Paragraph testResultStatus = new Paragraph(testResultName);
        testResultStatus.add(String.format("%s", new Phrase(testResult.getStatus().name())));
        details.add(testResultStatus);
    }

    private void addCustomFieldsSection(final TestResult testResult,
                                        final FontHolder fontHolder,
                                        final Paragraph details) {
        if (CollectionUtils.isNotEmpty(testResult.getLabels())) {
            final Map<String, String> labels = testResult.getLabels().stream().collect(
                    groupingBy(Label::getName, mapping(Label::getValue, joining(", ")))
            );
            details.add(new Paragraph("Labels", fontHolder.header4()));
            final com.lowagie.text.List list = new com.lowagie.text.List(false);
            labels.forEach((key, value) -> {
                list.add(new ListItem(String.format("%s: %s", key, value), fontHolder.normal()));
            });
            details.add(list);
        }
    }

    private void addSteps(final TestResult testResult, final FontHolder fontHolder, final Paragraph details) {
        if (Objects.nonNull(testResult.getSteps())) {
            details.add(new Paragraph("Scenario", fontHolder.header4()));
            details.add(createStepsList(testResult.getSteps(), fontHolder));
        }
    }

    private com.lowagie.text.List createStepsList(final List<StepResult> steps, final FontHolder fontHolder) {
        final com.lowagie.text.List stepList = new com.lowagie.text.List(true);
        steps.forEach(step -> {
            final Font font = fontHolder.normal(statusColors.getStatusColor(step.getStatus()));
            final ListItem stepItem = new ListItem(String.format("%s", step.getName()), font);
            final StatusDetails statusDetails = step.getStatusDetails();
            if (Objects.nonNull(statusDetails) && Objects.nonNull(statusDetails.getMessage())) {
                stepItem.add(new Paragraph(statusDetails.getMessage(), font));
            }
            if (Objects.nonNull(step.getSteps())) {
                stepItem.add(createStepsList(step.getSteps(), fontHolder));
            }
            if (Objects.nonNull(step.getAttachments())) {
                final com.lowagie.text.List attachments = new com.lowagie.text.List(false, false);
                for (final Attachment attach : step.getAttachments()) {
                    final String attachmentTitle = String.format("%s (%s)", attach.getName(), attach.getType());
                    final ListItem attachmentItem = new ListItem(attachmentTitle, font);
                    final com.lowagie.text.List content = new com.lowagie.text.List(false);
                    for (final String line : readFile(attach)) {
                        content.add(new ListItem(line.replace("\t", " "), font));
                    }
                    attachmentItem.add(content);
                    attachments.add(attachmentItem);
                }
                stepItem.add(attachments);
            }
            stepList.add(stepItem);
        });
        return stepList;
    }

    private List<String> readFile(final Attachment attachment) {
        final Path file = reportPath.resolve(attachment.getSource());
        try (InputStream stream = Files.newInputStream(file)) {
            return IOUtils.readLines(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void log(String template, Object... values) {
        System.out.println(String.format(template, values));
    }

}
