package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

public class CorpusStructureReader extends DefaultHandler2 {
	// TODO: auslagern
private static final String TAG_REFGROUP = "refGroup";
private static final String TAG_REF = "ref";

	private SCorpusGraph corpusGraph = null;

	public SCorpusGraph getCorpusGraph() {
		return corpusGraph;
	}

	public void setCorpusGraph(SCorpusGraph corpusGraph) {
		this.corpusGraph = corpusGraph;
	}
	
	private SCorpus parentCorpus = null;
	
	public SCorpus getParentCorpus() {
		return parentCorpus;
	}

	public void setParentCorpus(SCorpus parentCorpus) {
		this.parentCorpus = parentCorpus;
	}

	private SDocument currentDoc = null;
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(TAG_REFGROUP.equals(qName)){
			currentDoc = SaltFactory.eINSTANCE.createSDocument();
		}
		else if(TAG_REF.equals(qName)){
			currentDocName = null;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(TAG_REF.equals(qName)){
		currentDoc.setSName(currentDocName);
		getCorpusGraph().addSDocument(parentCorpus, currentDoc);
		}
	}
	
	
	private String currentDocName = null;
	@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i= start; i < start+ length; i++){
			stringBuilder.append(ch[i]);
		}
			currentDocName = stringBuilder.toString();
		}
	
	
}
