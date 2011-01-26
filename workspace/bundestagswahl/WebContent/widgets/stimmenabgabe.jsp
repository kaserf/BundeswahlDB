<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div id="wahlzettel">
<table>
<tr>
	<td style="padding: 5px">
		<div class="ui-widget">
		<label>Bitte geben Sie Ihre ID an:</label>
		</div>
	</td>
	<td style="padding: 5px">
		<div class="ui-widget">
		<input id="id" type="text" />
		</div>
	</td>
</tr>
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

<div align="right">
<button id="nextBtn">Weiter</button>
</div>

</div>

<script>
	$("#nextBtn").button();
	$("#nextBtn").click(function(){
		var id = $("#id").val();
		var persnr = $("#persnr").val();
		$('#wahlzettel').slideUp(function() {
			$('#wahlzettel').load("widgets/stimmenabgabe/wahlzettel.jsp?id=" + id + "&persnr=" + persnr, function() {
				$('#wahlzettel').slideDown();
			});
		});	
	});
</script>
