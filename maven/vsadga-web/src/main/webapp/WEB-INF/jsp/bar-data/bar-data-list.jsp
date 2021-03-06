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
					<form:form action="${pageContext.request.contextPath}/bar-data-filter.html" method="POST" modelAttribute="barDataModel">
					<td>Ramki czasowe:</td>
					<td>
						<form:select path="frame">
							<form:option value="ALL" label="--- wybierz ---"/>
							<form:options items="${frameList}" itemValue="timeFrameDesc" itemLabel="timeFrameDesc"/>
						</form:select>
					</td>
					<td>Symbole:</td>
					<td>
						<form:select path="symbolId">
							<form:option value="ALL" label="--- wybierz ---"/>
							<form:options items="${symbolList}" itemValue="id" itemLabel="symbolName"/>
						</form:select>
					</td>
					<td><input type="submit" name="submit" value="Filtruj"/></td>
					</form:form>
				</tr>
			</table>
			<table>
				<tr>
					<td width="120">Czas</td>
					<td width="120">Min</td>
					<td width="120">Max</td>
					<td width="120">Zamkniecie</td>
					
					<td width="120">Wolumen</td>
					<td width="40">Typ</td>
					<td width="40">Faza</td>
					<td width="40">Bar</td>
					
					<td width="40">Wskaznik</td>
					<td width="40">Trend</td>
					<td width="40">Waga</td>
					
					<td width="40">Absorb</td>
					<td width="40">Rozmiar</td>
				</tr>
				<c:forEach var="row" items="${barDataList}">
				<tr>
					<td><c:out value="${row.barTime}"></c:out></td>
					<td><c:out value="${row.barLow}"></c:out></td>
					<td><c:out value="${row.barHigh}"></c:out></td>
					<td><c:out value="${row.barClose}"></c:out></td>
					
					<td><c:out value="${row.barVolume}"></c:out></td>
					<td><c:out value="${row.volumeType}"></c:out></td>
					<td><c:out value="${row.processPhase}"></c:out></td>
					<td><c:out value="${row.barType}"></c:out></td>
					
					<td><c:out value="${row.indicatorNr}"></c:out></td>
					<td><c:out value="${row.trendIndicator}"></c:out></td>
					<td><c:out value="${row.trendWeight}"></c:out></td>
					
					<td><c:out value="${row.volumeAbsorb}"></c:out></td>
					<td><c:out value="${row.volumeSize}"></c:out></td>
				</tr>
				</c:forEach>

				<c:if test="${barDataList.size() > 0}">
					<spring:url value="/bar-data/${barDataModel.symbolId}/${barDataModel.frame}/first.html" var="firstUrl"/>
					<spring:url value="/bar-data/${barDateEnd}/${barDataModel.symbolId}/${barDataModel.frame}/next.html" var="nextUrl"/>
					<spring:url value="/bar-data/${barDateBegin}/${barDataModel.symbolId}/${barDataModel.frame}/prev.html" var="prevUrl"/>
					<spring:url value="/bar-data/${barDataModel.symbolId}/${barDataModel.frame}/last.html" var="lastUrl"/>
				
					<tr>
						<td width="70">
							<a href="${firstUrl}"><c:out value="<<<"/></a>
						</td>
						<td width="70">
							<a href="${prevUrl}"><c:out value="<"/></a>
						</td>
						<td width="70">
							<a href="${nextUrl}"><c:out value=">"/></a>
						</td>
						<td width="70">
							<a href="${lastUrl}"><c:out value=">>>"/></a>
						</td>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
						<td width="70"/>
					</tr>
				</c:if>
			</table>
		</td>
	</tr>
	</table>
	
</body>
</html>