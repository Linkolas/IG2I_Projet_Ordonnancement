<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
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
                <a class="navbar-brand navbar-link" href="#"><img class="img-responsive" src="assets/img/ecllogob.png"> </a>
                <button class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse"><span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span></button>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav links">
                    <li role="presentation"><a href="#">Overview</a></li>
                    <li role="presentation"><a href="#">Surveys</a></li>
                    <li role="presentation"><a href="#">Reports</a></li>
                    <li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false" href="#">Dropdown<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li role="presentation"><a href="#">First Item</a></li>
                            <li role="presentation"><a href="#">Second Item</a></li>
                            <li role="presentation"><a href="#">Third Item</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Camion </th>
                    <th>Clients</th>
                    <th>Données de parcours</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${vehicules}" var="vehicule">
                    <tr>
                        <td>Camion<c:if test="${vehicule.isTrain()}">-train</c:if> ${vehicule.id}</td>
                    </tr>
                </c:forEach>
                <tr>
                    <td>Camion 1</td>
                    <td>Sopra-Steria, Boulanger</td>
                    <td>300 km ; 5 heures</td>
                </tr>
                <tr>
                    <td>Camion-train 2</td>
                    <td>Engie </td>
                    <td>265 km ; 4 heures 20 minutes</td>
                </tr>
            </tbody>
        </table>
    </div>
    <iframe allowfullscreen="" frameborder="0" width="100%" height="400" src="https://www.google.com/maps/embed/v1/place?key=+AIzaSyB4poIa8NWHgctl0F46-oOfjqTzX4iqU3g+&amp;q=Paris%2C+France&amp;zoom=11"></iframe>
    <footer>
        <div class="row">
            <div class="col-md-6 col-sm-8">
                <form class="form-inline bootstrap-form-with-validation" method="post">
                    <p>Importer un fichier CSV</p>
                    <div class="form-group">
                        <input type="file" name="file-input">
                    </div>
                    <button class="btn btn-default" type="submit">Envoyer</button>
                </form>
            </div>
            <div class="col-md-3 col-md-offset-3 col-sm-2 col-sm-offset-2">
                <form class="form-inline bootstrap-form-with-validation" method="post">
                    <p>Remettre à zéro</p>
                    <button class="btn btn-danger" type="submit">SUPPRIMER</button>
                </form>
            </div>
        </div>
    </footer>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/bootstrap/js/bootstrap.min.js"></script>
</body>

</html>