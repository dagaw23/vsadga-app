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
	<br>
	<br>

	<table>
		<tr>
			<td width="70"/>
			<td align="right" width="220">Nazwa:</td>
			<td width="220">${configData.paramName}</td>
		</tr>
		<tr>
			<td width="70"/>
			<td align="right" width="220">Aktualna wartosc:</td>
			<td width="220">${configData.paramValue}</td>
		</tr>
		<form:form action="../../update-config-data.html" method="POST" modelAttribute="configDataModel">
		<tr>
			<td width="70">
				<form:hidden path="paramId"/>
			</td>
			<td align="right" width="220">Nowa wartosc:</td>
			<td width="220">
				<form:input path="paramNewValue"/>
			</td>
		</tr>
		<tr>
			<td/>
			<td/>
			<td>
				<input type="submit" value="Modyfikuj"/>
			</td>
		</tr>
		</form:form>
	</table>
	
	<br>
	<br>
	
</body>
</html>