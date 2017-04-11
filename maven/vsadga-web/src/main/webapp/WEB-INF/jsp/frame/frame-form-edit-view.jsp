<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
	<title>Konfiguracja pojedynczej ramki</title>
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
			<form:form action="../../frame-edit-done.html" method="POST" modelAttribute="timeFrame">
				<tr>
					<td width="70"/>
					<td align="right" width="220"/>
					<td width="220">
						<form:hidden path="id"/>
					</td>
				</tr>
				<tr>
					<td/>
					<td align="right" width="220">Minuty ramki:</td>
					<td width="220">
						<form:input path="timeFrame"/>
					</td>
				</tr>
				<tr>
					<td width="70"/>
					<td align="right" width="220">Opis ramki:</td>
					<td width="220">
						<form:input path="timeFrameDesc"/>
					</td>
				</tr>
				<tr>
					<td width="70"/>
					<td align="right" width="220">Ramka wg pliku:</td>
					<td width="220">
						<form:checkbox path="isFileFrame"/>
					</td>
				</tr>
				<tr>
					<td width="70"/>
					<td align="right" width="220">Ramka logiczna:</td>
					<td width="220">
						<form:checkbox path="isLogicalFrame"/>
					</td>
				</tr>
				<tr>
					<td width="70"/>
					<td align="right" width="220">Aktywność:</td>
					<td width="220">
						<form:checkbox path="isActive"/>
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
		</td>
	</tr>
	</table>
	
</body>
</html>