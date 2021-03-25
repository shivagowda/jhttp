package com.shivagowda.jhttp.jhttp.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.shivagowda.jhttp.jhttp.api.HttpRequest;
import com.shivagowda.jhttp.jhttp.api.HttpResponse;
import com.shivagowda.jhttp.jhttp.api.RequestProcessor;
import org.apache.commons.lang3.StringUtils;

/**
 * This processors renders directory listing.
 * 
 *
 */
public class DirectoryListing implements RequestProcessor {

    public boolean process(HttpRequest request, HttpResponse response) {
        final Path directory = request.resolvePath();
        if (!Files.isDirectory(directory)) {
            return false;
        }
        response.setContentType("text/html");

        final String uri = StringUtils.removeEnd(request.getUri(), "/");
        final PrintWriter writer = response.getPrintWriter();
        if (!uri.isEmpty()) {
            appendLink("..", uri, writer);
        }
        try {
            Files.list(directory)
                .map(Path::getFileName)
                .map(Path::toString)
                .forEach(s -> appendLink(s, uri, writer));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    private void appendLink(String fileName, String uri, PrintWriter writer) {
        writer.append(String.format("<li>\n<a href=\"%s/%s\">%s</a>\n</li>\n", uri, fileName, fileName));
    }
}
