import com.wiiudev.gecko.updater.GeckoUpdater;

import javax.swing.*;

public class GeckoUUpdaterClient
{
	public static void main(String[] arguments) throws Exception
	{
		UserInterfaceManager.setSystemLookAndFeelAsynchronously();
		GeckoUpdater.downloadFiles();

		JOptionPane.showMessageDialog(null,
				"Files successfully downloaded!\n" +
						"Please copy the contents of the " + GeckoUpdater.SD_CARD_FOLDER_NAME + " folder " +
						"to the root of your SD Card.\n" +
						"The " + GeckoUpdater.COMPUTER_FOLDER_NAME + " folder stays on your computer " +
						"and contains application(s) that you need to " +
						"run on your computer.\n" +
						"Remember that re-running the updater deletes the " +
						"\"" + GeckoUpdater.DOWNLOADED_FOLDER_NAME + "\"" +
						" folder so do NOT keep important data in there.",
				"Usage Information",
				JOptionPane.INFORMATION_MESSAGE);
	}
}