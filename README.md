Controle + shift + V pour visualiser le README


# LevelUpCali

Moteur d’analyse biomécanique pour le street workout.

---

## Problématique

Dans le street workout, l’analyse technique repose presque exclusivement sur l’observation visuelle.

Même pour des pratiquants avancés ou des coachs expérimentés, certaines **compensations biomécaniques** sont :  

- difficiles à percevoir,  
- parfois invisibles à l’œil nu,  
- souvent détectées trop tard (stagnation, mauvaise progression, risque de blessure).

L’œil humain juge le rendu visuel, mais le corps, lui, s’adapte mécaniquement.

---

## Solution

**LevelUpCali** est un moteur d’analyse biomécanique spécialisé dans le street workout.  

Il s’appuie sur :  

- la **vision par ordinateur**,  
- des **règles expertes biomécaniques**,  
- une **modélisation logique du corps humain** pour analyser images et vidéos et détecter les compensations invisibles visuellement.

Le système ne se contente pas de dire « figure correcte ou non » : il **identifie les causes biomécaniques sous-jacentes et leurs conséquences** sur le reste du corps.

---

## Fonctionnement

1. Détection des **landmarks corporels** via MediaPipe.  
2. Calcul des **angles articulaires** et des lignes corporelles.  
3. Application de **règles expertes** spécifiques au street workout.  
4. Identification des **défauts techniques** et des compensations associées.  
5. Génération de **feedbacks techniques clairs, hiérarchisés et exploitables**.  

Chaque décision est **explicable, traçable et liée à une logique biomécanique réelle** :  
`Problème → Compensation → Correction`.

---

## Fonctionnalités

### Analyse biomécanique (images & vidéos)
- Détection automatique des articulations.  
- Calcul précis des angles articulaires.  
- Analyse des lignes corporelles (alignement, stabilité).  
- Détection de compensations mécaniques.  

### Figures actuellement analysées
- Pull-ups  
- Dips  
- Pompes  
- Front lever  
- Planche  
- Handstand  

**Exemple (Front Lever) :**  
> Hanches trop basses → compensation au niveau des bras / épaules

### Feedback technique
- Conseils techniques précis.  
- Corrections priorisées selon la cause biomécanique.  
- Feedback compréhensible immédiatement par l’athlète.

### Outils d’entraînement
- Création de programmes d’entraînement.  
- Planning et suivi des progrès.  
- Visualisation graphique de la progression par figure et mouvement.

### Analyse musculaire
- Répartition des muscles sollicités.  
- Visualisation par figure et par mouvement.

---

## Architecture technique

- **Front-end** : Java / JavaFX  
- **Back-end** : Python (Flask)  
- **Vision par ordinateur** : Python (MediaPipe)  
- **Analyse biomécanique** : règles expertes personnalisées  

Les règles expertes garantissent des décisions **fiables et explicables**, adaptées aux exigences du street workout, contrairement aux modèles purement statistiques.

---

## Points techniques clés

- Analyse **images et vidéos** pour un retour immédiat.  
- Tolérances angulaires pour gérer le bruit de mesure.  
- Détection des compensations uniquement via relations multi-articulaires.

---

## Objectifs

Rendre accessible une analyse technique de haut niveau pour :  

- Comprendre les causes de stagnation.  
- Corriger les compensations invisibles.  
- Progresser efficacement.  
- Réduire le risque de blessure.  

**LevelUpCali** est conçu comme un outil d’aide à la décision pour athlètes et coachs.

---

## Améliorations futures

- Analyse biomécanique complète sur toutes les figures.  
- Générateur de programmes entièrement personnalisés.  
- Détection avancée des schémas de stagnation.  
- Enrichissement progressif des règles expertes.

---

## Statut du projet

- Projet en développement actif.  
- Conçu, développé et maintenu par un pratiquant de street workout.  
- Approche biomécanique réelle et orientée performance et sécurité.
