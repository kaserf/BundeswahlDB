<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div id="persNrErrorDialog" title="Fehler">
	<p>Ihre Personalausweisnummer ist nicht gültig.</p>
</div>

<script>
	$("#persNrErrorDialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: {
	   		OK: function() { $(this).dialog("close"); }
	   	}
	});
<%
	String persNr = request.getParameter("persnr");
	Auswertung auswertung = new Auswertung();
	boolean valid = auswertung.checkPersNr(persNr);
	if (valid) {
%>
	$('#wahlzettel').slideUp(function() {
		$('#wahlzettel').load("widgets/stimmenabgabe/wahlzettel.jsp?persnr=" + <%= persNr %>, function() {
			$('#wahlzettel').slideDown();
		});
	});
<% 
	}
	else {
%>
	$("#persNrErrorDialog").dialog('open');
<% 
	}
%>
</script>