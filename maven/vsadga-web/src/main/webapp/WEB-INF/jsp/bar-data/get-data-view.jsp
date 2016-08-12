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
	
	<form:form action="get-bar-data.html" method="POST" modelAttribute="barDataModel">
	<table>
		<tr>
			<td width="50"/>
			<td>
				<form:select path="symbolId">
					<form:options items="${symbolList}" itemValue="symbolId" itemLabel="symbolName"/>
				</form:select>
			</td>
			<td>
				<form:select path="frame">
					<form:options items="${frameList}" itemValue="timeFrameDesc" itemLabel="timeFrameDesc"/>
				</form:select>
			</td>
			
			<td>
				<input type="submit" value="Wyswietl"/>
			</td>
		</tr>
	</table>
	</form:form>
	<br>
	<br>
	
</body>
</html>