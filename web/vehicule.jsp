<%@page import="ordo.data.entities.CommandeClient"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ordo.data.entities.Vehicule"%>
<%@page import="ordo.data.entities.Colis"%>
<%@page import="java.util.Collection"%>
﻿<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POO_Index</title>
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Cookie">
    <link rel="stylesheet" href="assets/css/styles.css">
    <link rel="stylesheet" href="assets/css/Pretty-Header.css">
    <link rel="stylesheet" href="assets/css/Pretty-Footer.css">
    <link rel="stylesheet" href="assets/css/Pretty-Registration-Form.css">
</head>

<body>
    <nav class="navbar navbar-default custom-header">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand navbar-link" href="index"><img class="img-responsive" src="assets/img/ecllogob.png"> </a>
                <button class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse"><span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></button>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav links">
                    <li role="presentation"><a href="index">Véhicules</a></li>
                    <li role="presentation"><a href="index">Clients</a></li>
                </ul>
            </div>
        </div>
    </nav>
    
    <div class="row">
        <!-- TABLE DES CLIENTS -->
        <div class="col-lg-3">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Clients</th>
                        <th>Type</th>
                        <th>Quantité Demandée</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${clients}" var="client">
                        <tr>
                            <td>
                                <c:out value="${client.id}" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${client.nombreRemorquesMax > 1}">
                                        Train
                                    </c:when>    
                                    <c:otherwise>
                                        Camion
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:out value="${client.quantiteVoulue}" />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- TABLE DES VEHICULES ACTION -->
        <div class="col-lg-5">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>ACTION</th>
                        <th>DEPART</th>
                        <th>ARRIVEE</th>
                        <th>TRAIN</th>
                        <th>ACTION</th>
                        <th>DUREE</th>
                        <th>DISTANCE</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${vas}" var="va">
                        <tr>
                            <td>
                                <c:out value="${va.id}" />
                            </td>
                            <td>
                                <c:out value="${va.depart.numeroLieu}" />
                            </td>
                            <td>
                                <c:out value="${va.arrivee.numeroLieu}" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${va.isTrain}">
                                        Vrai
                                    </c:when>    
                                    <c:otherwise>
                                        Faux
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:out value="${va.enumAction}" />
                            </td>
                            <td>
                                <fmt:parseNumber var = "heures" type = "number" value = "${va.duree /3600}" />
                                <fmt:parseNumber var = "minutes" type = "number" value = "${va.duree %3600 /60}" />
                                <c:if test="${heures != 0}">
                                    <c:out value="${heures}h"/>
                                </c:if> 
                                <c:if test="${minutes != 0}">
                                    <c:out value="${minutes}m"/>
                                </c:if>
                                <c:if test="${heures == 0 && minutes == 0}">
                                    <c:out value="< 0 m"/>
                                </c:if>
                            </td>
                            <td>
                                <fmt:parseNumber var = "km" type = "number" value = "${va.distance /1000}" />
                                <c:if test="${km == 0}">
                                    <c:out value="< "/>
                                </c:if>
                                <c:out value="${km} km" />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <!-- TABLE DU SWAPBODY1 -->
        <c:choose>
            <c:when test="${colis2 == null}">
                <div class="col-lg-4">
            </c:when>    
            <c:otherwise>
                <div class="col-lg-2">
            </c:otherwise>
        </c:choose>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th colspan="3" style="text-align: center;">SwapBody 1</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <th>Colis</th>
                        <th>Client</th>
                        <th>Quantité</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${colis}" var="c">
                        <tr>
                            <td>
                                <c:out value="${c.id}" />
                            </td>
                            <td>
                                <c:out value="${c.commande.numeroLieu}" />
                            </td>
                            <td>
                                <c:out value="${c.quantite}" />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${colis2 != null}">
            <!-- TABLE DU SWAPBODY2 -->
            <div class="col-lg-2">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th colspan="3" style="text-align: center;">SwapBody 2</th>
                        </tr>
                    </thead>
                    <thead>
                        <tr>
                            <th>Colis</th>
                            <th>Client</th>
                            <th>Quantité</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${colis2}" var="c">
                            <tr>
                                <td>
                                    <c:out value="${c.id}" />
                                </td>
                                <td>
                                    <c:out value="${c.commande.numeroLieu}" />
                                </td>
                                <td>
                                    <c:out value="${c.quantite}" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if> 
    </div>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
    <script src="assets/js/index.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB4poIa8NWHgctl0F46-oOfjqTzX4iqU3g"></script>
</body>

</html>
