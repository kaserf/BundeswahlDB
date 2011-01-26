<%@page import="data.Wahlkreis"%>
<%@page import="java.util.List"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
int bundesland = Integer.parseInt(request.getParameter("bundesland"));
Auswertung auswertung = new Auswertung();
List<Wahlkreis> wahlkreise = auswertung.getWahlkreiseForBundesland(bundesland); 
int i = 0;
for (Wahlkreis w : wahlkreise) {
%>
<input type="radio" name="wahlkreisRadio" id="wahlkreisRadio<%= i %>" value="<%= w.getId() %>" />
	<label for="wahlkreisRadio<%= i %>"><%= w.getId() + " - " + w.getName() %></label><br />
<% 
	i++; 
} %>
<script>
$('#wahlkreisChooser').buttonset();
$('[for^="wahlkreisRadio"]').each(function(index) {
	$(this).css("width", "350px");
	$(this).css("text-align", "left");
});
$('[name="wahlkreisRadio"]').each(function(index) {
	var id = $(this).attr('value');
	$(this).click(function() {
		loadProfile(id);
	});
});

function loadProfile(id) {
	var time = new Date().getTime();
	$('#selectedDisplay').slideDown(function() {
		$('#wahlkreisProfile').fadeOut(function() {
			$('#wahlkreisProfile').load('widgets/wahlkreisuebersicht/wahlkreisprofile.jsp?wahlkreis=' 
					+ id + "&live=" + live, function() {
				$('#wahlkreisProfile').fadeIn();
				var timeEnd = new Date().getTime();
				var diff = timeEnd - time;
				showBenchmark(diff);
			});
		});
	});
	$('#chooserDisplay').slideUp();
	$('#bundeslandDisplay').html('<%= auswertung.getBundesland(bundesland).getName() %>');
	$('#wahlkreisnummerDisplay').html(id);
}
</script>

