package com.cs506.project;

import com.cs506.project.configs.ListenerConfig;
import com.cs506.project.configs.ServerConfig;
import com.cs506.project.configs.WorkerConfig;
import com.cs506.project.server.ProxyServer;
import com.cs506.project.server.ProxyServerListener;
import com.cs506.project.server.ProxyServerWorker;
import com.cs506.project.utils.ArgParser;
import com.cs506.project.utils.Option;
import com.cs506.project.RepositoryController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

/**
 * The main class for the Proxy Server application.
 * This class serves as the entry point for starting and configuring the
 * proxy server.
 * It provides methods for parsing command-line arguments, validating server
 * configuration, and executing the server.
 *
 * @author Mrigank Kumar
 */
public class Main {
    protected static final ArgParser parser;

    // Defaults
    protected static final int NUM_LISTENERS;
    protected static final int NUM_WORKERS;
    protected static final int PORT;
    protected static final int CHUNK_SIZE;
    protected static final boolean AUTO_APPEND;  // End messages with a NUL byte
    protected static final int TIMEOUT;  // In milliseconds

    static {
        /////////////////////// CONFIGURATION PARAMETERS ///////////////////////
        parser = new ArgParser();
        parser.addOption("-h", "boolean", "Display usage information")
              .alias("--help")

              .addOption("-l", "int", "Number of listeners")
              .alias("--num-listeners")

              .addOption("-w", "int", "Number of workers")
              .alias("--num-workers")

              .addOption("-p", "int",
                         "A hint to the port number of the first listener. ")
              .alias("--port-hint")

              .addOption("-P", "string",
                         "A comma separated list of ports to use per listener.")
              .alias("--use-ports")

              .addOption("-s", "int", "Size of the network read/write buffer")
              .alias("--chunk-size")

              .addOption("-a", "boolean",
                         "Add an extra NUL byte to all network writes.")
              .alias("--auto-append-null")

              .addOption("-t", "int",
                         "Read timeout for listeners in milliseconds.")
              .alias("--timeout")

              .addOption("-f", "string", "Path to log file.")
              .alias("--log-file")

              .addOption("-m", "boolean", "Run the mock server.")
              .alias("--mock");


        ////////////////////// SET DEFAULT CONFIGURATION ///////////////////////

        String env;
        if ((env = System.getenv("PROXYSERVER_NUM_LISTENERS")) != null)
            NUM_LISTENERS = Integer.parseInt(env);
        else  // Default to 1 listener
            NUM_LISTENERS = 1;

        if ((env = System.getenv("PROXYSERVER_NUM_WORKERS")) != null)
            NUM_WORKERS = Integer.parseInt(env);
        else   // Default to 1 worker
            NUM_WORKERS = 1;

        if ((env = System.getenv("PROXYSERVER_PORT")) != null)
            PORT = Integer.parseInt(env);
        else  // Default to 8000
            PORT = 8000;

        if ((env = System.getenv("PROXYSERVER_CHUNK_SIZE")) != null)
            CHUNK_SIZE = Integer.parseInt(env);
        else  // Default to 1KB
            CHUNK_SIZE = 0x400;

        if ((env = System.getenv("PROXYSERVER_AUTO_APPEND_NULL")) != null)
            AUTO_APPEND = Boolean.parseBoolean(env);
        else  // Default to 1KB
            AUTO_APPEND = true;

        if ((env = System.getenv("PROXYSERVER_TIMEOUT")) != null)
            // Assume Env Var has timeout in milliseconds
            TIMEOUT = Integer.parseInt(env);
        else // Default to 1 second
            TIMEOUT = 1000;
    } // End static block


    //////////////////////////  PROXY SERVER METHODS  //////////////////////////


    /**
     * Displays a usage message for the Proxy Server
     */
    public static void printHelpMsg() {
        final String prefix = "Usage: java -jar ProxyServer.jar ";
        final int prefixLen = prefix.length();
        final String sep = " | "; // Separator between aliases
        final int maxLen = 80;  // Max line length

        String msg = "";  // Overall usage message
        String line = prefix;  // Current line
        String descriptions = ""; // Option descriptions

        // Convert option set to a sorted array to ensure consistent error
        // messages, as sets are unordered
        String[] options = parser.getOptionsSet().toArray(new String[0]);
        Arrays.sort(options, String::compareToIgnoreCase);

        for (String optionName: options) {
            Option<?> option = parser.getOption(optionName);

            // `sep` separated list of option aliases
            String opts = String.join(sep, option.getAliases());

            // If option has aliases, join option with aliases
            if (!(opts.isBlank() || opts.isEmpty()))
                opts = String.join(sep, optionName, opts);

            // Encapsulate option between brackets
            // Ex. the help option would result in  `[ -h | --help ]`
            opts = "[ " + opts + " ]";

            // If adding the current option overflows max line length,
            // move to the next line
            if (line.length() + opts.length() > maxLen) {
                msg += line + "\n";
                line = " ".repeat(prefixLen);  // Align with previous lines
            }

            // Now we ensure adequate space on the line, add the option
            line += opts + " ";

            // Simultaneously compute the descriptions
            String optionDescription = option.getDescription();
            if (optionDescription == null || optionDescription.isEmpty())
                continue;

            descriptions += " " + optionName + ": " + optionDescription + "\n";
        }

        msg += line + "\n";
        System.out.println(msg);
        System.out.println(descriptions);
    }

