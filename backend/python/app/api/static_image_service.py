import base64
import cv2
import numpy as np
import mediapipe as mp

from core.biomech_engine import calculate_user_angles
from core.scapulla_detection import calculate_scapular_position
from analysis.static_unified import analyze_static_hold_unified
from config.static_skills import static_skills
from core.movement_classification import detect_static_figure

mp_pose = mp.solutions.pose
mp_drawing = mp.solutions.drawing_utils


def analyze_static_image_service(request):
    if not request.is_json:
        return {"status": "error", "message": "Requête non JSON"}

    data = request.get_json()
    image_base64 = data.get("image_base64")

    if not image_base64:
        return {"status": "error", "message": "Aucune image reçue"}

    try:
        image_bytes = base64.b64decode(image_base64)
        nparr = np.frombuffer(image_bytes, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    except:
        return {"status": "error", "message": "Image invalide"}

    with mp_pose.Pose(static_image_mode=True, min_detection_confidence=0.7) as pose:
        image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        results = pose.process(image_rgb)

        if not results.pose_landmarks:
            return {"status": "error", "message": "Aucun corps détecté"}

        scapular_info = calculate_scapular_position(results.pose_landmarks)
        user_angles = calculate_user_angles(results.pose_landmarks)

        figure = detect_static_figure([results.pose_landmarks])

        if figure in static_skills:
            analysis = analyze_static_hold_unified(
                figure, user_angles, static_skills[figure]
            )
        else:
            analysis = {
                "cause": "Figure non reconnue",
                "compensation": "N/A",
                "correction": "N/A",
                "deviations": {}
            }

        mp_drawing.draw_landmarks(
            image, results.pose_landmarks, mp_pose.POSE_CONNECTIONS
        )

        _, buffer = cv2.imencode(".jpg", image)
        image_out = base64.b64encode(buffer).decode("utf-8")

        return {
            "status": "ok",
            "analysis_type": "static_image",
            "detected_figure": figure,
            "image_base64": image_out,
            "analysis": analysis,
            "scapular_info": scapular_info
        }
