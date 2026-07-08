import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class SourceLauncher {
    private static final Path SOURCE_DIR = Paths.get("src");
    private static final Path OUTPUT_DIR = Paths.get("bin");

    private SourceLauncher() {
    }

    public static void run(String mainClassName, String[] args) {
        try {
            compileSources();
            runCompiledMain(mainClassName, args);
        } catch (Exception ex) {
            throw new RuntimeException("Programm konnte nicht gestartet werden.", ex);
        }
    }

    private static void compileSources() throws IOException {
        Files.createDirectories(OUTPUT_DIR);
        List<String> sourceFiles = findJavaSources();
        if (sourceFiles.isEmpty()) {
            throw new IOException("Keine Java-Dateien im src-Verzeichnis gefunden.");
        }

        List<String> compilerArgs = new ArrayList<String>();
        compilerArgs.add("-encoding");
        compilerArgs.add("UTF-8");
        compilerArgs.add("-d");
        compilerArgs.add(OUTPUT_DIR.toString());
        compilerArgs.add("-sourcepath");
        compilerArgs.add(SOURCE_DIR.toString());
        compilerArgs.addAll(sourceFiles);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result;
        if (compiler != null) {
            result = compiler.run(null, null, null, compilerArgs.toArray(new String[0]));
        } else {
            try {
                result = runExternalJavac(compilerArgs);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IOException("Kompilierung wurde unterbrochen.", ex);
            }
        }

        if (result != 0) {
            throw new IOException("Kompilierung der src-Dateien fehlgeschlagen.");
        }
    }

    private static List<String> findJavaSources() throws IOException {
        List<String> sourceFiles = new ArrayList<String>();
        Files.walk(SOURCE_DIR)
            .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".java"))
            .forEach(path -> sourceFiles.add(path.toString()));
        return sourceFiles;
    }

    private static int runExternalJavac(List<String> compilerArgs) throws IOException, InterruptedException {
        List<String> command = new ArrayList<String>();
        command.add("javac");
        command.addAll(compilerArgs);
        Process process = new ProcessBuilder(command)
            .inheritIO()
            .start();
        return process.waitFor();
    }

    private static void runCompiledMain(String mainClassName, String[] args) throws Exception {
        URL[] classpath = new URL[]{OUTPUT_DIR.toUri().toURL()};
        URLClassLoader classLoader = new URLClassLoader(classpath, SourceLauncher.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Class<?> mainClass = Class.forName(mainClassName, true, classLoader);
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw ex;
        }
    }
}
