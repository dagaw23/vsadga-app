<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title>Printing all</title>
	<style type="text/css">
	body {
		background-image: url('images/bg.png');
	}
	</style>
</head>

<body>
	<table>
		<tr>
			<td><a href="<c:url value="/audusd.html"/>">AUDUSD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/eurusd.html"/>">EURUSD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/gbpusd.html"/>">GBPUSD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/usdjpy.html"/>">USDJPY</a></td>
			<td>::</td>
			<td><a href="<c:url value="/usdchf.html"/>">USDCHF</a></td>
			<td>::</td>
			<td><a href="<c:url value="/usdcad.html"/>">USDCAD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/gbpcad.html"/>">GBPCAD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/gbpaud.html"/>">GBPAUD</a></td>
		</tr>
		<tr>
			<td><a href="<c:url value="/gold.html"/>">GOLD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/oil.html"/>">OIL</a></td>
		</tr>
	</table>
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
					<form:form action="alert-filter.html" method="POST" modelAttribute="alertDataModel">
					<td>Ramki czasowe:</td>
					<td>
						<form:select path="frameSelected">
							<form:option value="ALL" label="--- wszystkie ---"/>
							<form:options items="${frameList}" itemValue="id" itemLabel="timeFrame"/>
						</form:select>
					</td>
					<td>Symbole:</td>
					<td>
						<form:select path="symbolSelected">
							<form:option value="ALL" label="--- wszystkie ---"/>
							<form:options items="${symbolList}" itemValue="id" itemLabel="symbolName"/>
						</form:select>
					</td>
					<td><input type="submit" name="submit" value="Filtruj"/></td>
					</form:form>
				</tr>
			</table>
			
			<table>
				<c:forEach items="${alertList}" var="alert">
				<tr>
					<td>${alert.alertMessage}</td>
					<td>${alert.alertType}</td>
				</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	</table>

</body>
</html>