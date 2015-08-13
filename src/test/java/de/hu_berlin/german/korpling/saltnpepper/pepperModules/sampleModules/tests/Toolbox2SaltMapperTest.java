package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.emf.common.util.URI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules.Toolbox2SaltMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules.ToolboxImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules.ToolboxXmlDictionary;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;

/**
 * Maps a Toolbox structure to a Salt {@link SDocumentGraph}.
 * 
 *
 */
public class Toolbox2SaltMapperTest implements ToolboxXmlDictionary {

	ByteArrayOutputStream outStream = null;
	XMLStreamWriter xml = null;
	private final String TAG_UNICODE = "unicode";
	private final String TAG_DATABASE = "database";
	private final String TAG_TEXT1 = "this is an example.";
	private final String TAG_TEXT2 = "second example.";

	private Toolbox2SaltMapper fixture = null;

	public Toolbox2SaltMapper getFixture() {
		return fixture;
	}

	public void setFixture(Toolbox2SaltMapper fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() throws XMLStreamException {
		outStream = new ByteArrayOutputStream();
		XMLOutputFactory outFactory = XMLOutputFactory.newFactory();
		xml = outFactory.createXMLStreamWriter(outStream);
		setFixture(new Toolbox2SaltMapper());
		getFixture().setSDocument(SaltFactory.eINSTANCE.createSDocument());
		getFixture().getSDocument().setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		getFixture().getSDocument().setSName("irengdNName");
		getFixture().setProperties(new ToolboxImporterProperties());
	}

	@After
	public void tearDown() {
		outStream.reset();
	}

	private void start(Toolbox2SaltMapper mapper, String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir")
				+ "/xml2saltTest/");
		tmpDir.mkdirs();
		File tmpFile = new File(tmpDir.getAbsolutePath()
				+ System.currentTimeMillis() + ".xml");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(tmpFile, "UTF-8");
			writer.println(xmlString);
		} finally {
			if (writer != null)
				writer.close();
		}

