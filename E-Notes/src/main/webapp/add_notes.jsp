<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Add Note</title>
<%@include file="all_js_css.jsp"%>
</head>
<body>
	<div class="container">
		<%@include file="navbar.jsp"%><br>
		<h1 class="text-uppercase">Please fill your note</h1>
		<form action="SaveNoteServlet" method="post">
			<div class="form-group">
				<label for="exampleInputEmail1">Note Title</label> <input
					type="text" class="form-control" id="title" name="title"
					aria-describedby="emailHelp" placeholder="Enter Title" required> 
			</div>
			<div class="form-group">
				<label for="content">Note Content</label>
				<textarea style="height:200px;" id="content" class="form-control" name="content" required></textarea> 
			</div>
			<div class="container text-center"><button type="submit" class="btn btn-primary">Submit</button></div>
			
		</form>
	</div>
</body>
</html>