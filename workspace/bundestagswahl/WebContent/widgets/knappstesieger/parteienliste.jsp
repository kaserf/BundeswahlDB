<%@page import="data.Partei"%>
<%@page import="data.KnappsterSieger"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
Auswertung auswertung = new Auswertung();
List<Partei> parteien = auswertung.getAllParteien();
for (Partei p : parteien) {
	String name = p.getName();
	if (name.equals("pl")) {
		name = "parteilose";
	}
%>
<input type="radio" name="parteiRadio" id="parteiRadio<%= p.getId() %>" value="<%= p.getId() %>"  />
	<label for="parteiRadio<%= p.getId() %>"><%= name %></label><br />
<% 
} 
%>
<script>
$('#parteiChooser').buttonset();
$('[for^="parteiRadio"]').each(function(index) {
	$(this).css("width", "230px");
	$(this).css("text-align", "left");
});
$('[name="parteiRadio"]').each(function(index) {
	var id = $(this).attr('value');
	$(this).click(function() {
		var time = new Date().getTime();
		$('#top10').fadeOut(function() {
			$('#top10').load('widgets/knappstesieger/top10.jsp?partei=' + id, function() {
				$('#selectedDisplay').slideDown(function() {
					$('#top10').fadeIn();
					var timeEnd = new Date().getTime();
					var diff = timeEnd - time;
					showBenchmark(diff);
				});
			});	
		});
		$('#chooserDisplay').slideUp();
	});
});
</script>