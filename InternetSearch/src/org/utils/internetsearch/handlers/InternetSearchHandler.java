package org.utils.internetsearch.handlers;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;
import org.utils.internetsearch.preferences.ISPrefenceManager;
import org.utils.internetsearch.preferences.PreferenceConstants;

public class InternetSearchHandler extends AbstractHandler {

	public InternetSearchHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {
			String selText = null;
			ISelection sel = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();

			Control focusControl = Display.getCurrent().getFocusControl();

			if (focusControl instanceof StyledText) {
				StyledText st = (StyledText) focusControl;
				selText = st.getSelectionText().trim();
			} else if (sel instanceof TextSelection && !sel.isEmpty()) {
				TextSelection selection = (TextSelection) sel;
				selText = selection.getText().trim();
			} else if(ISPrefenceManager.isUseAnySelection()) {
				//if "any selection" search for <selection>.toString()
				ISelection cSel = HandlerUtil.getCurrentSelection(event);
				if (cSel != null)
					selText = cSel.toString();
			}

			if (selText == null || selText.isEmpty()) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Internet Search", "No text selected!");
				return null;
			}

			IWorkbench workbench = PlatformUI.getWorkbench();

			if (workbench == null)
				return null;

			IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();

			if (browserSupport == null)
				return null;

			List<String> engines = ISPrefenceManager.getSelectedEngines();

			if (ISPrefenceManager.getSelectedBrowser().equals(PreferenceConstants.INTERNAL_BROWSER)) {

				for (String engine : engines) {
					IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.STATUS, engine + selText, selText, null);
					URL pUrl = prepareUrl(engine, selText);

					if (pUrl != null)
						browser.openURL(pUrl);

				}
			} else if (ISPrefenceManager.getSelectedBrowser().equals(PreferenceConstants.EXTERNAL_BROWSER)) {
				IWebBrowser webBrowser = browserSupport.getExternalBrowser();
				for (String engine : engines) {
					URL pUrl = prepareUrl(engine, selText);

					if (pUrl != null)
						webBrowser.openURL(pUrl);
				}
			} else if (ISPrefenceManager.getSelectedBrowser().equals(PreferenceConstants.CUSTOM_BROWSER)) {
				for (String engine : engines) {
					URL pUrl = prepareUrl(engine, selText);

					if (pUrl != null)
						Runtime.getRuntime().exec(ISPrefenceManager.getCustomBrowser() + " " + pUrl);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private URL prepareUrl(String engine, String selText) {
		try {
			if (ISPrefenceManager.isInsertKeywords())
				selText = ISPrefenceManager.getKeywords() + " " + selText;
			
			if (!engine.contains("{}"))
				return new URL(engine + URLEncoder.encode(selText, "UTF-8"));

			engine = engine.replace("{}", URLEncoder.encode(selText, "UTF-8"));
			return new URL(engine);
		} catch (Exception e) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Internet Search", "Malformed URL!");
			return null;
		}
	}
}
