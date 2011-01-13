<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="data.Bundesland"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>

    
<%
Auswertung auswertung = new Auswertung();
List<Bundesland> bundeslaender = auswertung.getAllBundeslaender();
for (Bundesland b : bundeslaender) {
%>
<input type="radio" name="bundeslandRadio" id="bundeslandRadio<%= b.getNummer() %>" value="<%= b.getNummer() %>"  />
	<label for="bundeslandRadio<%= b.getNummer() %>"><%= b.getName() %></label><br />
<% 
} 
%>
<script>
$('#bundeslandChooser').buttonset();
$('[for^="bundeslandRadio"]').each(function(index) {
	$(this).css("width", "230px");
	$(this).css("text-align", "left");
});
$('[name="bundeslandRadio"]').each(function(index) {
	var name = $(this).attr('value');
	$(this).click(function() {
		$('#wahlkreisChooser').load('widgets/wahlkreisuebersicht/wahlkreisliste.jsp?bundesland=' + name);
	});
});
</script>