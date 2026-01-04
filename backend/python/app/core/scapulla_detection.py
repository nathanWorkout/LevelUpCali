# Detection of state of scapula (protraction/retraction/neutral)
import mediapipe as mp

mp_pose = mp.solutions.pose

def calculate_scapular_position(landmarks):
    lm = landmarks.landmark
    z_diff = (
        (lm[mp_pose.PoseLandmark.LEFT_SHOULDER].z +
         lm[mp_pose.PoseLandmark.RIGHT_SHOULDER].z) / 2
        -
        (lm[mp_pose.PoseLandmark.LEFT_ELBOW].z +
         lm[mp_pose.PoseLandmark.RIGHT_ELBOW].z) / 2
    )

    return {
        "is_protracted": z_diff > 0.05,
        "is_retracted": z_diff < -0.05,
        "is_neutral": abs(z_diff) <= 0.05
    }
