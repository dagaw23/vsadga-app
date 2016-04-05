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
	<table>
		<tr>
			<td><a href="<c:url value="/audusd.html"/>">AUDUSD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/eurusd.html"/>">EURUSD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/gbpusd.html"/>">GBPUSD</a></td>
		</tr>
		<tr>
			<td><a href="<c:url value="/gold.html"/>">GOLD</a></td>
			<td>::</td>
			<td><a href="<c:url value="/oil.html"/>">OIL</a></td>
		</tr>
	</table>
	<br>
	<br>
	
	${html_tab}
	
	<br>
	<br>
	
</body>
</html>