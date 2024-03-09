package nl.boukenijhuis;

import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.CompilationContainer;
import nl.boukenijhuis.dto.InputContainer;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Utils {

    public static Path createTemporaryFile(InputContainer inputContainer, CodeContainer response) throws IOException {
        Path tempPackageDirectory = inputContainer.getOutputDirectory();
        tempPackageDirectory = createPackageDirectories(response.getPackageName(), tempPackageDirectory);
        Path tempFile = tempPackageDirectory.resolve(response.getFileName());
        // remove the file if it already exists
        Files.deleteIfExists(tempFile);
        Files.createFile(tempFile);
        Files.writeString(tempFile, response.getContent());
        return tempFile;
    }

    /**
     * Create directories corresponding to the package name.
     * @param packageName a period seperated String with the directories to create
     * @param tempPackageDirectory the directory where the new directories should be created
     * @return the last created directory
     * @throws IOException
     */
    private static Path createPackageDirectories(String packageName, Path tempPackageDirectory) throws IOException {
        if (!packageName.isEmpty()) {
            StringTokenizer stringTokenizer = new StringTokenizer(packageName, ".");
            do {
                String directory = stringTokenizer.nextToken();
                tempPackageDirectory = tempPackageDirectory.resolve(directory);
                createDirectory(tempPackageDirectory);
            } while (stringTokenizer.hasMoreTokens());
        }
        return tempPackageDirectory;
    }

    /**
     * Create a directory if it does not exist or do nothing (if it does).
     * @param directory
     * @throws IOException
     */
    private static void createDirectory(Path directory) throws IOException {
        try {
            Files.createDirectory(directory);
        } catch (FileAlreadyExistsException e) {
            // do nothing, because the directory is already there
        }
    }

    public static CompilationContainer compileFiles(Path... pathArray) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<File> compileFileList = Arrays.stream(pathArray).map(Path::toFile).toList();
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(compileFileList);
        var stringWriter = new StringWriter();
        var succesfulCompilation = compiler.getTask(stringWriter, fileManager, null, null, null, compilationUnits).call();
        fileManager.close();
        if (succesfulCompilation) {
            return new CompilationContainer();
        } else {
            return new CompilationContainer(stringWriter.toString());
        }
    }

    public static void compileFilesInDirectory(Path directory) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromPaths(directory);

        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
        fileManager.close();
    }


    // add the compiled files to the classpath
    public static void addToClassLoader(Path parentDirectory) throws MalformedURLException {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        // Use currentClassLoader as parent, so we extend instead of replace
        URL[] urls = {parentDirectory.toFile().toURL()};
        ClassLoader newClassLoader = URLClassLoader.newInstance(urls, currentClassLoader);

        // set the new classloader as the current classloader
        Thread.currentThread().setContextClassLoader(newClassLoader);
    }

    // returns the location where to solution should go (based upon the input file)
    public static Path determineProjectParentFilePath(Path inputFilePath) {
        String parentPathAsString = inputFilePath.getParent().toString();
        String projectPathAsString = parentPathAsString.replace("/test/", "/main/");
        return Path.of(projectPathAsString);
    }
}
