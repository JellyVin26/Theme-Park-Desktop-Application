
package dao;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatbotDAO {

    private final TextDocumentParser parser = new TextDocumentParser();

    public Document loadKnowledgeBase() {
        // First try to load from classpath (standard approach)
        Document doc = tryLoadFromClasspath();
        if (doc != null) {
            return doc;
        }

        // If classpath fails, try to load from file system
        doc = tryLoadFromFileSystem();
        if (doc != null) {
            return doc;
        }

        // If both fail, try to use faq.txt as fallback
        doc = tryLoadFallback();
        if (doc != null) {
            return doc;
        }

        throw new IllegalStateException("Cannot find knowledge.txt in classpath or file system. Make sure the file exists and rebuild the project.");
    }

    private Document tryLoadFromClasspath() {
        String[] possiblePaths = {
                "docs/knowledge.txt",
                "/docs/knowledge.txt",
                "knowledge.txt",
                "/knowledge.txt"
        };

        for (String path : possiblePaths) {
            try {
                URL resource = this.getClass().getClassLoader().getResource(path);
                if (resource != null) {
                    System.out.println("Found knowledge base at: " + path + " (classpath)");
                    return UrlDocumentLoader.load(resource, parser);
                }
            } catch (Exception e) {
                System.err.println("Error loading from classpath path " + path + ": " + e.getMessage());
            }
        }

        return null;
    }

    private Document tryLoadFromFileSystem() {
        String[] possiblePaths = {
                "src/main/resources/docs/knowledge.txt",
                "combined-javafx/src/main/resources/docs/knowledge.txt",
                "combined-javafx/combined-javafx/src/main/resources/docs/knowledge.txt",
                "../src/main/resources/docs/knowledge.txt"
        };

        for (String path : possiblePaths) {
            try {
                Path filePath = Paths.get(path);
                if (Files.exists(filePath)) {
                    System.out.println("Found knowledge base at: " + path + " (file system)");
                    return FileSystemDocumentLoader.loadDocument(filePath, parser);
                }
            } catch (Exception e) {
                System.err.println("Error loading from file system path " + path + ": " + e.getMessage());
            }
        }

        return null;
    }

    private Document tryLoadFallback() {
        try {
            URL faqResource = this.getClass().getClassLoader().getResource("docs/faq.txt");
            if (faqResource != null) {
                System.err.println("Using faq.txt as fallback knowledge base");
                return UrlDocumentLoader.load(faqResource, parser);
            }
        } catch (Exception e) {
            System.err.println("Error loading fallback: " + e.getMessage());
        }

        return null;
    }
}