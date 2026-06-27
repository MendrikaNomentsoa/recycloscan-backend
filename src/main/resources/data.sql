-- ================================================
-- Données initiales — WasteItems
-- Ce fichier est exécuté automatiquement au démarrage
-- ================================================

-- Vider la table avant d'insérer pour éviter les doublons
TRUNCATE TABLE waste_items RESTART IDENTITY CASCADE;

-- ================================================
-- PLASTIQUE → Poubelle JAUNE
-- ================================================
INSERT INTO waste_items (name, gemini_keywords, category, bin_color, instruction, points_value) VALUES
                                                                                                    ('Bouteille en plastique', 'bouteille,plastique,pet,flacon,eau,soda,jus', 'PLASTIQUE', 'JAUNE', 'Vider, écraser et remettre le bouchon avant de jeter', 10),
                                                                                                    ('Sac plastique', 'sac,plastique,sachet,pochette,emballage', 'PLASTIQUE', 'JAUNE', 'Vider et déposer dans le bac jaune', 10),
                                                                                                    ('Barquette plastique', 'barquette,plastique,plateau,alimentaire', 'PLASTIQUE', 'JAUNE', 'Rincer légèrement avant de jeter', 10),
                                                                                                    ('Pot de yaourt', 'yaourt,pot,plastique,dairy', 'PLASTIQUE', 'JAUNE', 'Rincer le pot avant de le jeter', 10),
                                                                                                    ('Boîte de shampoing', 'shampoing,flacon,plastique,cosmétique,gel douche', 'PLASTIQUE', 'JAUNE', 'Vider complètement avant de jeter', 10),
                                                                                                    ('Film plastique', 'film,plastique,cellophane,wrap,emballage', 'PLASTIQUE', 'JAUNE', 'Déposer dans le bac jaune', 8),
                                                                                                    ('Bouteille de lait', 'lait,bouteille,plastique,dairy', 'PLASTIQUE', 'JAUNE', 'Rincer et écraser avant de jeter', 10),
                                                                                                    ('Canette aluminium', 'canette,aluminium,métal,bière,soda,boisson', 'PLASTIQUE', 'JAUNE', 'Écraser la canette pour gagner de la place', 10),
                                                                                                    ('Boîte de conserve', 'conserve,boîte,métal,fer,fer blanc,sauce,tomate', 'PLASTIQUE', 'JAUNE', 'Rincer et déposer dans le bac jaune', 10),
                                                                                                    ('Aérosol vide', 'aérosol,bombe,spray,déodorant,laque', 'PLASTIQUE', 'JAUNE', 'S assurer que la bombe est bien vide avant de jeter', 10),
                                                                                                    ('Brique de lait', 'brique,lait,carton,tetra pak,jus,soupe', 'PLASTIQUE', 'JAUNE', 'Ouvrir et rincer la brique avant de jeter', 10),
                                                                                                    ('Tube de dentifrice', 'dentifrice,tube,plastique,brosse', 'PLASTIQUE', 'JAUNE', 'Vider complètement le tube avant de jeter', 8),

-- ================================================
-- VERRE → Poubelle VERTE
-- ================================================
                                                                                                    ('Bouteille en verre', 'bouteille,verre,vin,bière,alcool,huile', 'VERRE', 'VERTE', 'Déposer dans le conteneur à verre sans le bouchon', 15),
                                                                                                    ('Pot en verre', 'pot,verre,confiture,cornichon,conserve', 'VERRE', 'VERTE', 'Rincer le pot et retirer le couvercle', 15),
                                                                                                    ('Bocal en verre', 'bocal,verre,conserve,sauce', 'VERRE', 'VERTE', 'Rincer et retirer le couvercle métallique', 15),
                                                                                                    ('Bouteille de vin', 'vin,bouteille,verre,alcool,rosé,rouge,blanc', 'VERRE', 'VERTE', 'Déposer dans le conteneur à verre', 15),
                                                                                                    ('Bouteille de bière en verre', 'bière,bouteille,verre,alcool,mousse', 'VERRE', 'VERTE', 'Déposer dans le conteneur à verre', 15),
                                                                                                    ('Flacon de parfum', 'parfum,flacon,verre,cosmétique', 'VERRE', 'VERTE', 'Vider et déposer dans le conteneur à verre', 12),
                                                                                                    ('Verre cassé', 'verre,cassé,brisé,tesson', 'VERRE', 'GRISE', 'Emballer dans du papier journal avant de jeter dans la poubelle grise — dangereux', 5),

