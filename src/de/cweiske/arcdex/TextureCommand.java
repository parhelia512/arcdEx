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
            File fo = new File(texturecPath + "-" + i + "." + getFileExtension(image));
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
            System.out.println(" Format: " + getFormatName(image.getFormat()));
            System.out.println(" Compression: " + getCompressionTypeName(image.getCompressionType()));
            System.out.println(" Compression flags: " + image.getCompressionFlags());
        }
    }

    protected static String getFormatName(TextureImage.TextureFormat format) {
        switch (format) {
            case TEXTURE_FORMAT_LUMINANCE:
                return "Luminance";
            case TEXTURE_FORMAT_LUMINANCE_ALPHA:
                return "Luminance alpha";

            case TEXTURE_FORMAT_R_BC4:
                return "R BC4";
            case TEXTURE_FORMAT_RG_BC5:
                return "RG BC5";

            case TEXTURE_FORMAT_RGB:
                return "RGB";
            case TEXTURE_FORMAT_RGB_16BPP:
                return "RGB 16bpp";
            case TEXTURE_FORMAT_RGB_BC1:
                return "RGB BC1";
            case TEXTURE_FORMAT_RGB_ETC1:
                return "RGB ETC1";
            case TEXTURE_FORMAT_RGB_PVRTC_2BPPV1:
                return "RGB PVRTC 2BPPv1";
            case TEXTURE_FORMAT_RGB_PVRTC_4BPPV1:
                return "RGB PVRTC 4BPPv1";

            case TEXTURE_FORMAT_RGBA:
                return "RGBA";
            case TEXTURE_FORMAT_RGBA_16BPP:
                return "RGBA 16bpp";
            case TEXTURE_FORMAT_RGBA_ASTC_4x4:
                return "RGBA ASTC 4x4";
            case TEXTURE_FORMAT_RGBA_BC3:
                return "RGBA BC3";
            case TEXTURE_FORMAT_RGBA_BC7:
                return "RGBA BC7";
            case TEXTURE_FORMAT_RGBA_ETC2:
                return "RGBA ETC2";
            case TEXTURE_FORMAT_RGBA_PVRTC_2BPPV1:
                return "RGBA PVRTC 2BPPv1";
            case TEXTURE_FORMAT_RGBA_PVRTC_4BPPV1:
                return "RGBA PVRTC 4BPPv1";
        }

        return "unknown";
    }

    protected static String getCompressionTypeName(TextureImage.CompressionType compressionType) {
        switch (compressionType) {
            case COMPRESSION_TYPE_WEBP: return "webp";
            case COMPRESSION_TYPE_DEFAULT: return "default";
            case COMPRESSION_TYPE_BASIS_ETC1S: return "basis etc1s";
            case COMPRESSION_TYPE_BASIS_UASTC: return "basis UASTC";
            case COMPRESSION_TYPE_WEBP_LOSSY: return "webp lossy";
        }
        return "unknown";
    }

    protected static String getFileExtension(TextureImage.Image image)
    {
        switch (image.getCompressionType()) {
            case COMPRESSION_TYPE_WEBP:
                return "webp";
            case COMPRESSION_TYPE_WEBP_LOSSY:
                return "webp";
            case COMPRESSION_TYPE_BASIS_ETC1S:
                return "basis";
            case COMPRESSION_TYPE_BASIS_UASTC:
                return "basis";
            case COMPRESSION_TYPE_DEFAULT:
                return "unknown";
        }

        return "unknown";
    }
}
