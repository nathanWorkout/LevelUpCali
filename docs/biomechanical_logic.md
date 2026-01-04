# Control shif V to see

# LevelUpCali

Moteur d’analyse biomécanique pour le street workout

---

## 🎯 Problématique

Dans le street workout, l’analyse technique repose presque exclusivement sur l’observation visuelle.  

Même pour des pratiquants avancés ou des coachs expérimentés, certaines compensations biomécaniques sont :

- difficiles à percevoir,  
- parfois invisibles à l’œil nu,  
- souvent détectées trop tard (stagnation, mauvaise progression, risque de blessure).  

L’œil humain juge le rendu visuel. Le corps, lui, s’adapte mécaniquement.

---

## 💡 Solution

**LevelUpCali** est un moteur d’analyse biomécanique spécialisé dans le street workout.  

Il s’appuie sur :

- la vision par ordinateur,  
- des règles expertes biomécaniques,  
- une modélisation logique du corps humain pour analyser des images (et à terme des vidéos) et détecter des compensations mécaniques invisibles visuellement.

Le système ne se contente pas de dire « la figure est correcte ou non » : il identifie les causes biomécaniques sous-jacentes et leurs conséquences mécaniques sur le reste du corps.

---

## 🧠 Principe de fonctionnement

1. Détection des **landmarks corporels** via la vision par ordinateur  
2. Calcul des **angles articulaires** et des lignes corporelles  
3. Application de **règles expertes** spécifiques au street workout  
4. Identification des **défauts techniques** et des **compensations associées**  
5. Génération de **feedbacks techniques clairs, hiérarchisés et exploitables**  

Chaque décision est explicable, traçable et liée à une logique biomécanique réelle (problème → adaptation → correction).

---

## ⚙️ Fonctionnalités

### Analyse biomécanique

- Détection automatique des articulations  
- Calcul précis des angles articulaires  
- Analyse des lignes corporelles (alignement, stabilité)  
- Détection de compensations mécaniques  

**Exemple (Front Lever)** :  
> hanches trop basses → compensation au niveau des bras / épaules

### 👀 Feedback technique

- Génération de conseils techniques précis  
- Corrections priorisées selon la cause biomécanique  
- Feedback compréhensible immédiatement par l’athlète

### ⚒️ Outils d’entraînement

- Création de programmes d’entraînement  
- Planning d’entraînement  
- Chemins de progression pour différentes figures  
- Suivi de la progression via graphiques

### 📈 Analyse musculaire

- Graphiques de répartition des muscles sollicités  
- Visualisation par figure et par mouvement

---

## 🏗️ Architecture technique

- **Front-end** : Java / JavaFX  
- **Back-end** : Python (Flask)  
- **Vision par ordinateur** : Python (MediaPipe)  
- **Analyse biomécanique** : règles expertes personnalisées  

Le choix d’un système basé sur des règles expertes permet :

- des décisions fiables,  
- une analyse explicable,  
- une adaptation précise aux exigences du street workout, contrairement à des modèles purement statistiques ou opaques.

---

## ⚠️ Choix techniques importants

- Utilisation de **tolérances angulaires** pour gérer le bruit de mesure  
- Détection des **compensations uniquement via relations multi-articulaires**

---

## 🚀 Objectif du projet

Rendre accessible une analyse technique de haut niveau en street workout et aider les pratiquants à :

- comprendre pourquoi ils stagnent,  
- corriger des compensations invisibles,  
- progresser plus efficacement,  
- réduire le risque de blessure.

LevelUpCali vise à être un **outil d’aide à la décision technique**, aussi bien pour les athlètes que pour les coachs.

---

## 🔮 Améliorations futures

- Analyse biomécanique complète sur vidéo et sur de nombreuses autres figures  
- Générateur de programmes entièrement personnalisés  
- Détection avancée des **schémas de stagnation**  
- Enrichissement progressif des règles expertes

---

## 📌 Statut du projet

Projet en **développement actif**.  
Conçu, développé et maintenu par un pratiquant de street workout, avec une approche **biomécanique réelle et applicative**.
