# Static analyze
def analyze_static_hold_unified(figure, angles_data, model):
    """
    Analyse unifiée des figures statiques (image ou vidéo).
    Retourne les mêmes conseils peu importe le type de média.
    """
    if not angles_data:
        return {
            "cause": "Pas de données disponibles",
            "compensation": "N/A",
            "correction": "N/A",
            "deviations": {}
        }

    # Normalisation : convertir en liste si c'est un dict unique (image)
    if isinstance(angles_data, dict):
        angles_sequence = [angles_data]
    else:
        angles_sequence = angles_data

    # Calcul des moyennes sur toute la séquence
    avg_left_elbow = np.mean([a.get("left_elbow", 180) for a in angles_sequence])
    avg_right_elbow = np.mean([a.get("right_elbow", 180) for a in angles_sequence])
    avg_left_hip = np.mean([a.get("left_hip", 180) for a in angles_sequence])
    avg_right_hip = np.mean([a.get("right_hip", 180) for a in angles_sequence])
    avg_left_shoulder = np.mean([a.get("left_shoulder", 180) for a in angles_sequence])
    avg_right_shoulder = np.mean([a.get("right_shoulder", 180) for a in angles_sequence])
    avg_left_knee = np.mean([a.get("left_knee", 180) for a in angles_sequence])
    avg_right_knee = np.mean([a.get("right_knee", 180) for a in angles_sequence])

    deviations = {}
    primary_issue = None

    # ========================================================================
    # HANDSTAND
    # ========================================================================
    if figure == "handstand":
        # Erreur 1 : Hanches fléchies
        if avg_left_hip < model["hip"]["min"] or avg_right_hip < model["hip"]["min"]:
            deviations["hanches_flechies"] = "Oui"
            primary_issue = {
                "cause": "Hanches fléchies, alignement perdu",
                "compensation": "Cambrure lombaire excessive et coudes fléchis pour compenser",
                "correction": "Contracte abdos et fessiers pour aligner le corps verticalement"
            }

        # Erreur 2 : Coudes fléchis
        if (avg_left_elbow < model["elbow"]["min"] or avg_right_elbow < model["elbow"]["min"]) and not primary_issue:
            deviations["coudes_flechis"] = "Oui"
            primary_issue = {
                "cause": "Coudes fléchis pendant le maintien",
                "compensation": "Le dos se cambre pour maintenir l'équilibre",
                "correction": "Verrouille complètement les coudes et engage les triceps"
            }

        # Erreur 3 : Genoux fléchis
        if (avg_left_knee < 170 or avg_right_knee < 170) and not primary_issue:
            deviations["genoux_flechis"] = "Oui"
            primary_issue = {
                "cause": "Genoux fléchis, jambes non tendues",
                "compensation": "Perte d'alignement et instabilité",
                "correction": "Verrouille complètement les genoux, engage les quadriceps"
            }

    # ========================================================================
    # PLANCHE
    # ========================================================================
    elif figure == "planche":
        # Erreur 1 : Hanches basses (bassin abaissé)
        if avg_left_hip < model["hip"]["min"] or avg_right_hip < model["hip"]["min"]:
            deviations["hanches_basses"] = "Oui"
            primary_issue = {
                "cause": "Hanches trop basses, gainage insuffisant",
                "compensation": "Les bras compensent en se pliant pour soutenir le poids",
                "correction": "Renforce le gainage : serre abdos et fessiers, rétroversion du bassin pour monter les hanches"
            }

        # Erreur 2 : Coudes fléchis
        if (avg_left_elbow < model["elbow"]["min"] or avg_right_elbow < model["elbow"]["min"]) and not primary_issue:
            deviations["coudes_flechis"] = "Oui"
            primary_issue = {
                "cause": "Bras fléchis pendant la planche",
                "compensation": "Perte de protraction scapulaire",
                "correction": "Pousse activement dans le sol, verrouille les coudes, engage les scapulas"
            }

        # Erreur 3 : Épaules trop hautes ou trop basses
        if (avg_left_shoulder < model["shoulder"]["min"] or avg_left_shoulder > model["shoulder"]["max"]) and not primary_issue:
            deviations["position_epaules"] = "Oui"
            primary_issue = {
                "cause": "Position des épaules incorrecte",
                "compensation": "Instabilité et perte de protraction scapulaire",
                "correction": "Pousse dans le sol pour protracter les épaules vers l'avant"
            }

    # ========================================================================
    # FRONT LEVER
    # ========================================================================
    elif figure == "front_lever":
        # Erreur 1 : Hanches basses
        if avg_left_hip < model["hip"]["min"] or avg_right_hip < model["hip"]["min"]:
            deviations["hanches_basses"] = "Oui"
            primary_issue = {
                "cause": "Hanches trop basses, le corps n'est pas aligné horizontalement",
                "compensation": "Les bras se plient pour compenser le manque de gainage et de force de traction",
                "correction": "Renforce ton gainage : contracte abdos/fessiers en rétroversion + tire plus fort avec les épaules pour monter les hanches"
            }

        # Erreur 2 : Coudes fléchis
        if (avg_left_elbow < model["elbow"]["min"] or avg_right_elbow < model["elbow"]["min"]) and not primary_issue:
            deviations["coudes_flechis"] = "Oui"
            primary_issue = {
                "cause": "Bras fléchis pendant le front lever",
                "compensation": "Perte de rétraction scapulaire et tension constante",
                "correction": "Verrouille complètement les bras, tire uniquement avec les épaules et le dos"
            }

        # Erreur 3 : Position des épaules incorrecte
        if ((avg_left_shoulder < model["shoulder"]["min"] or avg_left_shoulder > model["shoulder"]["max"]) or
            (avg_right_shoulder < model["shoulder"]["min"] or avg_right_shoulder > model["shoulder"]["max"])) and not primary_issue:
            deviations["position_epaules"] = "Oui"
            primary_issue = {
                "cause": "Position des épaules incorrecte, pas assez de dépression scapulaire",
                "compensation": "Les bras compensent en se pliant",
                "correction": "Tire les épaules vers le bas et vers l'arrière (rétraction + dépression scapulaire)"
            }

    # Si aucune erreur détectée
    if not primary_issue:
        primary_issue = {
            "cause": "Maintien correct de la figure",
            "compensation": "Aucune",
            "correction": "Excellente tenue ! Continue comme ça 💪"
        }

    return {**primary_issue, "deviations": deviations}
