package lip6;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileProcessor {
    private String directoryPath;

    public FileProcessor(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public List<Path> listFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .collect(Collectors.toList());
        }
    }

    public String readFileContent(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath));
    }
}