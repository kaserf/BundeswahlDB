<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div id="check">
</div>

<div id="wahlzettel">
<table>
<tr>
	<td style="padding: 5px">
		<div class="ui-widget">
		<label>Bitte geben Sie Ihre Personalausweisnummer an:</label>
		</div>
	</td>
	<td style="padding: 5px">
		<div class="ui-widget">
		<input id="persnr" type="text" />
		</div>
	</td>
</tr>
</table>
<br>
<div align="right">
<button id="nextBtn">Weiter</button>
</div>

</div>

<script>
	$("#errorDialog").dialog({
		modal: true,
		autoOpen: false,
		buttons: {
	   		OK: function() { $(this).dialog("close"); }
	   	}
	});

	$("#nextBtn").button();
	$("#nextBtn").click(function(){
		var persnr = $("#persnr").val();
		$('#check').load("widgets/stimmenabgabe/checkPersNr.jsp?persnr=" + persnr);	
	});
</script>
