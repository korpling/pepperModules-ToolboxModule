/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.corpus_tools.salt.SaltFactory;
import org.eclipse.emf.common.util.URI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperTestUtil;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules.Toolbox2SaltMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules.ToolboxImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules.ToolboxXmlDictionary;

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
		getFixture().setDocument(SaltFactory.createSDocument());
		getFixture().getDocument().setDocumentGraph(SaltFactory.createSDocumentGraph());
		getFixture().getDocument().setName("irengdNName");
		getFixture().setProperties(new ToolboxImporterProperties());
	}

	@After
	public void tearDown() {
		outStream.reset();
	}

	private void start(Toolbox2SaltMapper mapper, String xmlString) throws ParserConfigurationException, SAXException, IOException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir") + "/xml2saltTest/");
		tmpDir.mkdirs();
		File tmpFile = new File(tmpDir.getAbsolutePath() + System.currentTimeMillis() + ".xml");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(tmpFile, "UTF-8");
			writer.println(xmlString);
		} finally {
			if (writer != null)
				writer.close();
		}

		getFixture().setResourceURI(URI.createFileURI(tmpFile.getAbsolutePath()));
		getFixture().mapSDocument();
	}

	public XMLStreamWriter createFirstSample() throws XMLStreamException {
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

		return xml;
	}

	public XMLStreamWriter createSecondSample() throws XMLStreamException {
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

		return xml;
	}

	public XMLStreamWriter createThirdSample() throws XMLStreamException {
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
		xml.writeCharacters(PepperTestUtil.getTestResources() + "exampleSound.mp3");
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
		xml.writeStartElement(TAG_SOUND);
		xml.writeCharacters(PepperTestUtil.getTestResources() + "exampleSound.mp3");
		xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();

		return xml;
	}

	public XMLStreamWriter createFourthSample() throws XMLStreamException {
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
		xml.writeStartElement(TAG_UNICODE);
		xml.writeCharacters(TAG_TEXT2);
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

		return xml;
	}

	public XMLStreamWriter createFifthSample() throws XMLStreamException {
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
		xml.writeCharacters(PepperTestUtil.getTestResources() + "exampleSound.mp3");
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
		xml.writeStartElement(TAG_GLOSS);
		xml.writeCharacters("second example Gloss.");
		xml.writeEndElement();
		// xml.writeStartElement(TAG_SOUND);
		// xml.writeCharacters(PepperTestUtil.getTestResources() + "blub.mp3");
		// xml.writeEndElement();
		xml.writeEndElement();
		xml.writeEndDocument();
		xml.flush();

		return xml;
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

		createFirstSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		start(getFixture(), outStream.toString());

		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0));
		assertEquals(TAG_TEXT1 + TAG_TEXT2, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
	}

	/**
	 * test for correct generation of primary data with new generated STextualDS
	 * for each primary text
	 * 
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testPrimDataConcatenateFalse() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {

		createFirstSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(false);
		start(getFixture(), outStream.toString());

		assertEquals(2, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0));
		assertEquals(TAG_TEXT1, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTextualDSs().get(1));
		assertEquals(TAG_TEXT2, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(1).getText());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens());
	}

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

		createFirstSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(0));
		assertEquals("this", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(1));
		assertEquals("is", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(1)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(2));
		assertEquals("an", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(2)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(3));
		assertEquals("example", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(3)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(4));
		assertEquals(".", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(4)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(5));
		assertEquals("second", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(5)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(6));
		assertEquals("example", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(6)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(7));
		assertEquals(".", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(7)));

	}

	/**
	 * test for correct generation of token with concatenated STextualDS,
	 * without tokenization.
	 * 
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testPrimDataConcatenateTokenFalse() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {

		createFirstSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(false);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(0));
		assertEquals("this is an example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(1));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(1)));
	}

	/**
	 * test for correct generation of token with new generated STextualDS for
	 * each primary text.
	 * 
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testPrimDataConcatenateFalseToken() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {

		createFirstSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(false);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(0));
		assertEquals("this", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(1));
		assertEquals("is", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(1)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(2));
		assertEquals("an", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(2)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(3));
		assertEquals("example", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(3)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(4));
		assertEquals(".", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(4)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(5));
		assertEquals("second", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(5)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(6));
		assertEquals("example", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(6)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(7));
		assertEquals(".", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(7)));
	}

	/**
	 * test for correct generation of token with new generated STextualDS for
	 * each primary text, without tokenization.
	 * 
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testPrimDataConcatenateFalseTokenFalse() throws XMLStreamException, ParserConfigurationException, SAXException, IOException {

		createFirstSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(false);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(false);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(0));
		assertEquals("this is an example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTokens().get(1));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getTokens().get(1)));
	}

	/**
	 * test for correct generation of spans with concatenated STextualDS for
	 * each new annotation.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testPrimDataConcatenateTokSSpan() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createSecondSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(true);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(0));
		assertEquals("this is an example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(3));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(3)));
	}

	/**
	 * test for correct generation of annotations with concatenated STextualDS
	 * for each annotation-tag.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testPrimDataConcatenateTokSSpanAnno() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createSecondSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(true);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getAnnotations());
		
		assertEquals("exampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("ref").getValue());
		assertEquals("example Gloss.", getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotation("gloss").getValue());
		assertEquals("This is an example note.", getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotation("note").getValue());
		assertEquals("secondExampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(3).getAnnotation("ref").getValue());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(3).getAnnotations().size());
		assertEquals(4, getFixture().getDocument().getDocumentGraph().getSpans().size());
	}

	/**
	 * test for correct generation of spans with concatenated STextualDS for
	 * each primary text (only one span for all annotations).
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testPrimDataConcatenateTokSSpanFalse() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createSecondSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(false);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(0));
		assertEquals("this is an example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(1));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(1)));

	}

	/**
	 * test for correct generation of annotations with concatenated STextualDS
	 * for each primary text (only one span for all annotations).
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testPrimDataConcatenateTokSSpanFalseAnno() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createSecondSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(false);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getAnnotations());
		assertEquals("exampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("ref").getValue());
		assertEquals("example Gloss.", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("gloss").getValue());
		assertEquals("This is an example note.", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("note").getValue());
		assertEquals("secondExampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotation("ref").getValue());
		assertEquals(3, getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotations().size());
	}

	/**
	 * test for correct generation of SMedialDSs/ SAudioRelations with
	 * only one span and a concatenated STextualDS for each primary text (only
	 * one span for all annotations).
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testPrimDataConcatenateTokNewSSpanFalseSound() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createThirdSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(true);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getMedialDSs());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getMedialRelations());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getMedialDSs().get(0));
		assertEquals(8, getFixture().getDocument().getDocumentGraph().getMedialRelations().size());
		assertEquals(2, getFixture().getDocument().getDocumentGraph().getMedialDSs().size());
		assertEquals(PepperTestUtil.getTestResources() + "exampleSound.mp3", getFixture().getDocument().getDocumentGraph().getMedialRelations().get(0).getTarget().getMediaReference().toFileString());
		assertEquals(PepperTestUtil.getTestResources() + "exampleSound.mp3", getFixture().getDocument().getDocumentGraph().getMedialRelations().get(1).getTarget().getMediaReference().toFileString());
	}

	/**
	 * test for correct generation of SMedialDSs/ SAudioRelations with
	 * only one span and a concatenated STextualDS for each primary text (only
	 * one span for all annotations).
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testDoubledUnicodeAssociateWithAllToksFalse() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createFourthSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(false);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_ASSOCIATE_WITH_ALL_TOKEN)).setValue(null);
		start(getFixture(), outStream.toString());

		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0));
		assertEquals(TAG_TEXT1 + TAG_TEXT2 + TAG_TEXT2, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans());
		assertEquals(3, getFixture().getDocument().getDocumentGraph().getSpans().size());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(0));
		assertEquals("this is an example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(0)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(1));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(1)));
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(2));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(2)));

		assertNotNull(getFixture().getDocument().getDocumentGraph().getAnnotations());
		assertEquals("exampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("ref").getValue());
		assertEquals("example Gloss.", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("gloss").getValue());
		assertEquals("This is an example note.", getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotation("note").getValue());
		assertEquals("secondExampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotation("ref").getValue());
		assertEquals(2, getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotations().size());
	}

	/**
	 * test for correct generation of SMedialDSs/ SAudioRelations with
	 * only one span and a concatenated STextualDS for each primary text (only
	 * one span for all annotations).
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testDoubledUnicodeAssociateWithAllToks() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createFourthSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(false);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_ASSOCIATE_WITH_ALL_TOKEN)).setValueString("ref, note");
		start(getFixture(), outStream.toString());

		assertEquals(1, getFixture().getDocument().getDocumentGraph().getTextualDSs().size());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0));
		assertEquals(TAG_TEXT1 + TAG_TEXT2 + TAG_TEXT2, getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans());
		assertEquals(5, getFixture().getDocument().getDocumentGraph().getSpans().size());
		assertEquals("this is an example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(0)));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(1)));
		assertEquals("this is an example.second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(2)));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(3)));
		assertEquals("second example.", getFixture().getDocument().getDocumentGraph().getText(getFixture().getDocument().getDocumentGraph().getSpans().get(4)));

		assertNotNull(getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotations());
		assertEquals("example Gloss.", getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotation("gloss").getValue());
		assertEquals("exampleText", getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotation("ref").getValue());
		assertEquals("This is an example note.", getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotation("note").getValue());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(0).getAnnotations().size());
		assertEquals(0, getFixture().getDocument().getDocumentGraph().getSpans().get(1).getAnnotations().size());
		assertEquals(2, getFixture().getDocument().getDocumentGraph().getSpans().get(2).getAnnotations().size());
		assertEquals(0, getFixture().getDocument().getDocumentGraph().getSpans().get(3).getAnnotations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getSpans().get(4).getAnnotations().size());
	}

	/**
	 * test for correct generation of SMedialDSs/ SAudioRelations with
	 * only one span and a concatenated STextualDS for each primary text (only
	 * one span for all annotations).
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	@Test
	public void testNonExistingSound() throws ParserConfigurationException, SAXException, IOException, XMLStreamException {

		createFifthSample();
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_CONCATENATE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_TOKENIZE_TEXT)).setValue(true);
		((PepperModuleProperty<Boolean>) getFixture().getProperties().getProperty(ToolboxImporterProperties.PROP_NEW_SPAN)).setValue(false);
		start(getFixture(), outStream.toString());

		assertNotNull(getFixture().getDocument().getDocumentGraph().getMedialDSs());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getMedialRelations());
		assertNotNull(getFixture().getDocument().getDocumentGraph().getMedialDSs().get(0));
		assertEquals(5, getFixture().getDocument().getDocumentGraph().getMedialRelations().size());
		assertEquals(1, getFixture().getDocument().getDocumentGraph().getMedialDSs().size());
		assertEquals(PepperTestUtil.getTestResources() + "exampleSound.mp3", getFixture().getDocument().getDocumentGraph().getMedialRelations().get(0).getTarget().getMediaReference().toFileString());
	}
}
