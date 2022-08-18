package de.cweiske.arcdex;

import com.dynamo.bob.archive.ArchiveEntry;
import com.dynamo.bob.archive.ArchiveReaderProtected;
import com.dynamo.crypt.Crypt;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.IOException;

/**
 * Defold .arcd archive reader with support for compression and decryption
 */
public class BetterArchiveReader extends ArchiveReaderProtected {

    /**
     * Taken from com.dynamo.cr/com.dynamo.cr.bob/src/com/dynamo/bob/archive/ResourceEncryption.java
     */
    protected final byte[] KEY = "aQj8CScgNP4VsfXK".getBytes();
    protected LZ4FastDecompressor decompressor;
    public BetterArchiveReader(String archiveIndexFilepath, String archiveDataFilepath, String manifestFilepath) {
        super(archiveIndexFilepath, archiveDataFilepath, manifestFilepath);
        LZ4Factory factory = LZ4Factory.fastestInstance();
        decompressor = factory.fastDecompressor();
    }

    public byte[] getDecompressedEntryContent(ArchiveEntry entry) throws IOException {
        archiveDataFile.seek(entry.resourceOffset);
        if (entry.compressedSize == ArchiveEntry.FLAG_UNCOMPRESSED) {
            //not compressed
            byte[] buf = new byte[entry.size];
            archiveDataFile.read(buf, 0, entry.size);
            return buf;
        } else {
            //compressed
            byte[] rawBuf = new byte[entry.compressedSize];
            archiveDataFile.read(rawBuf, 0, entry.compressedSize);

            if ((entry.flags & ArchiveEntry.FLAG_ENCRYPTED) > 0) {
                rawBuf = Crypt.decryptCTR(rawBuf, KEY);
            }

            byte[] buf = new byte[entry.size];
            int decompressedLength2 = this.decompressor.decompress(rawBuf, 0, buf, 0, entry.size);
            return buf;
        }
    }

}
