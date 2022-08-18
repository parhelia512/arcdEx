package de.cweiske.arcdex;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.IOException;

public class Cli {
    @Parameter(names = {"-h", "--help"}, help = true)
    protected boolean help;

    public static void main(String[] args) throws IOException {
        Cli cli = new Cli();
        ArchiveCommand archive = new ArchiveCommand();
        TextureCommand texture = new TextureCommand();
        JCommander jc = JCommander.newBuilder()
                .addObject(cli)
                .addCommand("archive", archive)
                .addCommand("texture", texture)
                .build();

        jc.setProgramName("arcdex");
        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (cli.help) {
            jc.usage();
            System.exit(0);
        } else if (jc.getParsedCommand() == null) {
            jc.usage();
            System.exit(1);
        }
        switch (jc.getParsedCommand()) {
            case "archive":
                archive.run();
                break;
            case "texture":
                texture.run();
                break;
            default:
                System.out.println("Unknown command: " + jc.getParsedCommand());
                System.exit(1);
        }
    }
}
