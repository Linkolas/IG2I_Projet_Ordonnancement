<%@page import="ordo.data.entities.CommandeClient"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ordo.data.entities.Vehicule"%>
<%@page import="java.util.Collection"%>
﻿<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
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
        <div class="col-lg-3">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Clients</th>
                        <th>Type</th>
                        <th>Quantité Demandé</th>
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
        
    </div>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
    <script src="assets/js/index.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB4poIa8NWHgctl0F46-oOfjqTzX4iqU3g"></script>
</body>

</html>
