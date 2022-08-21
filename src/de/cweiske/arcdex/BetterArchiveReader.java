package de.cweiske.arcdex;

import com.dynamo.bob.archive.ArchiveEntry;
import com.dynamo.bob.archive.ArchiveReaderProtected;
import com.dynamo.crypt.Crypt;
import com.dynamo.liveupdate.proto.Manifest;
import com.dynamo.liveupdate.proto.Manifest1;
import com.dynamo.liveupdate.proto.Manifest2;
import com.dynamo.liveupdate.proto.Manifest3;
import com.google.protobuf.InvalidProtocolBufferException;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Defold .arcd archive reader with support for compression and decryption
 */
public class BetterArchiveReader extends ArchiveReaderProtected {

    /**
     * Taken from com.dynamo.cr/com.dynamo.cr.bob/src/com/dynamo/bob/archive/ResourceEncryption.java
     */
    protected final byte[] KEY = "aQj8CScgNP4VsfXK".getBytes();
    protected LZ4FastDecompressor decompressor;

    protected Manifest.ManifestData manifestv4;
    protected Manifest3.ManifestData manifestv3;

    protected Manifest2.ManifestData manifestv2;

    protected Manifest1.ManifestData manifestv1;

    public BetterArchiveReader(String archiveIndexFilepath, String archiveDataFilepath, String manifestFilepath) {
        super(archiveIndexFilepath, archiveDataFilepath, manifestFilepath);
        LZ4Factory factory = LZ4Factory.fastestInstance();
        decompressor = factory.fastDecompressor();
    }

    /**
     * Handle both manifest v4 and v3
     */
    protected void readArchiveData() throws IOException {
        // INDEX
        archiveIndexFile.readInt(); // Pad
        archiveIndexFile.readLong(); // UserData, should be 0
        entryCount = archiveIndexFile.readInt();
        entryOffset = archiveIndexFile.readInt();
        hashOffset = archiveIndexFile.readInt();
        hashLength = archiveIndexFile.readInt();

        entries = new ArrayList<ArchiveEntry>(entryCount);

        // Hashes are stored linearly in memory instead of within each entry, so the hashes are read in a separate loop.
        // Once the hashes are read, the rest of the entries are read.

        archiveIndexFile.seek(hashOffset);
        loadManifest();
        // Read entry hashes
        for (int i = 0; i < entryCount; ++i) {
            archiveIndexFile.seek(hashOffset + i * HASH_BUFFER_BYTESIZE);
            ArchiveEntry e = new ArchiveEntry("");
            e.hash = new byte[HASH_BUFFER_BYTESIZE];
            archiveIndexFile.read(e.hash, 0, hashLength);

            if (this.manifestv4 != null) {
                for (Manifest.ResourceEntry resource : manifestv4.getResourcesList()) {
                    if (matchHash(e.hash, resource.getHash().getData().toByteArray(), this.hashLength)) {
                        e.fileName = resource.getUrl();
                        e.relName = resource.getUrl();
                    }
                }

            } else if (this.manifestv3 != null) {
                for (Manifest3.ResourceEntry resource : manifestv3.getResourcesList()) {
                    if (matchHash(e.hash, resource.getHash().getData().toByteArray(), this.hashLength)) {
                        e.fileName = resource.getUrl();
                        e.relName = resource.getUrl();
                    }
                }

            } else if (this.manifestv2 != null) {
                for (Manifest2.ResourceEntry resource : manifestv2.getResourcesList()) {
                    if (matchHash(e.hash, resource.getHash().getData().toByteArray(), this.hashLength)) {
                        e.fileName = resource.getUrl();
                        e.relName = resource.getUrl();
                    }
                }

            } else if (this.manifestv1 != null) {
                for (Manifest1.ResourceEntry resource : manifestv1.getResourcesList()) {
                    if (matchHash(e.hash, resource.getHash().getData().toByteArray(), this.hashLength)) {
                        e.fileName = resource.getUrl();
                        e.relName = resource.getUrl();
                    }
                }
            }

            entries.add(e);
        }

        // Read entries
        archiveIndexFile.seek(entryOffset);
        for (int i=0; i<entryCount; ++i) {
            ArchiveEntry e = entries.get(i);

            e.resourceOffset = archiveIndexFile.readInt();
            e.size = archiveIndexFile.readInt();
            e.compressedSize = archiveIndexFile.readInt();
            e.flags = archiveIndexFile.readInt();
        }
    }

    /**
     * Load manifest 4, fall back to manifest v3 loading
     *
     * v1: https://github.com/defold/defold/commit/b895bb76c4c53b1c65e20534323d7954afaa2358 1.2.97
     * v2: https://github.com/defold/defold/commit/87d79e3ff1486badb55d02020f4b2d07be08cd40 1.2.133
     * v3: https://github.com/defold/defold/commit/17c765d7dcc970b1be616e40449e1f3905263d24 1.2.142
     * v4: https://github.com/defold/defold/commit/4eebea163ced71074f10761ea7774bb2b2b38faf 1.2.183
     */
    protected void loadManifest() throws InvalidProtocolBufferException {
        int version;

        try {
            this.manifestv4 = Manifest.ManifestData.parser().parsePartialFrom(this.manifestFile.getData());
            //ManifestData manifestData = ManifestData.parseFrom(this.manifestFile.getData());
            return;

        } catch (com.google.protobuf.InvalidProtocolBufferException exception) {
            //could not parse manifest file
            Manifest.ManifestData brokenManifestData = (Manifest.ManifestData) exception.getUnfinishedMessage();

            if (!brokenManifestData.hasHeader()) {
                System.err.println("Error: Could not parse manifest. Header missing.");
                System.exit(10);
            }

            if (!brokenManifestData.getHeader().hasVersion()) {
                System.err.println("Error: Could not parse manifest. Version in header missing.");
                System.exit(10);
            }

            version = brokenManifestData.getHeader().getVersion();
            if (version == 4) {
                System.err.println("Error: Could not parse manifest v4. Probably damaged.");
                System.exit(10);
            }
        }

        if (version == 3) {
            //load manifest v3
            this.manifestv3 = Manifest3.ManifestData.parseFrom(this.manifestFile.getData());
            return;

        } else if (version == 2) {
            //load manifest v2
            this.manifestv2 = Manifest2.ManifestData.parseFrom(this.manifestFile.getData());
            return;

        } else if (version == 1) {
            //load manifest v1
            this.manifestv1 = Manifest1.ManifestData.parseFrom(this.manifestFile.getData());
            return;
        }

        System.err.println("Error: Cannot parse manifest v" + version);
        System.exit(10);
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
