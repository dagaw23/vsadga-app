<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
					<td width="70"/>
					<td width="150" align="center" style="">Nazwa symbolu</td>
					<td width="150" align="center">Symbol z kontraktu</td>
					<td width="150" align="center">Aktywność</td>
					<td width="70"/>
					<td width="70"/>
				</tr>
				<c:forEach var="row" items="${symbolList}">
				<tr>
					<td/>
					<td><c:out value="${row.symbolName}"></c:out></td>
					<td><c:out value="${row.futuresSymbol}"></c:out></td>
					<td><c:out value="${row.isActive}"></c:out></td>
					<td>
						<spring:url value="symbol-edit/${row.id}/update.html" var="updateUrl"/>
						<button onclick="location.href='${updateUrl}'">Modyfikuj</button>
					</td>
					<td>
						<spring:url value="symbol-edit/${row.id}/delete.html" var="deleteUrl"/>
						<button onclick="location.href='${deleteUrl}'">Usuń</button>
					</td>
				</tr>
				</c:forEach>
				<tr>
					<td/>
					<td>
						<spring:url value="symbol-edit/add.html" var="addUrl"/>
						<button onclick="location.href='${addUrl}'">Dodaj nowy</button>
					</td>
					<td/>
					<td/>
					<td/>
					<td/>
				</tr>
			</table>
		</td>
	</tr>
	</table>
	
</body>
</html>