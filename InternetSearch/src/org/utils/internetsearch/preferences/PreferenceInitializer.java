package org.utils.internetsearch.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.utils.internetsearch.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		//default browser
		store.setDefault(PreferenceConstants.SELECTED_BROWSER_PREF_NAME, PreferenceConstants.DEFAULT_BROWSER);
		
		//default search engines
		StringBuffer sb = new StringBuffer();

		for (String string : PreferenceConstants.fDefaultEngines)
			sb.append(string).append("\n");

		store.setValue(PreferenceConstants.SEARCH_ENGINES_PREF_NAME, sb.toString());
		
		//default selected search engine
		store.setValue(PreferenceConstants.SELECTED_SEARCH_ENGINES_PREF_NAME, PreferenceConstants.DEFAULT_SEARCH_ENGINE);
		
		//update the internal model
		ISPrefenceManager.refreshPrefences();
	}

}
