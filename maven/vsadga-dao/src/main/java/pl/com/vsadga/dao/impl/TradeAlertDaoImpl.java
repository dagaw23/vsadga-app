package pl.com.vsadga.dao.impl;

import javax.sql.DataSource;

import pl.com.vsadga.dao.JdbcDaoBase;
import pl.com.vsadga.dao.TradeAlertDao;

public class TradeAlertDaoImpl extends JdbcDaoBase implements TradeAlertDao {

	public TradeAlertDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

}
