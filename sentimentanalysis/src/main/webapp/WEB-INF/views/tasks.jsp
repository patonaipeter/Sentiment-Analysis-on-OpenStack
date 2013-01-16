<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="wrapper">
	<table>
		<tr>
			<th>Taskname</th>
			<th>Query</th>
			<th>Sentiment Value</th>
			<th>Status</th>
			<th>Start time</th>
			<th>Duration</th>
		</tr>

		<c:forEach var="task" items="${tasks}">
			<tr>
				<td>${task.name}</td>
				<td>${task.query}</td>
				<c:if test="${empty task.sentiment}">
					<td style="text-align:center;">-</td>
				</c:if>
				<c:if test="${not empty task.sentiment}">
					<td>${task.sentiment}</td>
				</c:if>
				<td>${task.status}</td>
				<td>${task.date}</td>
				<c:if test="${empty task.duration}">
					<td style="text-align:center;">-</td>
				</c:if>
				<c:if test="${not empty task.duration}">
					<td style="text-align:right;">${task.duration}ms</td>
				</c:if>
			</tr>
		</c:forEach>
	</table>
</div>