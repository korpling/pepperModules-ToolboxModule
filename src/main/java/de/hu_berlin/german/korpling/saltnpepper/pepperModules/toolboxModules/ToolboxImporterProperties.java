package de.hu_berlin.german.korpling.saltnpepper.pepperModules.toolboxModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class ToolboxImporterProperties extends PepperModuleProperties {

	public static final String PROP_TEXT_ELEMENT = "textElement";
	public static final String PROP_CONCATENATE_TEXT = "concatenateText";
	public static final String PROP_TOKENIZE_TEXT = "tokenizeText";

	public ToolboxImporterProperties() {
		addProperty(new PepperModuleProperty<>(
				PROP_TEXT_ELEMENT,
				String.class,
				"Defines the name of the xml tag that includes the textual data to be used as primary text. This is set to 'unicode' by default.",
				"unicode"));
		addProperty(new PepperModuleProperty<>(
				PROP_CONCATENATE_TEXT,
				Boolean.class,
				"Defines, if the textual data shall be concatenated or if a new string object shall be created. This is set to 'false' by default.",
				false,false));
		addProperty(new PepperModuleProperty<>(
				PROP_TOKENIZE_TEXT,
				Boolean.class,
				"Defines, if the textual data shall be tokenized. This is set to 'true' by default.",
				true,false));
	}

	public String getPrimaryTextElement() {
		return (String) getProperty(PROP_TEXT_ELEMENT).getValue();
	}

	// konkatenieren?
	public Boolean concatenateText() {
		return (Boolean) getProperty(PROP_CONCATENATE_TEXT).getValue();
	}

	// tokenisieren?
	public Boolean tokenizeText() {
		return (Boolean) getProperty(PROP_TOKENIZE_TEXT).getValue();
	}
}
