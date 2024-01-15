package nl.boukenijhuis;

import nl.boukenijhuis.dto.CodeContainer;
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
import java.util.StringTokenizer;

public class Utils {

    public static Path createTemporaryFile(InputContainer inputContainer, CodeContainer response) throws IOException {
        Path tempPackageDirectory = inputContainer.getOutputDirectory();
        tempPackageDirectory = createPackageDirectories(response.getPackageName(), tempPackageDirectory);
        Path tempFile = tempPackageDirectory.resolve(response.fileName());
        Files.createFile(tempFile);
        Files.writeString(tempFile, response.content());
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
                Files.createDirectory(tempPackageDirectory);
            } while (stringTokenizer.hasMoreTokens());
        }
        return tempPackageDirectory;
    }

    public static void compileFiles(Path... pathArray) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<File> compileFileList = Arrays.stream(pathArray).map(Path::toFile).toList();
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(compileFileList);
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
        fileManager.close();
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

    // find the package name in source code
    public static String getPackageName(String sourceCode) {
        String searchString = "package ";
        int packageStart = sourceCode.indexOf(searchString);
        String packageName = "";

        // there is a package
        if (packageStart >= 0) {
            int packageEnd = sourceCode.indexOf(";", packageStart);
            packageName = sourceCode.substring(packageStart + searchString.length(), packageEnd);
        }

        return packageName;
    }
}
