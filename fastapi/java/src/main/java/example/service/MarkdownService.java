package example.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class MarkdownService {

    private static final Pattern LINK = Pattern.compile("\\[([^\\]]+)\\]\\(([^)]+)\\.md\\)");

    /** Rewrites [label](slug.md) links to <b>Label</b>, in place. */
    public void mdLinksToHtml(Path file) {
        try {
            String text = Files.readString(file);
            String result = LINK.matcher(text)
                    .replaceAll(m -> Matcher.quoteReplacement("<b>" + title(m.group(1)) + "</b>"));
            Files.writeString(file, result);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Mimics Python's str.title(): capitalise each run of letters, lowercase the rest. */
    static String title(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        boolean startOfWord = true;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(startOfWord ? Character.toUpperCase(c) : Character.toLowerCase(c));
                startOfWord = false;
            } else {
                sb.append(c);
                startOfWord = true;
            }
        }
        return sb.toString();
    }
}
