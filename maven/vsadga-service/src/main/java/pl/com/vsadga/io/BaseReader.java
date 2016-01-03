package pl.com.frxdream.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseReader {
	/**
	 * logger do zapisywania komunikatów
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaseReader.class);

	/**
	 * obiekt buforowania odczytanej informacji
	 */
	private BufferedReader bufferedReader;

	/**
	 * kodowanie znaków przy odczycie
	 */
	private String encode;

	/**
	 * obiekt odczytujący z pliku
	 */
	private FileInputStream fileInputStream;

	/**
	 * obiekt strumienia odczytującego dane
	 */
	private InputStreamReader inputStreamReader;

	/**
	 * Konstruktor ustawiający jedynie kodowanie znaków przy odczycie.
	 * 
	 * @param encode
	 *            kodowanie znaków przy odczycie
	 */
	public BaseReader(String encode) {
		super();
		this.encode = encode;
	}

	/**
	 * Metoda pobiera kodowanie znaków przy odczycie.
	 * 
	 * @return kodowanie znaków przy odczycie
	 */
	public String getEncode() {
		return encode;
	}

	/**
	 * Metoda ustawia kodowanie znaków przy odczycie.
	 * 
	 * @param encode
	 *            kodowanie znaków przy odczycie
	 */
	public void setEncode(String encode) {
		this.encode = encode;
	}

	/**
	 * Metoda zamyka wszystkie wykorzystywane strumienie do odczytu.
	 * 
	 * @throws ReaderException
	 *             błąd w trakcie zamykania jednego ze strumieni
	 */
	protected void closeStreams() throws ReaderException {
		try {
			if (fileInputStream != null)
				fileInputStream.close();

			if (inputStreamReader != null)
				inputStreamReader.close();

			if (bufferedReader != null)
				bufferedReader.close();
		} catch (IOException e) {
			LOGGER.error("closeStreams() - wyjatek IOException!");
			throw new ReaderException(e);
		} catch (Throwable th) {
			LOGGER.error("closeStreams() - wyjatek Throwable!");
			throw new ReaderException(th);
		}
	}

	/**
	 * Metoda otwiera zestaw strumieni wykorzystywanych do operacji odczytu
	 * zawartości pliku. Obiekt pliku {@link File} jest tworzony na podstawie
	 * ścieżki dostępu do pliku (parametr wejściowy metody).
	 * 
	 * @param path
	 *            pełna ścieżka dostępu do otwieranego pliku wraz z nazwą samego
	 *            pliku
	 * @throws ReaderException
	 *             błąd w trakcie otwierania jednego ze strumieni na pliku LUB
	 *             otwierany plik nie istnieje LUB lokalizacja nie wskazuje na
	 *             plik
	 */
	protected void openStreams(final String path) throws ReaderException {
		try {
			fileInputStream = new FileInputStream(getFile(path));
			inputStreamReader = new InputStreamReader(fileInputStream, encode);
			bufferedReader = new BufferedReader(inputStreamReader);

		} catch (FileNotFoundException e) {
			LOGGER.error("openStreams(File) - wyjatek IOException!");
			throw new ReaderException(e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("openStreams(File) - wyjatek UnsupportedEncodingException!");
			throw new ReaderException(e);
		} catch (Throwable th) {
			LOGGER.error("openStreams(File) - wyjatek Throwable!");
			throw new ReaderException(th);
		}
	}

	/**
	 * Metoda tworzy obiekt {@link File} - na podstawie ścieżki dostępu do pliku
	 * z parametru wejściowego. Podczas tworzenia pliku następuje sprawdzenie,
	 * czy ścieżka wskazuje na plik oraz czy istnieje wymagany plik.
	 * 
	 * @param path
	 *            pełna ścieżka dostępu do pliku
	 * @return obiekt otwartego i poprawnego pliku {@link File}
	 * @throws ReaderException
	 *             otwierany plik nie istnieje LUB lokalizacja nie wskazuje na
	 *             plik
	 */
	private File getFile(String path) throws ReaderException {
		File file = new File(path);

		if (!file.exists())
			throw new ReaderException("Plik nie istnieje w lokalizacji ["
					+ file.getAbsolutePath() + "]!");

		if (!file.isFile())
			throw new ReaderException("Wskazana lokalizacja ["
					+ file.getAbsolutePath() + "] nie jest plikiem!");

		return file;
	}

	protected String readLine() throws ReaderException {
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ReaderException("::readLine:: wyjatek IOException!", e);
		}
	}

}
