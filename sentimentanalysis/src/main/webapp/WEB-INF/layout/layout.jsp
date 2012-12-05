<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<link rel="stylesheet" type="text/css" href="styles/standard.css">
		<title><tiles:insertAttribute name="title" /></title>
	</head>
	<body>
	 	<header>
	 		<h1>
	 			Sentiment Analysis on Google App Engine
	 		</h1>
	 	</header>
	
		<div id="content">
			<tiles:insertAttribute name="content" />
		</div>
	
		<footer>
			<p>By Peter, Sebastian, Dymtro, Katryn, Andreas, Jacko, Navid</p>			
		</footer>
	</body>
</html>