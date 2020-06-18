package io.github.eroshenkoam.allure;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@CommandLine.Command(
        name = "allure-pdf", mixinStandardHelpOptions = true
)
public class MainCommand implements Runnable {

    @CommandLine.Parameters(
            index = "0",
            description = "The directories with allure result files"
    )
    protected Path reportPath;

    @CommandLine.Option(
            names = {"-o", "--output"},
            defaultValue = "export.pdf",
            description = "Export output directory"
    )
    protected Path outputPath;

    @CommandLine.Option(
            names = {"-n", "--name"},
            defaultValue = "Generated report",
            description = "Report name"
    )
    protected String reportName;

    @CommandLine.Option(names = {"-f", "--filter"})
    Map<String, String> filter;

    @Override
    public void run() {
        try {
            final AllurePDFGenerator generator = new AllurePDFGenerator(reportName, reportPath);
            generator.filter(filter);
            generator.generate(outputPath);
            ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
