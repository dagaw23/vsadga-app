<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
			<td width="150" align="center" style="">Param Name</td>
			<td width="250" align="center">Param Value</td>
			<td width="70"/>
		</tr>
		
		<c:forEach var="row" items="${configList}">
			<tr>
				<td/>
				<td><c:out value="${row.paramName}"></c:out></td>
				<td><c:out value="${row.paramValue}"></c:out></td>
				<td>
					<spring:url value="config-data/${row.id}/update.html" var="updateUrl"/>
					<button onclick="location.href='${updateUrl}'">Modyfikuj</button>
				</td>
			</tr>
		</c:forEach>

	</table>
	<br>
	<br>
	
</body>
</html>