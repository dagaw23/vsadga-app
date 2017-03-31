<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title>Konfiguracja pojedynczego symbolu</title>
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
			</table>
		</td>
		<td>
			<table>
			<form:form action="../symbol-new-add.html" method="POST" modelAttribute="currencySymbol">
				<tr>
					<td width="70"/>
					<td align="right" width="220"/>
					<td width="220">
						<form:hidden path="id"/>
					</td>
				</tr>
				<tr>
					<td/>
					<td align="right" width="220">Nazwa symbolu:</td>
					<td width="220">
						<form:input path="symbolName"/>
					</td>
				</tr>
				<tr>
					<td width="70"/>
					<td align="right" width="220">Nazwa kontraktu:</td>
					<td width="220">
						<form:input path="futuresSymbol"/>
					</td>
				</tr>
				<tr>
					<td width="70"/>
					<td align="right" width="220">Aktywność:</td>
					<td width="220">
						<form:checkbox path="isActive"/>
					</td>
				</tr>
				<tr>
					<td/>
					<td/>
					<td>
						<input type="submit" value="Dodaj"/>
					</td>
					
				</tr>
			</form:form>
			</table>
		</td>
	</tr>
	</table>
	
</body>
</html>