package pl.com.vsadga.dao;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class JdbcDaoBase {
	/**
	 * obiekt wspierający wywoływanie zapytań SQL
	 */
	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedParamJdbcTemplate;
	

	protected JdbcDaoBase(DataSource dataSource) {
		super();
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * @return the jdbcTemplate
	 */
	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}


	/**
	 * Zwraca jdbcTemplate z możliwością nazwanych parametrów
	 * @return the jdbcTemplate
	 */
	protected NamedParameterJdbcTemplate getNamedParamJdbcTemplate() {
		return namedParamJdbcTemplate;
	}
	
	/**
	 * Metoda sprawdza, czy przesłany obiekt do sprawdzenia - zawiera wartość <code>NULL</code>
	 * (jeśli tak jest - zgłaszany jest wyjątek typu {@link DaoException}).
	 * 
	 * @param field
	 *            sprawdzany obiekt pod względem wartości <code>NULL</code>
	 * @param message
	 *            część komunikatu o błędzie
	 * @throws DaoBaseException
	 *             jeśli parametr wejściowy metody - jest równy <code>null</code>
	 */
	protected void validateNotNull(final Object field, final String message) throws DaoBaseException {
		validateNotNull(field, message, null);
	}

	/**
	 * Metoda sprawdza, czy przesłany obiekt do sprawdzenia - zawiera wartość <code>NULL</code>
	 * (jeśli tak jest - zgłaszany jest wyjątek typu {@link DaoException}).
	 * 
	 * @param field
	 *            sprawdzany obiekt pod względem wartości <code>NULL</code>
	 * @param message
	 *            część komunikatu o błędzie
	 * @param fieldName
	 *            nazwa sprawdzanego parametru (opcjonalnie)
	 * @throws DaoBaseException
	 *             jeśli parametr wejściowy metody - jest równy <code>null</code>
	 */
	protected void validateNotNull(final Object field, final String message, final String fieldName)
			throws DaoBaseException {
		if (field != null)
			return;

		StringBuilder sb = new StringBuilder();

		if (message != null)
			sb.append(message);

		sb.append(": w parametrze wejsciowym ");

		if (fieldName != null)
			sb.append("[" + fieldName + "] ");

		sb.append("jest NULL!");

		throw new DaoBaseException(sb.toString());
	}

	/**
	 * Metoda sprawdza, czy przesłany obiekt do sprawdzenia - należy do zbioru 
	 * dopuszczalnych wartości
	 * (jeśli nie - zgłaszany jest wyjątek typu {@link DaoBaseException}).
	 * 
	 * @param field
	 *            sprawdzany obiekt pod względem dopuszczalnych wartości
	 * @param message
	 *            część komunikatu o błędzie
	 * @param fieldName
	 *            nazwa sprawdzanego parametru (opcjonalnie)
	 * @param fieldValues
	 *            tablica dopuszczalnych wartości dla sprawdzanego parametru
	 * @throws DaoBaseException
	 *             jeśli parametr wejściowy metody - jest równy <code>null</code>
	 */
	protected <T> void validateForValues(T field, String message, String fieldName, T[] fieldValues) 
			throws DaoBaseException {

		for (T availValue : fieldValues) {
			if (availValue.equals(field)) {return;}
		}
		
		StringBuilder sb = new StringBuilder();
		if (message != null) {sb.append(message);}

		sb.append(": w parametrze wejsciowym ");
		if (fieldName != null) {sb.append("[" + fieldName + "] ");}

		sb.append(" - nie nalezy do zbioru " + Arrays.toString(fieldValues));

		throw new DaoBaseException(sb.toString());
		
	}
}
