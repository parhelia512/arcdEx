package de.cweiske.arcdex;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.dynamo.lua.proto.Lua;

import java.io.*;

@Parameters(separators = "=", commandDescription = "Inspect or extract a .luac file")
public class LuaCommand {
    @Parameter(description = "<.luac file path>", required = true)
    protected String luacPath;

    @Parameter(names = "-i", description = "Show .luac file information. Do not extract.")
    protected boolean info = false;

    @Parameter(names = {"-v", "--verbose"}, description = "Show names of extracted files")
    protected boolean verbose = false;

    public void run() throws IOException {
        if (info) {
            info(luacPath);
        } else {
            extract(luacPath, verbose, "");
        }
    }

    protected static void extract(String luacPath, boolean verbose, String outputPrefix) throws IOException {
        InputStream luacInputStream = new FileInputStream(luacPath);
        Lua.LuaModule luac = Lua.LuaModule.parseFrom(luacInputStream);
        luacInputStream.close();

        File fo = new File(luacPath.substring(0, luacPath.length() - 1));
        if (verbose) {
            System.out.println(outputPrefix + "Writing " + fo.getAbsolutePath());
        }
        FileOutputStream os = new FileOutputStream(fo);
        os.write(luac.getSource().getScript().toByteArray());
    }

    protected void info(String luacPath) throws IOException {
        InputStream luacInputStream = new FileInputStream(luacPath);
        Lua.LuaModule luac = Lua.LuaModule.parseFrom(luacInputStream);
        luacInputStream.close();

        System.out.println("Filename: " + luac.getSource().getFilename());
        System.out.println("Script size: " + luac.getSource().getScript().size());

        if (luac.getModulesCount() > 0) {
            System.out.println("Required modules:");
            for (int i = 0; i < luac.getModulesCount(); i++) {
                System.out.println("- " + luac.getModules(i));
            }
        }

        if (luac.getResourcesCount() > 0) {
            System.out.println("Required resources:");
            for (int i = 0; i < luac.getResourcesCount(); i++) {
                System.out.println("- " + luac.getResources(i));
            }
        }
    }
}
