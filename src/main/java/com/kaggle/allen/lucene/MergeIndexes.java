package com.kaggle.allen.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class MergeIndexes {

    public static final Version LUCENE_VERSION = Version.LUCENE_4_9;

    public static void main(String[] args) throws IOException {
        File notMergedDir = new File("/home/agrigorev/tmp/fullwiki-lucene/not-merged/");

        FSDirectory mergedIndex = FSDirectory.open(new File("/home/agrigorev/tmp/fullwiki-lucene/merged"));

        IndexWriter writer = new IndexWriter(mergedIndex,
                new IndexWriterConfig(LUCENE_VERSION, null).setOpenMode(OpenMode.CREATE));

        File[] indexFiles = notMergedDir.listFiles();

        Directory[] indexes = new Directory[indexFiles.length];
        for (int i = 0; i < indexFiles.length; i++) {
            indexes[i] = FSDirectory.open(indexFiles[i]);
        }

        System.out.println("Merging...");
        writer.addIndexes(indexes);

        System.out.println("Full merge...");
        writer.forceMerge(1);
        writer.close();
        System.out.println("Done.");

    }

}
