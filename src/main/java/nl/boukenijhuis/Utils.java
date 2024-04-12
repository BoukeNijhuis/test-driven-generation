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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Utils {

    private static final String CLASSLOADER_NAME = "generator";

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
     *
     * @param packageName          a period seperated String with the directories to create
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
     *
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

    // TODO: dependencies still necessary? test to see if it works without
    public static CompilationContainer compileFiles(List<String> dependencies, Path... pathArray) throws IOException {

        // build a classpath from the current classpath and given dependencies
        String dependenciesString = String.join(":", dependencies);
        String classPath = System.getProperty("java.class.path") + ":" + dependenciesString;
        List<String> optionList = new ArrayList<String>(Arrays.asList("-classpath", classPath));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<File> compileFileList = Arrays.stream(pathArray).map(Path::toFile).toList();
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(compileFileList);
        var stringWriter = new StringWriter();
        var succesfulCompilation = compiler.getTask(stringWriter, fileManager, null, optionList, null, compilationUnits).call();
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
    public static void addToClassLoader(Path parentDirectory, List<String> dependencies) throws MalformedURLException {
        var urlList = new ArrayList<URL>();
        // the parent directory has to be first in the list (otherwise old implementations could be used)
        urlList.add(new URL("file://" + parentDirectory + "/"));
        urlList.addAll(getUrlList(dependencies));
        URL[] urlArray = urlList.toArray(new URL[0]);

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

        // check if the current class loader is one created by this program
        if (CLASSLOADER_NAME.equals(currentClassLoader.getName())) {
            // if so, use the parent as current class loader
            // TODO contains the assumption that we only created one class loader
            currentClassLoader = currentClassLoader.getParent();
        }

        // Use currentClassLoader as parent, so we extend instead of replace
        ClassLoader newClassLoader = new URLClassLoader(CLASSLOADER_NAME, urlArray, currentClassLoader);

        // set the new classloader as the current classloader
        Thread.currentThread().setContextClassLoader(newClassLoader);
    }

    private static List<URL> getUrlList(List<String> dependencies) {
        // directories NEED a slash at the end
        return dependencies.stream().map(x -> {
            try {
                if (x.endsWith(".jar")) {
                    return new URL("file://" + x);
                } else {
                    // for directories
                    return new URL("file://" + x + "/");
                }
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        }).toList();
    }

    // returns the location where to solution should go (based upon the input file)
    public static Path determineProjectParentFilePath(Path inputFilePath) {
        String parentPathAsString = inputFilePath.getParent().toString();
        String projectPathAsString = parentPathAsString.replace("/test/", "/main/");
        // misses the following case: "/test$"
        projectPathAsString = projectPathAsString.replaceAll("/test$", "/main");
        return Path.of(projectPathAsString);
    }
}
