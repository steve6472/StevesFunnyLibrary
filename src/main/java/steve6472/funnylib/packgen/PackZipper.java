package steve6472.funnylib.packgen;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import steve6472.funnylib.FunnyLib;
import steve6472.funnylib.util.Preconditions;
import steve6472.funnylib.util.Procedure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by steve6472
 * Date: 2/12/2024
 * Project: StevesFunnyLibrary <br>
 */
public class PackZipper
{
	private final PackEngine engine;

	public PackZipper(PackEngine engine)
	{
		this.engine = engine;
	}

	public void zip(Procedure callback)
	{
		CompletableFuture.runAsync(() ->
		{
			try
			{
				// Clear pack-generated
				for (File file : engine.files.generatedRoot.listFiles())
				{
					if (file.isDirectory())
					{
						FileUtils.deleteDirectory(file);
					} else
					{
						FileUtils.delete(file);
					}
				}

				generatePack();

				File outFile = new File(engine.files.finalPackFolder, engine.files.zippedFile.getName());

				if (outFile.exists())
					outFile.delete();

				FileOutputStream fos = new FileOutputStream(outFile);
				ZipOutputStream zipOut = new ZipOutputStream(fos);

				for (File file : engine.files.generatedRoot.listFiles())
				{
					zipFile(file, file.getName(), zipOut);
				}
				zipOut.close();
				fos.close();

				Bukkit.getScheduler().runTask(FunnyLib.getPlugin(), callback::apply);

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}, Executors.newSingleThreadExecutor());
	}

	private void generatePack() throws IOException
	{
		File[] staticFiles = engine.files.staticRoot.listFiles();

		Preconditions.checkNotNull(staticFiles, "Static files are null!");

		// First step: Copy pack/static to pack-generated
		for (File file : staticFiles)
		{
			if (file.isDirectory())
			{
				FileUtils.copyDirectory(file, new File(engine.files.generatedRoot, file.getName()));
			} else
			{
				FileUtils.copyFile(file, new File(engine.files.generatedRoot, file.getName()));
			}
		}

		// Second step: copy all textures
		for (File file : engine.files.packTextures.listFiles())
		{
			if (file.isDirectory())
			{
				FileUtils.copyDirectory(file, new File(engine.files.genCustomTextures, file.getName()));
			} else
			{
				FileUtils.copyFile(file, new File(engine.files.genCustomTextures, file.getName()));
			}
		}

		// Third step: generate models
		engine.models.generate();
	}

	private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException
	{
		// Ignore .zip files (idc if they are not actually zip)
		if (fileToZip.getName().equals(".zip"))
		{
			return;
		}

		// Ignore hidden files
		if (fileToZip.isHidden())
		{
			return;
		}

		// Zip a directory
		if (fileToZip.isDirectory())
		{
			if (fileName.endsWith("/"))
			{
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else
			{
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();

			if (children == null)
				return;

			for (File childFile : children)
			{
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}

		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0)
		{
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
}
