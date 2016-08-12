<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
			<td width="130" align="center" style="">Bar Time</td>
			<td width="130" align="center">Bar High</td>
			<td width="130" align="center">Bar Low</td>
			<td width="130" align="center">Bar Close</td>
			<td width="130" align="center">Bar Volume</td>
			<td width="50" align="center">Phase</td>
		</tr>
		
		<c:forEach var="row" items="${dataList}">
			<tr>
				<td><c:out value="${row.barTime}"></c:out></td>
				<td><c:out value="${row.barHigh}"></c:out></td>
				<td><c:out value="${row.barLow}"></c:out></td>
				<td><c:out value="${row.barClose}"></c:out></td>
				<td><c:out value="${row.barVolume}"></c:out></td>
				<td><c:out value="${row.processPhase}"></c:out></td>
			</tr>
		</c:forEach>

	</table>
	<br>
	<br>
	
</body>
</html>