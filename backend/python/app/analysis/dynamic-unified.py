# Analyze for dynamic movement
def analyze_push_up(cycles, model):
    """Analyse des pompes."""
    if not cycles:
        return {
            "cause": "Aucun cycle détecté",
            "compensation": "N/A",
            "correction": "N/A",
            "deviations": {}
        }

    avg_min = np.mean([c["min_value"] for c in cycles])
    avg_max = np.mean([c["max_value"] for c in cycles])
    avg_amplitude = np.mean([c["amplitude"] for c in cycles])
    avg_duration = np.mean([c["duration"] for c in cycles])

    deviations = {}
    primary_issue = None

    if avg_min > model["elbow"]["min"] + 10:
        deviations["amplitude_insuffisante"] = "Oui"
        primary_issue = {
            "cause": "Tu ne descends pas assez, tes coudes restent trop hauts",
            "compensation": "Le dos se cambre pour compenser le manque de flexion",
            "correction": "Descends jusqu'à 90° de flexion des coudes"
        }

    if avg_max < model["elbow"]["max"] - 10 and not primary_issue:
        deviations["verrouillage_incomplet"] = "Oui"
        primary_issue = {
            "cause": "Tu n'étends pas complètement les coudes en haut",
            "compensation": "Les épaules compensent le manque d'extension",
            "correction": "Verrouille complètement les coudes en position haute"
        }

    if avg_duration < 0.8 and not primary_issue:
        deviations["execution_rapide"] = "Oui"
        primary_issue = {
            "cause": "Exécution trop rapide, mouvement non contrôlé",
            "compensation": "Le corps réduit l'amplitude pour suivre le rythme",
            "correction": "Ralentir: 1-2 secondes par phase"
        }

    if not primary_issue:
        primary_issue = {
            "cause": "Exécution correcte",
            "compensation": "Aucune",
            "correction": "Continuer avec cette technique"
        }

    return {**primary_issue, "deviations": deviations}

def analyze_pull_up(cycles, model):
    """Analyse des tractions."""
    if not cycles:
        return {
            "cause": "Aucun cycle détecté",
            "compensation": "N/A",
            "correction": "N/A",
            "deviations": {}
        }

    avg_min = np.mean([c["min_value"] for c in cycles])
    avg_max = np.mean([c["max_value"] for c in cycles])
    avg_amplitude = np.mean([c["amplitude"] for c in cycles])

    deviations = {}
    primary_issue = None

    if avg_min > model["elbow"]["min"] + 15:
        deviations["amplitude_insuffisante"] = "Oui"
        primary_issue = {
            "cause": "Tu ne montes pas assez haut : le menton ne dépasse pas la barre",
            "compensation": "Le mouvement est raccourci pour rester fluide et enchaîner les répétitions",
            "correction": "Tire un peu plus haut à chaque répétition, quitte à faire moins de reps mais complètes"
        }

    if avg_max < model["elbow"]["max"] - 15 and not primary_issue:
        deviations["extension_incomplete"] = "Oui"
        primary_issue = {
            "cause": "Tu ne descends pas complètement bras tendus entre les répétitions",
            "compensation": "Tu gardes une tension constante pour éviter la partie la plus difficile du mouvement",
            "correction": "Marque une vraie position basse avec les bras tendus avant de repartir (scapula relâché)"
        }

    if not primary_issue:
        primary_issue = {
            "cause": "Amplitude et contrôle corrects sur l'ensemble des répétitions",
            "compensation": "Aucune",
            "correction": "Excellente technique, continue !"
        }

    return {**primary_issue, "deviations": deviations}

def analyze_dips(cycles, model):
    """Analyse des dips."""
    if not cycles:
        return {
            "cause": "Aucun cycle détecté",
            "compensation": "N/A",
            "correction": "N/A",
            "deviations": {}
        }

    avg_min = np.mean([c["min_value"] for c in cycles])
    avg_max = np.mean([c["max_value"] for c in cycles])
    avg_amplitude = np.mean([c["amplitude"] for c in cycles])

    deviations = {}
    primary_issue = None

    if avg_min > model["elbow"]["min"] + 10:
        deviations["profondeur_insuffisante"] = "Oui"
        primary_issue = {
            "cause": "Tu ne descends pas assez bas, les coudes restent trop ouverts",
            "compensation": "Le mouvement est volontairement raccourci pour rester confortable et stable",
            "correction": "Descends progressivement plus bas jusqu'à atteindre au moins 90° de flexion des coudes"
        }

    if avg_max < model["elbow"]["max"] - 10 and not primary_issue:
        deviations["verrouillage_incomplet"] = "Oui"
        primary_issue = {
            "cause": "Tu ne verrouilles pas complètement les bras en position haute",
            "compensation": "Tu évites la fin du mouvement pour garder du rythme ou réduire l'effort",
            "correction": "Marque une vraie position haute avec les bras tendus avant de redescendre"
        }

    if not primary_issue:
        primary_issue = {
            "cause": "Amplitude et contrôle corrects sur l'ensemble des répétitions",
            "compensation": "Aucune",
            "correction": "Technique solide, maintiens-la !"
        }

    return {**primary_issue, "deviations": deviations}