import os
import base64
import cv2
import numpy as np
import mediapipe as mp

from core.biomech_engine import calculate_user_angles
from analysis.push_up import analyze_push_up
from analysis.pull_up import analyze_pull_up
from analysis.dips import analyze_dips
from utils.cycles import extract_cycles_generic
from config.dynamic_skills import dynamic_skills
from core.movement_classification import classify_movement_type

UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

mp_pose = mp.solutions.pose
mp_drawing = mp.solutions.drawing_utils


def analyze_video_dynamic_service(request):
    if "file" not in request.files:
        return {"status": "error", "message": "Aucune vidéo reçue"}

    video = request.files["file"]
    path = os.path.join(UPLOAD_FOLDER, video.filename)
    video.save(path)

    cap = cv2.VideoCapture(path)
    angles_sequence, timestamps, landmarks_sequence = [], [], []

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
    if movement_type != "dynamic":
        return {"status": "error", "message": "Mouvement non dynamique"}

    signal = [(a["left_elbow"] + a["right_elbow"]) / 2 for a in angles_sequence]
    model = dynamic_skills[figure]

    cycles = extract_cycles_generic(
        signal, timestamps, signal_inverted=(figure == "pull_up")
    )

    if figure == "push_up":
        analysis = analyze_push_up(cycles, model)
    elif figure == "pull_up":
        analysis = analyze_pull_up(cycles, model)
    else:
        analysis = analyze_dips(cycles, model)

    return {
        "status": "ok",
        "analysis_type": "video_dynamic",
        "detected_figure": figure,
        "reps": len(cycles),
        "analysis": analysis
    }
