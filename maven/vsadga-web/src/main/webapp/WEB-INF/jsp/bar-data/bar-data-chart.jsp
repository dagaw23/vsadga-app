<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title>Lista symboli</title>
	<style type="text/css">
	body {
		background-image: url('images/bg.png');
	}
	</style>
</head>

<body>
	<br>
	<br>

	<table>
	<tr>
		<td>
			<table>
				<tr><td>
					<a href="<c:url value="/alert.html"/>">Print alerts</a>
				</td></tr>
				<tr><td>
					<a href="<c:url value="/show-config-data.html"/>">Parametry konfiguracyjne</a>
				</td></tr>
				<tr><td>
					<a href="<c:url value="/show-symbol-list.html"/>">Lista symboli</a>
				</td></tr>
				<tr><td>
					<a href="<c:url value="/show-frame-list.html"/>">Lista ramek</a>
				</td></tr>
				<tr><td>
					<a href="<c:url value="/show-bar-data-list.html"/>">Lista barów</a>
				</td></tr>
				<tr><td>
					<a href="<c:url value="/show-bar-data-chart.html"/>">Wykres barów</a>
				</td></tr>
			</table>
		</td>
		<td>
			<table>
				<tr>
					<form:form action="bar-data-chart.html" method="POST" modelAttribute="barDataChartModel">
					<td>Ramki czasowe:</td>
					<td>
						<form:select path="frame">
							<form:option value="ALL" label="--- wybierz ---"/>
							<form:options items="${frameList}" itemValue="timeFrameDesc" itemLabel="timeFrame"/>
						</form:select>
					</td>
					<td>Symbole:</td>
					<td>
						<form:select path="symbolId">
							<form:option value="ALL" label="--- wybierz ---"/>
							<form:options items="${symbolList}" itemValue="id" itemLabel="symbolName"/>
						</form:select>
					</td>
					<td>Ilosc:</td>
					<td>
						<form:select path="barCount">
							<form:option value="50" label="25"/>
							<form:option value="50" label="50"/>
							<form:option value="100" label="100"/>
							<form:option value="150" label="150"/>
							<form:option value="200" label="200"/>
							<form:option value="200" label="250"/>
							<form:option value="200" label="300"/>
						</form:select>
					</td>
					<td><input type="submit" name="submit" value="Chart"/></td>
					</form:form>
				</tr>
			</table>
		</td>
	</tr>
	</table>
	
</body>
</html>