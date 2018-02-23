package jenjinn.engine.openingdatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jenjinn.engine.boardstate.BoardState;
import jenjinn.engine.moves.ChessMove;

/**
 * @author ThomasB
 * @since 11 Jul 2017
 */
public class OpeningDBv4
{
	/** The source file path assumes the source file is in a source folder! */
	private final List<String> srcpaths = new ArrayList<>();

	public OpeningDBv4(final List<String> filenames)
	{
		srcpaths.addAll(filenames);
	}

	/**
	 * Read through the database file looking for a hashing match. If we find one we
	 * return the recorded move, if we don't a null value is returned.
	 *
	 * @param stateHashing
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public ChessMove getMoveForPosition(final long stateHashing) throws IOException, URISyntaxException
	{
		for (final String s : srcpaths) {
			final File temp = File.createTempFile(s, null);
			final FileOutputStream out = new FileOutputStream(temp);
			copyStream(getClass().getResourceAsStream(s), out);

			try (ZipFile zipsrc = new ZipFile(temp)) {
				final Enumeration<? extends ZipEntry> entries = zipsrc.entries();

				while (entries.hasMoreElements()) {
					final ZipEntry entry = entries.nextElement();
					final InputStream is = zipsrc.getInputStream(entry);

					try (final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
						String line;
						while ((line = reader.readLine()) != null) {
							final String[] components = line.split(" ");

							if (new BigInteger(components[0], 16).longValue() == stateHashing) {
								return ChessMove.fromCompactString(components[1]);
							}
						}
					}
				}
			}
			temp.delete();
		}
		return null;
	}

	public static void copyStream(final InputStream in, final OutputStream out) throws IOException
	{
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public ChessMove getMoveForPosition(final BoardState state) throws IOException
	{
		try {

			return getMoveForPosition(state.getHashing());
		}
		catch (final URISyntaxException e) {
			e.printStackTrace();
			throw new AssertionError();
		}
	}
}
