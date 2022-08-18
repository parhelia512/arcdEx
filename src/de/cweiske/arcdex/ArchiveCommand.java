package de.cweiske.arcdex;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.dynamo.bob.archive.ArchiveEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Parameters(separators = "=", commandDescription = "Inspect a Defold .arcd archive")
public class ArchiveCommand {
    @Parameter(description = "<.arcd data file> [files to extract]", required = true)
    protected List<String> arguments = new ArrayList<>();

    protected String dataFilePath;

    protected List<String> filesToExtract;

    @Parameter(names = "--outdir", description = "Directory to extract files to")
    protected String outputDir;

    @Parameter(names = {"-l", "--list"}, description = "List archive contents")
    protected boolean list = false;

    @Parameter(names = "--extract-lua", description = "Extract Lua scripts from .luac files")
    protected boolean extractLua = false;

    @Parameter(names = "--extract-textures", description = "Extract textures from .texturec files")
    protected boolean extractTextures = false;

    @Parameter(names = {"-f", "--filter"}, description = "File extension filter")
    protected String filter;

    @Parameter(names = "--arci", description = ".arci index file path")
    protected String indexFilePath;

    @Parameter(names = "--dmanifest", description = ".dmanifest file path")
    protected String manifestFilePath;

    @Parameter(names = {"-v", "--verbose"}, description = "Show names of extracted files")
    protected boolean verbose = false;

    public void run() throws IOException {
        dataFilePath = arguments.get(0);
        arguments.remove(0);
        filesToExtract = arguments;

        if (!new File(dataFilePath).exists()) {
            error("Error: Data file does not exist: " + dataFilePath);
        }
        if (dataFilePath.lastIndexOf('.') == -1) {
            error("Error: arcd file has no extension");
        }

        String filePrefix = dataFilePath.substring(0, dataFilePath.lastIndexOf('.'));
        if (indexFilePath == null) {
            indexFilePath = filePrefix + ".arci";
        }
        if (!new File(indexFilePath).exists()) {
            error("Error: Index file does not exist: " + indexFilePath);
        }

        if (manifestFilePath == null) {
            manifestFilePath = filePrefix + ".dmanifest";
        }
        if (!new File(manifestFilePath).exists()) {
            error("Error: Manifest file does not exist: " + manifestFilePath);
        }

        BetterArchiveReader ar = new BetterArchiveReader(indexFilePath, dataFilePath, manifestFilePath);
        ar.read();

        if (list) {
            list(ar);
        } else {
            if (outputDir == null) {
                error("Error: no output dir given");
            }
            extract(ar, outputDir);
        }
    }

    public void list(BetterArchiveReader ar)
    {
        System.out.println("Flags: C=compressed, E=encrypted, L=liveupdate");
        System.out.println(
                "      Size"
                + " Compressed"
                + " Flags"
                + " Filename"
        );
        for (ArchiveEntry entry: ar.getEntries()) {
            if (filter != null && !entry.fileName.endsWith(filter)) {
                continue;
            }
            if (filesToExtract.size() > 0 && !filesToExtract.contains(entry.fileName)) {
                continue;
            }

            String flags = "";
            flags += (entry.flags & ArchiveEntry.FLAG_COMPRESSED) > 0 ? "C" : " ";
            flags += (entry.flags & ArchiveEntry.FLAG_ENCRYPTED) > 0 ? "E" : " ";
            flags += (entry.flags & ArchiveEntry.FLAG_LIVEUPDATE) > 0 ? "L" : " ";
            System.out.println(
                    String.format("%10s", entry.size)
                    + " " + String.format("%10s", entry.compressedSize)
                    + " " + String.format("%-5s", flags)
                    + " " + entry.fileName
            );
        }
    }

    public void extract(BetterArchiveReader ar, String outputDir)
    {
        String outputPath;
        for (ArchiveEntry entry: ar.getEntries()) {

            try {
                if (filter != null && !entry.fileName.endsWith(filter)) {
                    continue;
                }
                if (filesToExtract.size() > 0 && !filesToExtract.contains(entry.fileName)) {
                    continue;
                }

                outputPath = outputDir + entry.fileName;
                if (verbose) {
                    System.out.println("Extracting \"" + entry.fileName + "\" to \"" + outputPath + "\"");
                }

                File fo = new File(outputPath);
                if (!fo.getParentFile().exists() && !fo.getParentFile().mkdirs()) {
                    error("Error: Cannot create directory: " + fo.getParentFile().toString());
                }
                FileOutputStream os = new FileOutputStream(fo);
                os.write(ar.getDecompressedEntryContent(entry));
                os.close();

                if (extractTextures && entry.fileName.endsWith(".texturec")) {
                    TextureCommand.extract(fo.getAbsolutePath(), verbose, " ");
                } else if (extractLua && entry.fileName.endsWith(".luac")) {
                    LuaCommand.extract(fo.getAbsolutePath(), verbose, " ");
                }
            } catch (IOException e) {
                System.out.println("Error extracting: " + e.getMessage());
            }
        }
    }

    protected void error(String message) {
        error(message, 1);
    }

    protected void error(String message, int status) {
        System.out.println(message);
        System.exit(status);
    }
}
