import os
import cv2
import mediapipe as mp

from core.biomech_engine import calculate_user_angles
from analysis.static_unified import analyze_static_hold_unified
from config.static_skills import static_skills
from core.movement_classification import classify_movement_type

UPLOAD_FOLDER = "uploads"
mp_pose = mp.solutions.pose


def analyze_video_static_service(request):
    if "file" not in request.files:
        return {"status": "error", "message": "Aucune vidéo reçue"}

    video = request.files["file"]
    path = os.path.join(UPLOAD_FOLDER, video.filename)
    video.save(path)

    cap = cv2.VideoCapture(path)
    angles_sequence, landmarks_sequence, timestamps = [], [], []

    with mp_pose.Pose(min_detection_confidence=0.7, min_tracking_confidence=0.7) as pose:
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

            ts = cap.get(cv2.CAP_PROP_POS_MSEC) / 1000
            results = pose.process(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))

            if results.pose_landmarks:
                landmarks_sequence.append(results.pose_landmarks)
                angles_sequence.append(calculate_user_angles(results.pose_landmarks))
                timestamps.append(ts)

    cap.release()

    movement_type, figure = classify_movement_type(landmarks_sequence)
    if movement_type != "static":
        return {"status": "error", "message": "Mouvement non statique"}

    analysis = analyze_static_hold_unified(
        figure, angles_sequence, static_skills[figure]
    )

    hold_duration = timestamps[-1] - timestamps[0] if len(timestamps) > 1 else 0

    return {
        "status": "ok",
        "analysis_type": "video_static",
        "detected_figure": figure,
        "hold_duration": round(hold_duration, 2),
        "analysis": analysis
    }
