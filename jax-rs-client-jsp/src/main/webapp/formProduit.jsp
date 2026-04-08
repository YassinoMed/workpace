<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <html>

    <head>
        <title>Ajouter Produit</title>
    </head>

    <body>
        <h2>Ajouter un produit</h2>
        <form action="FormProduitAction" method="POST">
            <label>Nom:</label>
            <input type="text" name="nom" required /><br /><br />
            <label>Prix:</label>
            <input type="number" step="0.01" name="prix" required /><br /><br />
            <label>Description:</label>
            <input type="text" name="description" /><br /><br />
            <input type="submit" value="Ajouter Produit" />
        </form>
    </body>

    </html>