package nl.boukenijhuis;

import nl.boukenijhuis.dto.FileNameContent;
import nl.boukenijhuis.dto.InputContainer;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static Path createTemporaryFile(InputContainer inputContainer, FileNameContent response) throws IOException {
        Path tempFile = inputContainer.getOutputDirectory().resolve(response.fileName());
        Files.createFile(tempFile);
        Files.writeString(tempFile, response.content());
        return tempFile;
    }

    public static void compileFiles(Path... pathArray) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<File> compileFileList = Arrays.stream(pathArray).map(Path::toFile).toList();
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(compileFileList);
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
        fileManager.close();
    }

    public static void addToClassLoader(Path inputFile) throws MalformedURLException {
        // add the compiled files to the classpath
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        // Use currentClassLoader as parent, so we extend instead of replace
        File parentDirectory = inputFile.getParent().toFile();
        URL[] urls = {parentDirectory.toURL()};
        ClassLoader newClassLoader = URLClassLoader.newInstance(urls, currentClassLoader);

        // set the new classloader as the current classloader
        Thread.currentThread().setContextClassLoader(newClassLoader);
    }
}
