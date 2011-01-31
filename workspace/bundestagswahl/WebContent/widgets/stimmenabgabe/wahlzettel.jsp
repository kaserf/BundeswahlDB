<%@page import="data.Partei"%>
<%@page import="data.Kandidat"%>
<%@page import="java.util.List"%>
<%@page import="data.Wahlzettelauswahl"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
	String persnr = request.getParameter("persnr");
	Auswertung auswertung = new Auswertung();
	Wahlzettelauswahl auswahl = auswertung.getWahlzettelauswahl(persnr);
%>

<div id="errorDialog" title="Fehler">
	<p>Bitte selektieren Sie einen Direktkandiaten und eine Partei.</p>
</div>

<table style="border-width: 0px" >
<tr>
	<td style="padding: 5px" id="erststimme" valign="top">
	<h2>Erststimme</h2>
<%
	List<Kandidat> kandidaten = auswahl.getKandidaten();
	for (Kandidat k : kandidaten) {
		String kandidatLabel = k.getName() + ", " + k.getVorname() 
			+ " (" + k.getPartei().getName() + ")";
%>
		<input type="radio" name="erststimmeRadio" id="erststimmeRadio<%= k.getId() %>" value="<%= k.getId() %>"  />
		<label for="erststimmeRadio<%= k.getId() %>"><%= kandidatLabel %></label><br />
<%
	}
%>
	</td>
	<td style="padding: 5px" id="zweitstimme" valign="top">
	<h2>Zweitstimme</h2>
<%
	List<Partei> parteien = auswahl.getParteien();
	for (Partei p : parteien) {
%>
		<input type="radio" name="zweitstimmeRadio" id="zweitstimmeRadio<%= p.getId() %>" value="<%= p.getId() %>"  />
		<label for="zweitstimmeRadio<%= p.getId() %>"><%= p.getName() %></label><br />
<%
	}
%>
	</td>
</tr>
</table>
<br>
<div align="right">
	<button id="abgabe">Stimme abgeben</button>
</div>

<script>
	$("#errorDialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: {
	   		OK: function() { $(this).dialog("close"); }
	   	}
	});

	$('#erststimme').buttonset();
	$('#zweitstimme').buttonset();
	$('[for^="erststimmeRadio"]').each(function(index) {
		$(this).css("width", "350px");
		$(this).css("text-align", "left");
	});
	$('#abgabe').button();
	$('#abgabe').click(function(){
		var kandidatId = $("input[name='erststimmeRadio']:checked").val();
		var parteiId = $("input[name='zweitstimmeRadio']:checked").val();
		if (kandidatId == undefined || parteiId == undefined) {
			$("#errorDialog").dialog('open');
		}
		else {
			$('#wahlzettel').slideUp(function() {
				$('#wahlzettel').load("widgets/stimmenabgabe/submit.jsp?kandidatId=" 
						+ kandidatId + "&parteiId=" + parteiId + "&persNr=" + <%= persnr %> + "&wahlkreis=" + <%= auswahl.getWahlkreis()%> + "&wahlbezirk=" + <%= auswahl.getWahlbezirk()%>, function() {
					$('#wahlzettel').slideDown();
				});
			});
		}
	});
</script>