<%@page import="com.entities.Note"%>
<%@page import="com.helper.FactoryProvider"%>
<%@page import="org.hibernate.Session"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Edit Notes</title>
<%@include file="all_js_css.jsp" %>
</head>
<body>
<div class="container">
    <%@include file="navbar.jsp" %>
    <h1>Edit Note</h1><br>
    <%
    int noteId=Integer.parseInt(request.getParameter("note_id").trim());
	
	Session s=FactoryProvider.getFactory().openSession();
	Note note=s.get(Note.class, noteId);
	%>
	<form action="UpdateServlet" method="post">
	<input type="hidden" name="noteId" value="<%=note.getId()%>">
			<div class="form-group">
				<label for="exampleInputEmail1">Note Title</label> <input
					type="text" class="form-control" id="title" name="title"
					aria-describedby="emailHelp" value="<%=note.getTitle() %>" required> 
			</div>
			<div class="form-group">
				<label for="content">Note Content</label>
				<textarea style="height:200px;" id="content" class="form-control" name="content" required><%=note.getContent() %></textarea> 
			</div>
			<div class="container text-center"><button type="submit" class="btn btn-success">Save</button></div>
			
		</form>
	
    </div>
</body>
</html>