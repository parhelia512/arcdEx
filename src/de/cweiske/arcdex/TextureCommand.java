package de.cweiske.arcdex;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.dynamo.graphics.proto.Graphics.TextureImage;
import com.google.protobuf.ByteString;

import java.io.*;

@Parameters(separators = "=", commandDescription = "Inspect or extract a texture file")
public class TextureCommand {

    @Parameter(description = "<.texturec file path>", required = true)
    protected String texturecPath;

    @Parameter(names = "-i", description = "Show texture information. Do not extract.")
    protected boolean info = false;

    @Parameter(names = {"-v", "--verbose"}, description = "Show names of extracted files")
    protected boolean verbose = false;

    public void run() throws IOException, SecurityException {
        if (info) {
            info(texturecPath);
        } else {
            extract(texturecPath, verbose, "");
        }
    }

    public static void extract(String texturecPath, boolean verbose, String outputPrefix) throws IOException {
        System.out.println(outputPrefix + "Extracting texturec file: " + texturecPath);
        InputStream textureInputStream = new FileInputStream(texturecPath);
        TextureImage texture = TextureImage.parseFrom(textureInputStream);
        textureInputStream.close();

        for (int i = 0; i < texture.getAlternativesCount(); i++) {
            TextureImage.Image image = texture.getAlternatives(i);

            ByteString data = image.getData();
            File fo = new File(texturecPath + "-" + i);
            if (verbose) {
                System.out.println(outputPrefix + " Writing " + fo.getName());
            }
            FileOutputStream os = new FileOutputStream(fo);
            os.write(data.toByteArray());
        }
    }

    public static void info(String texturecPath) throws IOException {
        InputStream textureInputStream = new FileInputStream(texturecPath);
        TextureImage texture = TextureImage.parseFrom(textureInputStream);
        textureInputStream.close();

        System.out.println("Number of alternatives: " + texture.getAlternativesCount());

        switch (texture.getType()) {
            case TYPE_2D:
                System.out.println("Type: 2D");
                break;
            case TYPE_CUBEMAP:
                System.out.println("Type: Cubemap");
                break;
        }

        for (int i = 0; i < texture.getAlternativesCount(); i++) {
            TextureImage.Image image = texture.getAlternatives(i);
            System.out.println("Alternative #" + i);
            System.out.println(" Size: " + image.getWidth() + "x" + image.getHeight());
            System.out.println(" Original size: " + image.getOriginalWidth() + "x" + image.getOriginalHeight());
            switch (image.getFormat()) {
                case TEXTURE_FORMAT_LUMINANCE: System.out.println(" Format: Luminance"); break;
                case TEXTURE_FORMAT_LUMINANCE_ALPHA: System.out.println(" Format: Luminance alpha"); break;

                case TEXTURE_FORMAT_R_BC4: System.out.println(" Format: R BC4"); break;
                case TEXTURE_FORMAT_RG_BC5: System.out.println(" Format: RG BC5"); break;

                case TEXTURE_FORMAT_RGB: System.out.println(" Format: RGB"); break;
                case TEXTURE_FORMAT_RGB_16BPP: System.out.println(" Format: RGB 16bpp"); break;
                case TEXTURE_FORMAT_RGB_BC1: System.out.println(" Format: RGB BC1"); break;
                case TEXTURE_FORMAT_RGB_ETC1: System.out.println(" Format: RGB ETC1"); break;
                case TEXTURE_FORMAT_RGB_PVRTC_2BPPV1: System.out.println(" Format: RGB PVRTC 2BPPv1"); break;
                case TEXTURE_FORMAT_RGB_PVRTC_4BPPV1: System.out.println(" Format: RGB PVRTC 4BPPv1"); break;

                case TEXTURE_FORMAT_RGBA: System.out.println(" Format: RGBA"); break;
                case TEXTURE_FORMAT_RGBA_16BPP: System.out.println(" Format: RGBA 16bpp"); break;
                case TEXTURE_FORMAT_RGBA_ASTC_4x4: System.out.println(" Format: RGBA ASTC 4x4"); break;
                case TEXTURE_FORMAT_RGBA_BC3: System.out.println(" Format: RGBA BC3"); break;
                case TEXTURE_FORMAT_RGBA_BC7: System.out.println(" Format: RGBA BC7"); break;
                case TEXTURE_FORMAT_RGBA_ETC2: System.out.println(" Format: RGBA ETC2"); break;
                case TEXTURE_FORMAT_RGBA_PVRTC_2BPPV1: System.out.println(" Format: RGBA PVRTC 2BPPv1"); break;
                case TEXTURE_FORMAT_RGBA_PVRTC_4BPPV1: System.out.println(" Format: RGBA PVRTC 4BPPv1"); break;
                default: System.out.println(" Format: ??"); break;
            }

            switch (image.getCompressionType()) {
                case COMPRESSION_TYPE_WEBP: System.out.println(" Compression: webp"); break;
                case COMPRESSION_TYPE_DEFAULT: System.out.println(" Compression: default"); break;
                case COMPRESSION_TYPE_BASIS_ETC1S: System.out.println(" Compression: basis etc1s"); break;
                case COMPRESSION_TYPE_BASIS_UASTC: System.out.println(" Compression: basis UASTC"); break;
                case COMPRESSION_TYPE_WEBP_LOSSY: System.out.println(" Compression: webp lossy"); break;
            }

            System.out.println(" Compression flags: " + image.getCompressionFlags());
        }
    }
}
