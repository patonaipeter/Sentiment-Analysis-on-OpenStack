<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="wrapper">
	<table>
		<tr>
			<th>Taskname</th><th>Query</th><th>Sentiment Value</th><th>Start time</th><th>Duration</th>
		</tr>

		<c:forEach var="task" items="${tasks}">
			<tr>
				<td>${task.name}</td>
				<td>${task.query}</td>
				<td>${task.status}</td>
				<td>${task.date}</td>
			</tr>
		</c:forEach>
		
	</table>
</div>