		this.getFixture().setResourceURI(
				URI.createFileURI(tmpFile.getAbsolutePath()));
		this.getFixture().mapSDocument();
	}

	/**
	 * test for correct generation of primary data with concatenated STextualDS
	 * 
	 * @throws XMLStreamException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void testPrimDataConcatenate() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {
		xml.writeStartDocument();
		xml.writeStartElement(TAG_DATABASE);
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("exampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT1);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("secondExampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT2);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();
		
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		start(getFixture(), outStream.toString());
		
		assertEquals(1, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0));
		assertEquals(TAG_TEXT1 + TAG_TEXT2, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
	}
	
	/**
	 * test for correct generation of primary data with new generated STextualDS for each primary text
	 * 
	 * @throws XMLStreamException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
//	@Test
//	public void testPrimDataConcatenateFalse() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {
//		xml.writeStartDocument();
//		xml.writeStartElement(TAG_DATABASE);
//		xml.writeStartElement(TAG_REF_GROUP);
//		xml.writeStartElement(TAG_REF);
//		xml.writeCharacters("exampleText");
//		xml.writeEndElement();
//		xml.writeStartElement(TAG_UNICODE);
//		xml.writeCharacters(TAG_TEXT1);
//		xml.writeEndElement();
//		xml.writeEndElement();
//		xml.writeStartElement(TAG_REF_GROUP);
//		xml.writeStartElement(TAG_REF);
//		xml.writeCharacters("secondExampleText");
//		xml.writeEndElement();
//		xml.writeStartElement(TAG_UNICODE);
//		xml.writeCharacters(TAG_TEXT2);
//		xml.writeEndElement();
//		xml.writeEndElement();
//		xml.writeEndDocument();
//		xml.flush();
//		
//		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(false);
//		start(getFixture(), outStream.toString());
//		
//		assertEquals(2, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().size());
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0));
//		assertEquals(TAG_TEXT1, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(0).getSText());
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(1));
//		assertEquals(TAG_TEXT2, getFixture().getSDocument().getSDocumentGraph().getSTextualDSs().get(1).getSText());
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens());
//	}
	
	/**
	 * test for correct generation of token with concatenated STextualDS.
	 * 
	 * @throws XMLStreamException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	@Test
	public void testPrimDataConcatenateToken() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {
		xml.writeStartDocument();
		xml.writeStartElement(TAG_DATABASE);
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("exampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT1);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("secondExampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT2);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();
		
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		start(getFixture(), outStream.toString());
		
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0));
		assertEquals("this", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(1));
		assertEquals("is", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(1)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(2));
		assertEquals("an", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(2)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(3));
		assertEquals("example", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(3)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(4));
		assertEquals(".", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(4)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(5));
		assertEquals("second", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(5)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(6));
		assertEquals("example", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(6)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(7));
		assertEquals(".", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(7)));
	
	}
	
	/**
	 * test for correct generation of token with new generated STextualDS for each primary text.
	 * 
	 * @throws XMLStreamException
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
//	@Test
//	public void testPrimDataConcatenateFalseToken() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {
//		xml.writeStartDocument();
//		xml.writeStartElement(TAG_DATABASE);
//		xml.writeStartElement(TAG_REF_GROUP);
//		xml.writeStartElement(TAG_REF);
//		xml.writeCharacters("exampleText");
//		xml.writeEndElement();
//		xml.writeStartElement(TAG_UNICODE);
//		xml.writeCharacters(TAG_TEXT1);
//		xml.writeEndElement();
//		xml.writeEndElement();
//		xml.writeStartElement(TAG_REF_GROUP);
//		xml.writeStartElement(TAG_REF);
//		xml.writeCharacters("secondExampleText");
//		xml.writeEndElement();
//		xml.writeStartElement(TAG_UNICODE);
//		xml.writeCharacters(TAG_TEXT2);
//		xml.writeEndElement();
//		xml.writeEndElement();
//		xml.writeEndDocument();
//		xml.flush();
//		
//		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(false);
//		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
//		start(getFixture(), outStream.toString());
//		
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens());
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0));
//		assertEquals("this", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(1));
//		assertEquals("is", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(1)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(2));
//		assertEquals("an", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(2)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(3));
//		assertEquals("example", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(3)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(4));
//		assertEquals(".", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(4)));
//		// check Tokenizer!
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(5));
//		assertEquals(".", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(5)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(6));
//		assertEquals("second", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(6)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(7));
//		assertEquals("example", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(7)));
//		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(8));
//		assertEquals(".", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(8)));
//	}
	
	@Test
	public void testPrimDataConcatenateFalseTokenFalse() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {
		xml.writeStartDocument();
		xml.writeStartElement(TAG_DATABASE);
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("exampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT1);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("secondExampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT2);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();
		
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(false);
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(false);
		start(getFixture(), outStream.toString());
		
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0));
		assertEquals("this is an example.", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(0)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(1));
		assertEquals("second example.", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSTokens().get(1)));
	}
	
	@Test
	public void testPrimDataConcatenateTokSSpan() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {
		xml.writeStartDocument();
		xml.writeStartElement(TAG_DATABASE);
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("exampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT1);
		xml.writeEndElement();
		xml.writeStartElement(TAG_GLOSS);
		xml.writeCharacters("example Gloss.");
		xml.writeEndElement();
		xml.writeStartElement(TAG_SOUND);
		xml.writeCharacters("exampleSound.wav");
		xml.writeEndElement();
		xml.writeStartElement(TAG_NOTE);
		xml.writeCharacters("This is an example note.");
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeStartElement(TAG_REF_GROUP);
		xml.writeStartElement(TAG_REF);
		xml.writeCharacters("secondExampleText");
		xml.writeEndElement();
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT2);
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();
		
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>)getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(true);
		start(getFixture(), outStream.toString());
		
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSSpans());
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSSpans().get(0));
		assertEquals("this is an example.", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSSpans().get(0)));
		assertNotNull(getFixture().getSDocument().getSDocumentGraph().getSSpans().get(2));
		assertEquals("second example.", getFixture().getSDocument().getSDocumentGraph().getSText(getFixture().getSDocument().getSDocumentGraph().getSSpans().get(3)));
		assertEquals("ref", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(0).getSAnnotations().get(0).getSName());
		assertEquals("exampleText", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(0).getSAnnotations().get(0).getSValue());
		assertEquals("gloss", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(1).getSAnnotations().get(0).getSName());
		assertEquals("example Gloss.", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(1).getSAnnotations().get(0).getSValue());
		assertEquals("note", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(2).getSAnnotations().get(0).getSName());
		assertEquals("This is an example note.", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(2).getSAnnotations().get(0).getSValue());
		assertEquals("ref", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(3).getSAnnotations().get(0).getSName());
		assertEquals("secondExampleText", getFixture().getSDocument().getSDocumentGraph().getSSpans().get(3).getSAnnotations().get(0).getSValue());
		
	}



}
