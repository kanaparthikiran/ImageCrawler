/**
 * 
 */
package com.resto.image.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * @author kkanaparthi
 * 
 */
public class ImageSimpleFileIndexer {

	private static final Logger log = Logger
			.getLogger(ImageSimpleFileIndexer.class.getName());

	/**
	 * 
	 */
	public ImageSimpleFileIndexer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		// TODO Auto-generated method stub
		String toCompare=".*(Hello|Welcome).*";
		
		String subject ="Hello World WelcomeCome ";
		
		String s = "humbapumpa jim";
	   log.info("ONE111 :" + s.matches(".*(jim|joe).*"));
	    s = "humbapumpa jom";
	    log.info("TWO22222 :" +s.matches(".*(jim|joe).*"));
	    s = "humbaPumpa joe";
	    log.info("THREE3333 :" +s.matches(".*(jim|joe).*"));
	    s = "humbapumpa joe jim";
	    log.info("FOUR444444 :" +s.matches(".*(jim|joe).*"));
	    s= "True";
	    log.info("FIVE 555555 :" +s.matches("[tT]rue"));
	    
		log.info("Printing the Result :" + subject.matches(toCompare));
		
		
		HashSet<String> hs = new LinkedHashSet<String>();
		hs.add("one");
		hs.add("two");
		hs.add("one");
		hs.add("three");
		 log.info("The HashSet Size is : "+ hs.size());
		 for(String hsElem: hs) {
			 log.info("hsElem :" + hsElem);
			 
		 }
		// create the Analyser
		StandardAnalyzer standardAnalyzer = new StandardAnalyzer(
				Version.LUCENE_45);
		// create the Index
		Directory directoryIndex = new RAMDirectory();
		String queryString = "Data Structures";
		index(standardAnalyzer, directoryIndex);
		Query luceneQuery = createLuceneQuery(standardAnalyzer, queryString);
		int hitsPerPage = 10;
		IndexReader indexReader = IndexReader.open(directoryIndex);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		ScoreDoc[] searchHits = searchContent(directoryIndex, luceneQuery,
				indexSearcher, hitsPerPage);
		log.info("indexSearcher Here is :" + indexSearcher);
		displayResults(indexSearcher, searchHits);
	}

	/**
	 * 
	 * @param indexWriter
	 * @param documentTitle
	 * @param Id
	 * @throws IOException
	 */
	private static void addDocForIndex(IndexWriter indexWriter,
			String documentTitle, String Id) throws IOException {
		Document document = new Document();
		document.add(new TextField("title", documentTitle, Field.Store.YES));
		document.add(new StringField("id", Id, Field.Store.YES));
		indexWriter.addDocument(document);
	}

	/**
	 * 
	 * @param standardAnalyzer
	 * @param queryString
	 * @return
	 * @throws ParseException
	 */
	private static Query createLuceneQuery(StandardAnalyzer standardAnalyzer,
			String queryString) throws ParseException {
		Query luceneQuery = new QueryParser(Version.LUCENE_45, "title",
				standardAnalyzer).parse(queryString);
		return luceneQuery;
	}

	/**
	 * @throws IOException
	 * 
	 */
	private static ScoreDoc[] searchContent(Directory directoryIndex,
			Query query, IndexSearcher indexSearcher, int hitsPerPage)
			throws IOException {
		TopScoreDocCollector topScoreDocCollector = TopScoreDocCollector
				.create(hitsPerPage, true);
		indexSearcher.search(query, topScoreDocCollector);
		ScoreDoc[] hits = topScoreDocCollector.topDocs().scoreDocs;
		return hits;
	}

	/**
	 * 
	 * @param indexSearcher
	 * @param hits
	 * @throws IOException
	 */
	private static void displayResults(IndexSearcher indexSearcher,
			ScoreDoc[] hits) throws IOException {
		log.info("There are Total :" + hits.length + " Results");
		for (ScoreDoc scoreDocElem : hits) {
			int docId = scoreDocElem.doc;
			log.info("The Document ID Is : " + docId);
			Document document = indexSearcher.doc(docId);
			log.info("There are : " + document.get("id") + " and Title"
					+ document.get("title"));
		}
	}

	/**
	 * 
	 * @return
	 */
	private static void index(StandardAnalyzer standardAnalyzer,
			Directory directoryIndex) throws IOException {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_45, standardAnalyzer);
		IndexWriter indexWriter = null;
		indexWriter = new IndexWriter(directoryIndex, indexWriterConfig);
		addDocForIndex(indexWriter, "Lucene In Action", "1587587658723");
		addDocForIndex(indexWriter, "Lucene For Dummies", "421343454354355");
		addDocForIndex(indexWriter, "Data Strucure With Jav Lucene", "32342554");
		indexWriter.close();
	}
}
