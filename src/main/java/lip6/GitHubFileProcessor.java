package lip6;

import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GitHubFileProcessor {
    private String repoUrl;
    private GitHub github;
    private GHRepository repository;

    public GitHubFileProcessor(String repoUrl, String token) throws IOException {
        this.repoUrl = repoUrl;
        this.github = new GitHubBuilder().withOAuthToken(token).build();
        this.repository = github.getRepository(repoUrl);
    }
    public List<GHContent> listJavaFiles() throws IOException {
        return listJavaFilesRecursive("");
    }

    private List<GHContent> listJavaFilesRecursive(String path) throws IOException {
        List<GHContent> javaFiles = new ArrayList<>();
        List<GHContent> contents = repository.getDirectoryContent(path);
    System.out.println("contents : " + contents.size());
        for (GHContent content : contents) {
            if (content.isFile() && content.getPath().endsWith(".java")) {
                javaFiles.add(content);
            } else if (content.isDirectory()) {
                javaFiles.addAll(listJavaFilesRecursive(content.getPath()));
            }
        }

        return javaFiles;
    }

    public String readFileContent(GHContent file) throws IOException {
        System.out.println("file : " + file.getPath());
        return file.getContent();
    }
}