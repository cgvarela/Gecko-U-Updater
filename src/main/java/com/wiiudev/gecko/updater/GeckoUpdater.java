package com.wiiudev.gecko.updater;

import com.wiiudev.gecko.updater.utilities.DownloadingUtilities;
import com.wiiudev.gecko.updater.utilities.FileUtilities;
import com.wiiudev.gecko.updater.utilities.ProgramDirectoryUtilities;
import com.wiiudev.gecko.updater.utilities.Zipping;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeckoUpdater
{
	private static final Path DOWNLOADED_DIRECTORY = Paths.get(ProgramDirectoryUtilities.getProgramDirectory() + File.separator + "Downloaded");
	private static final Path COMPUTER_DIRECTORY = DOWNLOADED_DIRECTORY.resolve("Computer");
	public static final Path SD_CARD_DIRECTORY = DOWNLOADED_DIRECTORY.resolve("SD Card");

	public static void downloadFiles() throws Exception
	{
		// FileUtilities.deleteFolder(DOWNLOADED_DIRECTORY.toFile());

		// Create the necessary folders
		Path jGeckoUDirectory = COMPUTER_DIRECTORY.resolve("JGecko U");
		Files.createDirectories(jGeckoUDirectory);

		Path wiiUDirectory = SD_CARD_DIRECTORY.resolve("wiiu");
		Path appsFolder = wiiUDirectory.resolve("apps");
		Path tcpGeckoFolder = appsFolder.resolve("tcpgecko");
		Files.createDirectories(tcpGeckoFolder);

		Runtime runtime = Runtime.getRuntime();
		int threadPoolSize = runtime.availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
		ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(pool);
		List<Future<String>> tasks = new ArrayList<>();

		String tcpGeckoSourceRepositoryURL = "https://github.com/BullyWiiPlaza/tcpgecko/";
		String tcpGeckoMasterRepositoryURL = tcpGeckoSourceRepositoryURL + "blob/master/";

		tasks.add(completionService.submit(() ->
		{
			String homeBrewLauncherDownloadURL = "https://github.com/dimok789/homebrew_launcher/releases/download/1.4/homebrew_launcher.v1.4.zip";
			Path downloadedHomeBrewLauncherArchive = DownloadingUtilities.download(homeBrewLauncherDownloadURL);
			Files.move(downloadedHomeBrewLauncherArchive, SD_CARD_DIRECTORY.resolve(downloadedHomeBrewLauncherArchive), StandardCopyOption.REPLACE_EXISTING);
			downloadedHomeBrewLauncherArchive = SD_CARD_DIRECTORY.resolve(downloadedHomeBrewLauncherArchive);
			Zipping.extract(downloadedHomeBrewLauncherArchive.toString(), SD_CARD_DIRECTORY.toString());
			Files.delete(downloadedHomeBrewLauncherArchive);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			String jGeckoUURL = "https://github.com/BullyWiiPlaza/JGeckoU/blob/master/JGecko%20U.jar";
			Path downloadedJGeckoU = DownloadingUtilities.downloadRaw(jGeckoUURL);
			Files.move(downloadedJGeckoU, jGeckoUDirectory.resolve(downloadedJGeckoU.getFileName()), StandardCopyOption.REPLACE_EXISTING);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			String iconURL = tcpGeckoMasterRepositoryURL + "meta/icon.png";
			Path iconFile = DownloadingUtilities.downloadRaw(iconURL);
			Files.move(iconFile, tcpGeckoFolder.resolve(iconFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);

			return null;
		}));

		tasks.add(completionService.submit(() ->
		{
			String metaXML = tcpGeckoMasterRepositoryURL + "meta/meta.xml";
			Path metaXMLFilePath = DownloadingUtilities.downloadRaw(metaXML);
			Files.move(metaXMLFilePath, tcpGeckoFolder.resolve(metaXMLFilePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);

			return null;
		}));

		/*tasks.add(completionService.submit(() ->
		{
			Path codeHandlerBinaries;

			try
			{
				// Prioritize CosmoCortney's website because it's the most up-to-date
				codeHandlerBinaries = DownloadingUtilities.download("http://cosmocortney.ddns.net/wiiu_tools/codehandler.bin");
			} catch (Exception exception)
			{
				// As a fallback use my GitHub
				codeHandlerBinaries = DownloadingUtilities.downloadRaw(tcpGeckoMasterRepositoryURL + "codehandler.bin");
			}

			Files.move(codeHandlerBinaries, tcpGeckoFolder.resolve(codeHandlerBinaries.getFileName()));

			return null;
		}));*/

		tasks.add(completionService.submit(() ->
		{
			String tcpGeckoInstaller = tcpGeckoMasterRepositoryURL + "tcpgecko.elf";
			Path downloadedTCPGeckoInstaller = DownloadingUtilities.downloadRaw(tcpGeckoInstaller);
			Files.move(downloadedTCPGeckoInstaller, tcpGeckoFolder.resolve(downloadedTCPGeckoInstaller.getFileName()), StandardCopyOption.REPLACE_EXISTING);

			return null;
		}));

		// Wait for all tasks to finish
		int tasksCount = tasks.size();
		for (int tasksIndex = 0; tasksIndex < tasksCount; tasksIndex++)
		{
			completionService.take().get();
		}

		pool.shutdown();
	}
}