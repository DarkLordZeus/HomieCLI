package gsr.Bucket.HomieCLI.cliClass;

import gsr.Bucket.HomieCLI.services.StreamingAI;
import picocli.CommandLine.*;

@Command(name = "ask", mixinStandardHelpOptions = true, version = "1.0",
        description = "Ask an AI a question.")
public class CLIclass implements Runnable {

    @Parameters(index = "0..*", description = "Your question")
    private String[] questionParts;

    @Option(names = {"--joke", "-j"}, description = "Easy funny way") boolean joke;
    @Option(names = {"--prof", "-p"}, description = "Professional way") boolean professional;
    @Option(names = {"--short", "-s"}, description = "Short mode") boolean small;
    @Option(names = {"--code", "-c"}, description = "For code answers") boolean code;
    @Option(names = {"--verbose", "-v"}, description = "Verbose") boolean verbose;

    private final StreamingAI streamingAI;

    public CLIclass(StreamingAI streamingAI) {
        this.streamingAI = streamingAI;
    }

    @Override
    public void run() {
        if (verbose && small) {
            System.err.println("❌ Error: You cannot use --verbose and --small together.");
            System.exit(1);
        }

        if (code && joke) {
            System.err.println("❌ Error: You cannot use --code and --joke together.");
            System.exit(1);
        }

        StringBuilder modeBuilder = new StringBuilder();
        if (joke) modeBuilder.append("tell a joke, ");
        if (code) modeBuilder.append("write code too, ");
        if (professional) modeBuilder.append("make the answer professional, ");
        if (verbose) modeBuilder.append("explain in detailed and verbose way, ");
        if (small) modeBuilder.append("I need a small answer, ");
        if (!modeBuilder.isEmpty()) modeBuilder.setLength(modeBuilder.length() - 2);

        //String prompt = question.joi;
        String prompt = String.join(" ", questionParts);
        if (prompt.trim().isEmpty()) {
            prompt = "joke about JVM";  // your default fallback
        }
        if (!modeBuilder.isEmpty()) prompt += ", " + modeBuilder;

        System.out.print("🤖 AI: ");
        try {
            streamingAI.askStream(prompt);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.exit(0);
    }
}
