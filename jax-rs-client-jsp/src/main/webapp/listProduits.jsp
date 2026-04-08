<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="ws.rest.servlet.Produit" %>
<html>
<head>
    <title>Liste des Produits</title>
    <style>
        table { border-collapse: collapse; width: 80%; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>

<h2>Liste des Produits</h2>

<a href="formProduit.jsp">Ajouter un nouveau produit</a><br><br>

<table>
    <tr>
        <th>ID</th>
        <th>Désignation</th>
        <th>Prix</th>
        <th>Quantité</th>
        <th>Actions</th>
    </tr>

    <%
        List<Produit> produits = (List<Produit>) request.getAttribute("produits");
        if (produits != null) {
            for (Produit p : produits) {
    %>
    <tr>
        <td><%= p.getId() %></td>
        <td><%= p.getDesignation() %></td>
        <td><%= p.getPrix() %></td>
        <td><%= p.getQuantite() %></td>
        <td>
            <a href="ListProduitsAction?action=editer&id=<%= p.getId() %>">Editer</a> |
            <a href="ListProduitsAction?action=supprimer&id=<%= p.getId() %>" 
               onclick="return confirm('Confirmer la suppression ?')">Supprimer</a>
        </td>
    </tr>
    <% 
            }
        }
    %>
</table>

</body>
</html>