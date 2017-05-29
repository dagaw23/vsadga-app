package pl.com.vsadga.processor.impl;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import pl.com.vsadga.processor.DataStreamProcessor;
import pl.com.vsadga.processor.DataStreamProcessorException;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;

public class DataStreamProcessorImpl implements DataStreamProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamProcessorImpl.class);

	@Autowired
	private ConfigDataService configDataService;

	private SyncAPIConnector connector;

	@Override
	public void connect(String user, String passwd) throws DataStreamProcessorException {

		try {
			// create new connector:
			if (connector == null) {
				connector = new SyncAPIConnector(ServerEnum.REAL);
				LOGGER.info("   ::connect:: Connected to the server [" + ServerEnum.REAL + "].");
			}

			// czy połączenie jest już zestawione:
			if (connector.isStreamConnected()) {
				LOGGER.info("   ::connect:: polaczenie juz zestawione [" + connector.isStreamConnected() + "].");
			} else {
				login(user, passwd);
			}

			// ustaw informację o zestawieniu połączenia:
			configDataService.update("DATA_SUBSCRIBE_MODE", "1");

		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::connect:: wyjatek UnknownHostException!", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::connect:: wyjatek IOException!", e);
		} catch (BaseServiceException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::connect:: wyjatek BaseServiceException!", e);
		}
	}

	@Override
	public void disconnect() throws DataStreamProcessorException {
		try {
			// czy jest już połączony:
			if (connector == null) {
				LOGGER.info("   ::disconnect:: Object connector is null [" + connector + "].");

				// ustaw informację o zestawieniu połączenia:
				configDataService.update("DATA_SUBSCRIBE_MODE", "0");
				return;
			}

			// rozłączenie strumienia:
			connector.disconnectStream();

			// zamknięcie połączenia:
			connector.close();
			LOGGER.info("   ::disconnect:: Connection closed.");

			// ustaw informację o zestawieniu połączenia:
			configDataService.update("DATA_SUBSCRIBE_MODE", "0");
		} catch (APICommunicationException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::disconnect:: wyjatek APICommunicationException!", e);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new DataStreamProcessorException("::disconnect:: wyjatek Throwable!", th);
		}
	}

	@Override
	public void subscribe(String symbol) throws DataStreamProcessorException {
		if (connector == null) {
			LOGGER.info("   ::subscribe:: Not connected to the server [" + connector + "].");
			return;
		}

		// czy połączenie jest już zestawione:
		if (!connector.isStreamConnected()) {
			LOGGER.info("   ::subscribe:: polaczenie nie jest zestawione [" + connector.isStreamConnected() + "].");
			return;
		}
		
		try {
			// subskrybcja wg symbolu:
			connector.subscribeCandle(symbol);
			
			// ustaw informację o zestawieniu połączenia:
			configDataService.update("DATA_SUBSCRIBE_MODE", "1");
		} catch (APICommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void unsubscribe(String symbol) throws DataStreamProcessorException {
		if (connector == null) {
			LOGGER.info("   ::unsubscribe:: Not connected to the server [" + connector + "].");
			return;
		}

		// czy połączenie jest już zestawione:
		if (!connector.isStreamConnected()) {
			LOGGER.info("   ::unsubscribe:: polaczenie nie jest zestawione [" + connector.isStreamConnected() + "].");
			return;
		}
		
		try {
			// subskrybcja wg symbolu:
			connector.unsubscribeCandle(symbol);
			
			// ustaw informację o zestawieniu połączenia:
			configDataService.update("DATA_SUBSCRIBE_MODE", "1");
		} catch (APICommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BaseServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String decoratePassword(String password) {

		// odejmij ostatnie 5 znaków z hasła:
		String p1 = password.substring(0, password.length() - 5);

		// odejmij 4 pierwsze znaki z hasła:
		String p2 = p1.substring(4);

		// zmien wielkosc pierwszej litery na duza:

		return p2.substring(0, 1).toUpperCase() + p2.substring(1);
	}

	private String decorateUser(String password) {

		// odejmij ostatnie 5 znaków:
		String p1 = password.substring(0, password.length() - 5);

		// odejmij 4 pierwsze znaki:
		return p1.substring(4);
	}

	private void login(String user, String passwd) throws DataStreamProcessorException {
		Credentials credentials = null;
		LoginResponse loginResponse = null;

		try {
			// Create new credentials
			credentials = new Credentials(decorateUser(user), decoratePassword(passwd));

			// Create and execute new login command
			loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
			LOGGER.info("   ::login:: Login done.");

			// Check if user logged in correctly
			if (loginResponse.getStatus() == true) {

				// Print the message on console
				LOGGER.info("   ::login:: User logged in - with streamSessionId=[" + loginResponse.getStreamSessionId()
						+ "].");

				// połączenie strumieniowe:
				connector.connectStream(new DataStreamListener());
			} else {

				// Print the error on console
				LOGGER.error("   ::login:: User couldn't log in [" + loginResponse.getStatus() + "]!");

			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek UnknownHostException!", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek IOException!", e);
		} catch (APICommunicationException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek APICommunicationException!", e);
		} catch (APICommandConstructionException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek APICommandConstructionException!", e);
		} catch (APIReplyParseException e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek APIReplyParseException!", e);
		} catch (APIErrorResponse e) {
			e.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek APIErrorResponse!", e);
		} catch (Throwable th) {
			th.printStackTrace();
			throw new DataStreamProcessorException("::login:: wyjatek Throwable!", th);
		}

	}
}
