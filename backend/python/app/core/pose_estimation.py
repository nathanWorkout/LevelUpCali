# Pose estimation
import mediapipe as mp
import logging

logger = logging.getLogger(__name__)
mp_pose = mp.solutions.pose

def get_landmark_points(landmarks, name):
    try:
        lm = landmarks.landmark[mp_pose.PoseLandmark[name.upper()]] # Get landmarks 
        return [lm.x, lm.y]
    except Exception as e:
        logger.error(f"Erreur landmark {name}: {e}")
        return [0.0, 0.0]