-- ================================================
-- PAPIER → Poubelle BLEUE
-- ================================================
                                                                                                    ('Journal', 'journal,presse,magazine,revue,papier', 'PAPIER', 'BLEUE', 'Déposer dans le bac bleu ou le conteneur à papier', 10),
                                                                                                    ('Carton', 'carton,boîte,emballage,colis,amazon', 'PAPIER', 'BLEUE', 'Aplatir le carton avant de le déposer dans le bac bleu', 10),
                                                                                                    ('Feuille de papier', 'feuille,papier,bureau,imprimante,copie', 'PAPIER', 'BLEUE', 'Déposer dans le bac bleu', 8),
                                                                                                    ('Enveloppe', 'enveloppe,courrier,lettre,papier', 'PAPIER', 'BLEUE', 'Retirer la fenêtre plastique si présente', 8),
                                                                                                    ('Livre', 'livre,bouquin,roman,papier,pages', 'PAPIER', 'BLEUE', 'Déposer dans le bac bleu ou donner à une bibliothèque', 10),
                                                                                                    ('Cahier', 'cahier,carnet,notes,papier,scolaire', 'PAPIER', 'BLEUE', 'Retirer les agrafes et déposer dans le bac bleu', 8),
                                                                                                    ('Boîte de céréales', 'céréales,boîte,carton,petit déjeuner', 'PAPIER', 'BLEUE', 'Aplatir et déposer dans le bac bleu', 10),
                                                                                                    ('Papier cadeau', 'cadeau,emballage,papier,ruban', 'PAPIER', 'BLEUE', 'Retirer le ruban adhésif avant de jeter', 8),
                                                                                                    ('Boîte à pizza', 'pizza,boîte,carton,restaurant', 'PAPIER', 'GRISE', 'Boîte souillée par la nourriture → poubelle grise', 5),

-- ================================================
-- ORGANIQUE → Poubelle MARRON
-- ================================================
                                                                                                    ('Épluchures de légumes', 'épluchures,légumes,carotte,pomme de terre,oignon,organique', 'ORGANIQUE', 'MARRON', 'Déposer dans le bac marron ou composter', 12),
                                                                                                    ('Restes de repas', 'restes,repas,nourriture,alimentation,organique', 'ORGANIQUE', 'MARRON', 'Déposer dans le bac marron', 12),
                                                                                                    ('Marc de café', 'café,marc,filtre,organique', 'ORGANIQUE', 'MARRON', 'Le marc de café est excellent pour le compost', 12),
                                                                                                    ('Sachet de thé', 'thé,sachet,infusion,organique', 'ORGANIQUE', 'MARRON', 'Déposer dans le bac marron ou composter', 10),
                                                                                                    ('Coquilles d œuf', 'oeuf,coquille,organique', 'ORGANIQUE', 'MARRON', 'Excellent pour le compost', 12),
                                                                                                    ('Fruits abîmés', 'fruit,abîmé,pourri,organique,banane,pomme', 'ORGANIQUE', 'MARRON', 'Déposer dans le bac marron ou composter', 12),
                                                                                                    ('Gazon tondu', 'gazon,herbe,tonte,jardin,organique', 'ORGANIQUE', 'MARRON', 'Composter ou déposer en déchetterie', 10),
                                                                                                    ('Feuilles mortes', 'feuilles,mortes,jardin,automne,organique', 'ORGANIQUE', 'MARRON', 'Excellent pour le compost ou le paillage', 10),
                                                                                                    ('Pain rassis', 'pain,rassis,organique,nourriture', 'ORGANIQUE', 'MARRON', 'Déposer dans le bac marron', 10),
                                                                                                    ('Os de viande', 'os,viande,organique,alimentation', 'ORGANIQUE', 'MARRON', 'Déposer dans le bac marron', 10),

