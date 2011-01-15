<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<p>
<div id="chooser">
	<div id="selectedDisplay" style="width:100%">
		Partei: <span id="parteiDisplay"></span>
	</div>
	<div id="chooserDisplay">
		<table style="border-width: 0px" >
			<tr>
				<td style="padding: 5px" id="parteiChooser" valign="top">
					
				</td>
			</tr>
		</table>
	</div>
</div>
</p>
<p>
<div id="top10"></div>
</p>
<script>
$('#selectedDisplay').button();
$('#selectedDisplay').click(function() {
	$('#selectedDisplay').slideUp();
	$('#top10').fadeOut(function() {
		$('#chooserDisplay').slideDown();
	});
});
$('#parteiChooser').load("widgets/knappstesieger/parteienliste.jsp");
$('#selectedDisplay').hide();
$('#top10').hide();
</script>