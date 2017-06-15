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
    <%
    //Collection<Vehicule> vehicules = new ArrayList<>();
    Collection<Vehicule> vehicules = (Collection<Vehicule>) request.getAttribute("vehicule");
    %>
    <nav class="navbar navbar-default custom-header">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand navbar-link" href="index "><img class="img-responsive" src="assets/img/ecllogob.png"> </a>
                <button class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse"><span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></button>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav links">
                </ul>
                
                <ul class="nav navbar-nav links" style="float: right;">
                    <li role="presentation"><button class="btn btn-danger" id="calculate_wip">CALCULER (WIP)</button></li>
                    <li role="presentation"><button class="btn btn-success" id="calculate">CALCULER</button></li>
                </ul>
            </div>
        </div>
    </nav>
    <div id="map"></div>
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Camion </th>
                    <th>Clients</th>
                    <th>Données de parcours</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${vehicules}" var="vehicule">
                <% Vehicule vehicule = (Vehicule) pageContext.getAttribute("vehicule"); %>
                <tr>
                    <td><a href="vehiculeControler?id=${vehicule.id}">Camion<c:if test="${vehicule.isTrain()}">-train</c:if> ${vehicule.id}</a></td>
                    <td>
                        <% 
                        List<String> libellesCommandes = new ArrayList<String>();
                        for(CommandeClient cc: vehicule.getCommandes()) {
                            libellesCommandes.add(cc.getNumeroLieu());
                        }
                        %>
                        <%= String.join(", ", libellesCommandes) %>
                    </td>
                    <td>
                        <%= ((int) (vehicule.getDistanceParcourue() / 1000))%> km ; 
                        <%
                        long secondes = (long) vehicule.getTempsTrajet();
                        int heures = ((int) (vehicule.getTempsTrajet() / 3600));
                        int minutes = ((int) vehicule.getTempsTrajet()%3600 /60);
                        switch(heures) {
                            case 0:
                                break;
                            case 1:
                        %>
                        1 heure
                        <%
                                break;
                            default:
                        %>
                        <%= heures %> heures
                        <%
                                break;
                        }

                        switch(minutes) {
                            case 0:
                                break;
                            case 1:
                        %>
                        1 minute
                        <%
                                break;
                            default:
                        %>
                        <%= minutes %> minutes
                        <%
                                break;
                        }
                        %>
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <footer>
        <div class="row">
            <div class="col-md-6 col-sm-8">
                <form class="form-inline bootstrap-form-with-validation" method="post" action="upload" enctype="multipart/form-data">
                    <p>Importer un fichier CSV</p>
                    <div class="form-group">
                        <input type="file" name="file-input">
                    </div>
                    <button class="btn btn-default" type="submit">Envoyer</button>
                </form>
                <p>Utiliser les fichiers CSV présents</p>
                <button class="btn btn-default" id="loadData">Charger les données</button>
            </div>
            <div class="col-md-3 col-sm-2">
                <p>Générer la solution CSV</p>
                <button class="btn btn-success" id="generate">GENERER</button>
            </div>
            <div class="col-md-3 col-sm-2">
                <form class="form-inline bootstrap-form-with-validation" action="${pageContext.request.contextPath}/delete">
                    <p>Remettre à zéro</p>
                    <button class="btn btn-danger" type="submit">SUPPRIMER</button>
                </form>
            </div>
        </div>
    </footer>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
    <script src="assets/js/index.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB4poIa8NWHgctl0F46-oOfjqTzX4iqU3g"></script>
</body>

</html>