-- ================================================
-- DANGEREUX → Point de collecte spécial
-- ================================================
                                                                                                    ('Pile électrique', 'pile,batterie,électrique,alcaline,lr06', 'DANGEREUX', 'GRISE', 'Déposer dans un point de collecte piles en magasin', 20),
                                                                                                    ('Batterie téléphone', 'batterie,téléphone,smartphone,lithium,portable', 'DANGEREUX', 'GRISE', 'Déposer en déchetterie ou point de collecte électronique', 20),
                                                                                                    ('Médicaments périmés', 'médicament,périmé,comprimé,pharmacie,pilule', 'DANGEREUX', 'GRISE', 'Rapporter à la pharmacie — ne jamais jeter dans les toilettes', 20),
                                                                                                    ('Huile de vidange', 'huile,vidange,moteur,voiture,lubrifiant', 'DANGEREUX', 'GRISE', 'Déposer en déchetterie dans un bidon fermé', 20),
                                                                                                    ('Peinture', 'peinture,pot,vernis,laque,solvant', 'DANGEREUX', 'GRISE', 'Déposer en déchetterie — ne jamais jeter dans l évier', 20),
                                                                                                    ('Ampoule LED', 'ampoule,led,lumière,lampe,éclairage', 'DANGEREUX', 'GRISE', 'Déposer en magasin ou déchetterie', 20),
                                                                                                    ('Thermomètre à mercure', 'thermomètre,mercure,médical', 'DANGEREUX', 'GRISE', 'Déposer en pharmacie ou déchetterie', 20),
                                                                                                    ('Cartouche d encre', 'cartouche,encre,imprimante,toner', 'DANGEREUX', 'GRISE', 'Retourner en magasin ou déposer en déchetterie', 15),
                                                                                                    ('Produit chimique', 'chimique,produit,acide,base,détergent,nettoyant', 'DANGEREUX', 'GRISE', 'Déposer en déchetterie dans son contenant d origine', 20),
                                                                                                    ('Téléphone portable', 'téléphone,smartphone,portable,mobile,iphone,android', 'DANGEREUX', 'GRISE', 'Déposer en magasin ou déchetterie pour recyclage électronique', 20),

-- ================================================
-- AUTRE → Poubelle GRISE
-- ================================================
                                                                                                    ('Couche bébé', 'couche,bébé,pampers,hygiène', 'AUTRE', 'GRISE', 'Déposer dans la poubelle grise', 5),
                                                                                                    ('Mégot de cigarette', 'mégot,cigarette,tabac,filtre', 'AUTRE', 'GRISE', 'Ne jamais jeter par terre — poubelle grise', 5),
                                                                                                    ('Stylo', 'stylo,bic,crayon,feutre,marqueur', 'AUTRE', 'GRISE', 'Poubelle grise ou point de collecte Terracycle', 5),
                                                                                                    ('Tissu déchiré', 'tissu,vêtement,chiffon,textile,déchiré', 'AUTRE', 'GRISE', 'Si en bon état → donner. Sinon poubelle grise', 5),
                                                                                                    ('Chaussures usées', 'chaussures,usées,basket,soulier,textile', 'AUTRE', 'GRISE', 'Déposer dans une borne textile ou déchetterie', 8),
                                                                                                    ('Jouet cassé', 'jouet,cassé,plastique,enfant', 'AUTRE', 'GRISE', 'Poubelle grise ou déchetterie', 5),
                                                                                                    ('Éponge usée', 'éponge,usée,cuisine,nettoyage', 'AUTRE', 'GRISE', 'Poubelle grise', 5),
                                                                                                    ('Papier aluminium', 'aluminium,papier,alu,cuisine,emballage', 'AUTRE', 'GRISE', 'Poubelle grise si souillé, bac jaune si propre', 5),
                                                                                                    ('Cendre', 'cendre,cheminée,barbecue,feu,charbon', 'AUTRE', 'GRISE', 'Laisser refroidir complètement avant de jeter', 5),
                                                                                                    ('Déchet non identifié', 'inconnu,non identifié,autre,divers', 'AUTRE', 'GRISE', 'Déposez ce déchet dans la poubelle grise (ordures ménagères)', 5);