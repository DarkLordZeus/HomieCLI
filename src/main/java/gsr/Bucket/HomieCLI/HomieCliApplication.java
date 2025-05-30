package gsr.Bucket.HomieCLI;

import gsr.Bucket.HomieCLI.cliClass.CLIclass;
import gsr.Bucket.HomieCLI.services.StreamingAI;
import picocli.CommandLine;

public class HomieCliApplication {
	public static void main(String[] args) {
		CLIclass cli = new CLIclass(new StreamingAI());
		int exitCode = new CommandLine(cli).execute(args);
		System.exit(exitCode);
	}
}