    /**
     * Checks if the given port number is a valid port number to bind
     * Note: Valid does not mean available, rather it means it exists.
     *
     * @param  port Port number to test.
     * @return Array of length 2, with index 0 being the port that was tested,
     *         and index 1 with an integer value corresponding to the state of
     *         the port checked. Index 1 can have one of the following values:
     *           {@code -1} if the port is invalid
     *           {@code 0}  if the port number is valid
     *           {@code 1}  if the port is a reserved port
     */
    protected static int[] portCheck(int port) {
        if (port < 0 || port >= 65336)  // Should be in 16 bit range
            return new int[]{port, -1};

        if (port < 1024) // Should not be a reserved port
            return new int[]{port, 1};

        return new int[]{port, 0};  // All good!
    }

    /**
     * Prints an error message if the port numbers are invalid
     *
     * @param  port   The port to use for the error message
     * @param  reason The reason why it's invalid.
     *                See {@link ProxyServer.portCheck(int)}
     *
     * @return  {@code true} if an error was displayed, {@code false} otherwise
     */
    protected static boolean portErrMsg(int port, int reason) {
        if (reason == -1) {
            System.err.println(port + " is not a valid port number.");
            return true;
        } else if (reason == 1) {
            System.err.println("Cannot use port " + port
                             + " because it is a port reserved by the OS.");
            return true;
        }

        return false;
    }

    /**
     * Validates the given server configuration, and displays the reasons for
     * errors on the stderr stream.
     * If there is more than one error, this function will display all of them
     *
     * @param config
     *
     * @return {@code true} if the configuration is valid,
     *         {@code false} otherwise
     */
    public static boolean validateServerConfig(ServerConfig config) {
        boolean error = false;

        // Validate number of listeners
        if (config.numListeners() < 1) {
            error = true;
            System.err.println("Cannot have fewer than 1 listeners.");
        }

        // Validate number of workers
        if (config.numWorkers() < 1) {
            error = true;
            System.err.println("Cannot have fewer than 1 workers.");
        }

        // Validate the port number hint
        int[] portInfo = portCheck(config.portHint());
        if (portInfo[1] != 0 && config.portList() == null) {
            error = true;
            portErrMsg(portInfo[0], portInfo[1]);
        }

        // Validate port list, if any
        if (config.portList() != null) {
            boolean valid = Arrays.stream(config.portList().split(","))
                                  .map(s -> Integer.parseInt(s.trim()))
                                  .map(Main::portCheck)
                                  .map(info -> portErrMsg(info[0], info[1]))
                                  .reduce(true, (acc, val) -> acc && val);
            if (!valid)
                error = true;
        }

        // Validate chunk size
        if (config.chunkSize() <= 0) {
            error = true;
            System.err.println("Chunk size must be positive");
        }

        // Validate read timeout
        if (config.timeout() <= 0) {
            error = true;
            System.err.println("Timeout must be positive");
        }

        // Validate log file path
        if (config.logFilePath() != null) {
            File file = new File(config.logFilePath());

            if (!file.isDirectory())
               file = file.getParentFile();
            else {
                error = true;
                System.err.println(config.logFilePath()
                                 + " already exists and is a directory");
            }

            if (!file.exists()) {
                error = true;
                System.err.println(config.logFilePath()
                                 + " is not a valid filepath");
            }
        }

        return !error;
    }

    public static void main(String[] args) {
        // Parse CLAs
        parser.parse(args);

        // Check for the help option
        if (parser.get("-h")) {
            printHelpMsg();
            return;
        }

        // Check for mock option
        if (parser.get("-m")) {
            MockServer.main(args);
            return;
        }

        // Accept either a port hint or a list or ports, not both
        if (parser.getOption("-p").found() && parser.getOption("-P").found()) {
            System.out.println("Please only specify a port hint or a list of"
                             + " ports.\n"
                             + "Do not specify both --port-hint (-p)"
                             + " and --use-ports (-P).");
            return;
        }

        // Setup server configuration using specified options and defaults
        ServerConfig serverConfig = new ServerConfig(
            parser.getOrDefault("-l", NUM_LISTENERS),
            parser.getOrDefault("-w", NUM_WORKERS),
            parser.getOrDefault("-p", PORT),
            parser.getOrDefault("-P", null),
            parser.getOrDefault("-s", CHUNK_SIZE),
            parser.getOrDefault("-a", AUTO_APPEND),
            parser.getOrDefault("-t", TIMEOUT),
            parser.getOrDefault("-f", null)
        );


        if (!validateServerConfig(serverConfig))
            return;

        ProxyServer server;
        try {
            server = new ProxyServer(serverConfig, new LinkedBlockingQueue<>());
        } catch (IOException e) {
            System.out.println("Failed to initialize the server."
                             + " See stderr for stack trace.");
            e.printStackTrace(System.err);
            return;
        }

        RepositoryController controller = new RepositoryController();
        try {
            server.setup(x -> {
                if ((new String(x)).startsWith("healthcheck"))
                    return "";
                return controller.handleRequest(x);
            });
        } catch (IOException e) {
            System.out.println("Failed to start the server."
                             + " See stderr for stack trace.");
            e.printStackTrace(System.err);
            return;
        }

        server.run();

        try {
            server.waitForTermination();
        } catch (InterruptedException e) {
            System.out.println("Server did not close cleanly!"
                             + " See stderr for stack trace.");
            e.printStackTrace(System.err);
        }
    }
}
