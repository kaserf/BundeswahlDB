<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
	Auswertung auswertung = new Auswertung();
	int kandidatId = Integer.parseInt(request.getParameter("kandidatId"));
	int parteiId = Integer.parseInt(request.getParameter("parteiId"));
	String hash = request.getParameter("hash");
	auswertung.setWahlzettel(kandidatId, parteiId, hash);
%>
<h3>Danke für Ihre Stimmabgabe!</h3>