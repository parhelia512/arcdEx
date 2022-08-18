// Copyright 2020-2022 The Defold Foundation
// Copyright 2014-2020 King
// Copyright 2009-2014 Ragnar Svensson, Christian Murray
// Licensed under the Defold License version 1.0 (the "License"); you may not use
// this file except in compliance with the License.
// 
// You may obtain a copy of the License, together with FAQs at
// https://www.defold.com/license
// 
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

package com.dynamo.bob.archive;

import com.dynamo.liveupdate.proto.Manifest.ManifestData;
import com.dynamo.liveupdate.proto.Manifest.ManifestFile;
import com.dynamo.liveupdate.proto.Manifest.ResourceEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copy of ArchiveReader with all private changed to protected
 */
public class ArchiveReaderProtected {
    public static final int VERSION = 4;
    public static final int HASH_BUFFER_BYTESIZE = 64; // 512 bits

    protected ArrayList<ArchiveEntry> entries = null;

    protected int entryCount = 0;
    protected int entryOffset = 0;
    protected int hashOffset = 0;
    protected int hashLength = 0;

    protected final String archiveIndexFilepath;
    protected final String archiveDataFilepath;
    protected final String manifestFilepath;
    protected RandomAccessFile archiveIndexFile = null;
    protected RandomAccessFile archiveDataFile = null;
    protected ManifestFile manifestFile = null;

    public ArchiveReaderProtected(String archiveIndexFilepath, String archiveDataFilepath, String manifestFilepath) {
        this.archiveIndexFilepath = archiveIndexFilepath;
        this.archiveDataFilepath = archiveDataFilepath;
        this.manifestFilepath = manifestFilepath;
    }

    public void read() throws IOException {
    	this.archiveIndexFile = new RandomAccessFile(this.archiveIndexFilepath, "r");
        this.archiveDataFile = new RandomAccessFile(this.archiveDataFilepath, "r");
        
        this.archiveIndexFile.seek(0);
        this.archiveDataFile.seek(0);
        
        if (this.manifestFilepath != null) {
	        InputStream manifestInputStream = new FileInputStream(this.manifestFilepath);
	        this.manifestFile = ManifestFile.parseFrom(manifestInputStream);
	        manifestInputStream.close();
        }

        // Version
        int indexVersion = this.archiveIndexFile.readInt();
        if (indexVersion == ArchiveReaderProtected.VERSION) {
            readArchiveData();
        } else {
            throw new IOException("Unsupported archive index version: " + indexVersion);
        }
    }
    
    protected boolean matchHash(byte[] a, byte[] b, int hlen) {
    	if (a.length < hlen || b.length < hlen) {
    		return false;
    	}
    	
    	for (int i = 0; i < hlen; ++i) {
    		if (a[i] != b[i]) {
    			return false;
    		}
    	}
    	
    	return true;
    }

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
        // Read entry hashes
        for (int i = 0; i < entryCount; ++i) {
            archiveIndexFile.seek(hashOffset + i * HASH_BUFFER_BYTESIZE);
            ArchiveEntry e = new ArchiveEntry("");
            e.hash = new byte[HASH_BUFFER_BYTESIZE];
            archiveIndexFile.read(e.hash, 0, hashLength);

            if (this.manifestFile != null) {
                ManifestData manifestData = ManifestData.parseFrom(this.manifestFile.getData());
                for (ResourceEntry resource : manifestData.getResourcesList()) {
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

    public List<ArchiveEntry> getEntries() {
        return entries;
    }

    public byte[] getEntryContent(ArchiveEntry entry) throws IOException {
        byte[] buf = new byte[entry.size];
        archiveDataFile.seek(entry.resourceOffset);
        archiveDataFile.read(buf, 0, entry.size);

        return buf;
    }

    public void extractAll(String path) throws IOException {

        int entryCount = entries.size();

        System.out.println("Extracting entries to " + path + ": ");
        for (int i = 0; i < entryCount; i++) {
            ArchiveEntry entry = entries.get(i);
            String outdir = path + entry.fileName;
            System.out.println("> " + entry.fileName);
            int readSize = entry.compressedSize;

            // extract
            byte[] buf = new byte[entry.size];
            archiveDataFile.seek(entry.resourceOffset);
            archiveDataFile.read(buf, 0, readSize);

            File fo = new File(outdir);
            fo.getParentFile().mkdirs();
            FileOutputStream os = new FileOutputStream(fo);
            os.write(buf);
            os.close();
        }
    }

    public void close() throws IOException {
        if (archiveIndexFile != null) {
            archiveIndexFile.close();
            archiveIndexFile = null;
        }

        if (archiveDataFile != null) {
            archiveDataFile.close();
            archiveDataFile = null;
        }
    }
}
