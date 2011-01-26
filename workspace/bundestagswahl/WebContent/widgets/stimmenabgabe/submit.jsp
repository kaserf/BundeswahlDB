<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
	Auswertung auswertung = new Auswertung();
	int kandidatId = Integer.parseInt(request.getParameter("kandidatId"));
	int parteiId = Integer.parseInt(request.getParameter("parteiId"));
	int persnr = Integer.parseInt(request.getParameter("persNr"));
	auswertung.setGewaehlt(persnr);
	auswertung.setWahlzettel(kandidatId, parteiId);
%>
<h3>Danke für Ihre Stimmabgabe!</h3>