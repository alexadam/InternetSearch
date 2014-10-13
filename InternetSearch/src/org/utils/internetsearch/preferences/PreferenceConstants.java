package org.utils.internetsearch.preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String CUSTOM_BROWSER = "custom";
	public static final String EXTERNAL_BROWSER = "external";
	public static final String INTERNAL_BROWSER = "internal";
	public static final String STACKOVERFLOW_COM = "http://stackoverflow.com/search?q=";
	public static final String DUCKDUCKGO_COM = "https://duckduckgo.com/?q=";
	public static final String GOOGLE_COM = "http://www.google.com/search?q=";
	public static final String GITHUB_COM = "https://github.com/search?q={}&type=Code";
	
	public static final String DEFAULT_SEARCH_ENGINE = GOOGLE_COM;
	public static final String DEFAULT_BROWSER = EXTERNAL_BROWSER;
	public static final String DEFAULT_KEYWORDS = "";
	public static final boolean DEFAULT_INSERT_DEFAULT_KEYWORDS = false;
	public static final boolean DEFAULT_USE_ANY_SELECTION = false;
	
	public static final String SELECTED_BROWSER_PREF_NAME = "internetSearch.currentBrowser";
	public static final String CUSTOM_BROWSER_PREF_NAME = "internetSearch.customBrowser";
	public static final String SEARCH_ENGINES_PREF_NAME = "internetSearch.engines";
	public static final String SELECTED_SEARCH_ENGINES_PREF_NAME = "internetSearch.currentEngines";
	public static final String INSERT_KEYWORDS_PREF_NAME = "internetSearch.insertKeywords";
	public static final String KEYWORDS_PREF_NAME = "internetSearch.currentKeywords";
	public static final String USE_ANY_SELECTION_PREF_NAME = "internetSearch.useAnySelection";
	
	public static final List<String> fDefaultEngines = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add(GOOGLE_COM);
			add(DUCKDUCKGO_COM);
			add(STACKOVERFLOW_COM);
			add(GITHUB_COM);
		}
	};
	
}
