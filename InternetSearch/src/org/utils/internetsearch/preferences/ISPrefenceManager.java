package org.utils.internetsearch.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.utils.internetsearch.Activator;

public class ISPrefenceManager {
	
	private static List<String> fAllEngines = new ArrayList<String>();
	private static List<String> fSelectedEngines = new ArrayList<String>();
	private static String fSelectedBrowser = PreferenceConstants.DEFAULT_BROWSER;
	private static String fCustomBrowser = "";
	
	public static void refreshPrefences() {
		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		
		//selected browser
		fSelectedBrowser = prefStore.getString(PreferenceConstants.SELECTED_BROWSER_PREF_NAME);

		fCustomBrowser = prefStore.getString(PreferenceConstants.CUSTOM_BROWSER_PREF_NAME);

		//Set all search enignes
		fAllEngines.clear();
		String tmpStr = prefStore.getString(PreferenceConstants.SEARCH_ENGINES_PREF_NAME);
		if (tmpStr.contains("\n"))
			fAllEngines = new ArrayList<String>(Arrays.asList(tmpStr.split("\n")));
		else
			fAllEngines.add(tmpStr);

		//set selected search engines
		fSelectedEngines.clear();
		tmpStr = prefStore.getString(PreferenceConstants.SELECTED_SEARCH_ENGINES_PREF_NAME);
		if (tmpStr.contains("\n"))
			fSelectedEngines = new ArrayList<String>(Arrays.asList(tmpStr.split("\n")));
		else
			fSelectedEngines.add(tmpStr);
	}
	
	public static void savePreferences() {
		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		
		prefStore.setValue(PreferenceConstants.SELECTED_BROWSER_PREF_NAME, fSelectedBrowser);
		
		if (fSelectedBrowser.equals(PreferenceConstants.CUSTOM_BROWSER))
			prefStore.setValue(PreferenceConstants.CUSTOM_BROWSER_PREF_NAME, fCustomBrowser);
		
		StringBuffer sb = new StringBuffer();
		for (String engine : fAllEngines)
			sb.append(engine.trim()).append("\n");
		prefStore.setValue(PreferenceConstants.SEARCH_ENGINES_PREF_NAME, sb.toString().trim());
		
		sb = new StringBuffer();
		for (String engine : fSelectedEngines)
			sb.append(engine.trim()).append("\n");
		prefStore.setValue(PreferenceConstants.SELECTED_SEARCH_ENGINES_PREF_NAME, sb.toString().trim());
	}
	
	public static void setAllToDefaults() {
		setSelectedBrowserToDefault();
		fCustomBrowser = "";
		setSelectedEnginesToDefault();
		setSelectedEnginesToDefault();
	}
	
	public static List<String> getAllEngines() {
		return fAllEngines;
	}
	
	public static void setAllEngines(List<String> engines) {
		fAllEngines.clear();
		if (engines == null)
			setAllEnginesToDefault();
		else
			fAllEngines.addAll(engines);
	}
	
	public static void setAllEnginesToDefault() {
		fAllEngines.clear();
		fAllEngines.addAll(PreferenceConstants.fDefaultEngines);
	}
	
	public static List<String> getSelectedEngines() {
		return fSelectedEngines;
	}
	
	public static void setSelectedEngines(List<String> engines) {
		fSelectedEngines.clear();
		if (engines == null)
			setSelectedEnginesToDefault();
		else
			fSelectedEngines.addAll(engines);
	}
	
	public static void setSelectedEnginesToDefault() {
		fSelectedEngines.clear();
		fSelectedEngines.add(PreferenceConstants.DEFAULT_SEARCH_ENGINE);
	}
	
	public static String getSelectedBrowser() {
		return fSelectedBrowser;
	}
	
	public static void setSelectedBrowser(String browser) {
		if (browser == null || browser.isEmpty())
			setSelectedBrowserToDefault();
		else
			fSelectedBrowser = browser.trim();
	}
	
	public static void setSelectedBrowserToDefault() {
		fSelectedBrowser = PreferenceConstants.DEFAULT_BROWSER;
	}
	
	/**
	 * 
	 * @return if the selected browser is custom, return the custom String
	 */
	public static String getCustomBrowser() {
		return fCustomBrowser;
	}
	
	public static void setCustomBrowser(String browser) {
		if (browser == null)
			fCustomBrowser = "";
		else 
			fCustomBrowser = browser.trim();
	}

}
