/*
 * Scilab ( http://www.scilab.org/ ) - This file is part of Scilab
 * Copyright (C) 2015 - Marcos CARDINOT
 *
 * This file must be used under the terms of the CeCILL.
 * This source file is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at
 * http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.txt
 *
 */

package org.scilab.modules.xcos.palette;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * Handles a near real time IndexReader from the IndexWriter.
 * @author Marcos Cardinot <mcardinot@gmail.com>
 */
public final class PaletteSearcher {

    private PaletteSearchManager mgr;
    private QueryParser parser;

    /**
     * Default constructor 
     * @param psm PaletteSearchManager
     */
    public PaletteSearcher(PaletteSearchManager psm) {
        mgr = psm;
        parser = new QueryParser("helpPage", mgr.getAnalyzer());
    }

    /**
     * @param str Query
     * @return paths to the found blocks
     */
    public List<String> search(String str) {
        List<String> blockPaths = new ArrayList<String>();
        try {
            IndexReader reader = DirectoryReader.open(mgr.getIndexWriter(), true);

            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = parser.parse(str);

            final int limit = 100;
            TopDocs results  = searcher.search(query, limit);
            ScoreDoc[] hits = results.scoreDocs;

            // TODO show results on the search view
            int numTotalHits = results.totalHits;
            System.out.println(numTotalHits + " total matching documents");

            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                blockPaths.add(doc.get("treePath") + File.separator + doc.get("blockName"));
                // TODO show results on the search view
                System.out.println(blockPaths.get(i));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return blockPaths;
    }
}