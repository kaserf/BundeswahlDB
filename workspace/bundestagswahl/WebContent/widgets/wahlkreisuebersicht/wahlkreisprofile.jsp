<%@page import="java.text.NumberFormat"%>
<%@page import="com.googlecode.charts4j.DataUtil"%>
<%@page import="com.googlecode.charts4j.AxisTextAlignment"%>
<%@page import="com.googlecode.charts4j.AxisStyle"%>
<%@page import="com.googlecode.charts4j.Color"%>
<%@page import="com.googlecode.charts4j.BarChart"%>
<%@page import="java.util.Collections"%>
<%@page import="data.Einzelergebnis"%>
<%@page import="com.googlecode.charts4j.AxisLabelsFactory"%>
<%@page import="com.googlecode.charts4j.Plots"%>
<%@page import="com.googlecode.charts4j.BarChartPlot"%>
<%@page import="com.googlecode.charts4j.Data"%>
<%@page import="com.googlecode.charts4j.GCharts"%>
<%@page import="com.googlecode.charts4j.PieChart"%>
<%@page import="java.util.Map"%>
<%@page import="data.Partei"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.googlecode.charts4j.Slice"%>
<%@page import="java.util.List"%>
<%@page import="data.Kandidat"%>
<%@page import="data.WahlkreisUebersicht"%>
<%@page import="beans.Auswertung"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	int id = Integer.parseInt(request.getParameter("wahlkreis"));
	boolean live = Boolean.parseBoolean(request.getParameter("live"));
	Auswertung auswertung = new Auswertung();
	WahlkreisUebersicht uebersicht;
	if(live) {
		uebersicht = auswertung.getWahlkreisUebersichtEinzelstimmen(id);
	}
	else {
		uebersicht = auswertung.getWahlkreisUebersicht(id);
	}
	Kandidat kandidat = uebersicht.getDirektkandidat();
	NumberFormat numberFormat = NumberFormat.getInstance();
	numberFormat.setMaximumFractionDigits(2);
	String wahlbeteiligung = numberFormat.format(uebersicht.getWahlbeteiligung());
%>
<h2>Ergebnisse für Wahlkreis: <%= request.getParameter("wahlkreis") + " - " + uebersicht.getWahlkreis().getName() %></h2>
<h3>Direktkandidat: <%= kandidat.getVorname() + " " + kandidat.getName() + " (" + kandidat.getPartei() + ")" %></h3>
<h3>Wahlbeteiligung: <%= wahlbeteiligung + "%"%></h3>

<%
	List<Einzelergebnis<Partei, Double>> stimmenProzentual = uebersicht.getStimmenProzentual();
	Collections.sort(stimmenProzentual);
	Collections.reverse(stimmenProzentual);
	List<Slice> slices = new ArrayList<Slice>();
	for (Einzelergebnis<Partei, Double> erg : stimmenProzentual) {
		String parteiName = erg.getEntity().getName();
		Double stimmenDouble = erg.getValue();
		int stimmen = (new Double(stimmenDouble * 100.0)).intValue();
		Double stimmenRounded = new Double(stimmen) / 100;
		Slice s = Slice.newSlice(stimmen, null, parteiName + " (" + stimmenRounded + "%)", null);
		slices.add(s);
	}
	PieChart pieChart = GCharts.newPieChart(slices);
	pieChart.setSize(600,300);
	pieChart.setMargins(0,0,0,0);
	pieChart.setTitle("Stimmverteilung (prozentual)");
	String pieChartUrl = pieChart.toURLString();
%>
<p class="centered">
<img id="StimmenProzentual" src="<%= pieChartUrl %>" alt="Stimmen prozentual" />
</p>

<%
	List<Einzelergebnis<Partei, Integer>> stimmenAbsolut = uebersicht.getStimmenAbsolut();
	Collections.sort(stimmenAbsolut);
	Collections.reverse(stimmenAbsolut);
	Integer max = Collections.max(stimmenAbsolut).getValue();
	List<Integer> data = new ArrayList<Integer>();
	List<String> labels = new ArrayList<String>();

	for (Einzelergebnis<Partei, Integer> erg : stimmenAbsolut) {
		data.add(erg.getValue());
		String parteiName = erg.getEntity().getName();
		labels.add(parteiName);
	}
	BarChartPlot plot = Plots.newBarChartPlot(DataUtil.scaleWithinRange(0, max, data));
	BarChart barChart = GCharts.newBarChart(plot);
	// this is necessary for making the bar graph horizontal!
	Collections.reverse(labels);
	barChart.addYAxisLabels(AxisLabelsFactory.newAxisLabels(labels));
	barChart.addXAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, max));
	barChart.setSize(500, labels.size() * 40 + 50);
	barChart.setTitle("Stimmverteilung (absolut)");
	barChart.setBarWidth(30);
	barChart.setHorizontal(true);
	String barChartUrl = barChart.toURLString();
%>
<p class="centered">
<img id="StimmenAbsolut" src="<%= barChartUrl %>" alt="Stimmen absolut" />
</p>

<%
	List<Einzelergebnis<Partei, Double>> stimmenEntwicklung = uebersicht.getStimmenEntwicklung();
	Collections.sort(stimmenEntwicklung);
	Collections.reverse(stimmenEntwicklung);
	Double maxDouble = Collections.max(stimmenEntwicklung).getValue();
	Double minDouble = Collections.min(stimmenEntwicklung).getValue();
	List<Double> dataDouble = new ArrayList<Double>();
	labels = new ArrayList<String>();
	for (Einzelergebnis<Partei, Double> erg : stimmenEntwicklung) {
		double prozent = erg.getValue();
		dataDouble.add(prozent);
		String parteiName = erg.getEntity().getName();
		labels.add(parteiName);
	}
	plot = Plots.newBarChartPlot(DataUtil.scaleWithinRange(minDouble - 1.0, maxDouble + 1.0, dataDouble));
	BarChart barChartDev = GCharts.newBarChart(plot);
	barChartDev.addXAxisLabels(AxisLabelsFactory.newAxisLabels(labels));
	barChartDev.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(minDouble - 1.0, maxDouble + 1.0));
	Double span = Math.abs(maxDouble - minDouble);
	Double zeroLinePercent = Math.abs(minDouble) / span;
	barChartDev.setSize(labels.size() * 50,500);
	barChartDev.setTitle("Stimmenentwicklung (prozentual)");
	barChartDev.setBarWidth(40);
	String barChartDevUrl = barChartDev.toURLString();
%>
<p class="centered">
<img id="StimmenEntwicklung" src="<%= barChartDevUrl %>&chp=<%= zeroLinePercent %>" alt="Stimmenentwicklung" />
</p>