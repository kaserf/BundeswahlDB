<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<p>
<div id="chooser">
<div id="selectedDisplay" style="width:100%">
	Bundesland: <span id="bundeslandDisplay">BY</span>, 
	Wahlkreisnummer: <span id="wahlkreisnummerDisplay">45</span>
</div>
<div id="chooserDisplay">
<table style="border-width: 0px" >
	<tr>
		<td style="padding: 5px" id="bundeslandChooser" valign="top">
			
		</td>
		<td style="padding: 5px" id="wahlkreisChooser" valign="top">
			
		</td>
	</tr>
</table>
</div>
</div>
</p>
<p>
<div id="wahlkreisProfile"></div>
</p>
<script>
$('#selectedDisplay').button();
$('#selectedDisplay').click(function() {
	$('#selectedDisplay').slideUp();
	$('#wahlkreisProfile').fadeOut(function() {
		$('#chooserDisplay').slideDown();
	});
});
$('#bundeslandChooser').load("widgets/wahlkreisuebersicht/bundeslandliste.jsp");
$('#selectedDisplay').hide();
$('#wahlkreisProfile').hide();
</script>