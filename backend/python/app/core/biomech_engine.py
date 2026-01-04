# Calculate user angles between 3 points of core
from core.angle_calculator import calculate_angle
from core.pose_estimation import get_landmark_points

def calculate_user_angles(landmarks):
    joints = {
        "left_elbow": ["left_shoulder", "left_elbow", "left_wrist"],
        "right_elbow": ["right_shoulder", "right_elbow", "right_wrist"],
        "left_shoulder": ["left_elbow", "left_shoulder", "left_hip"],
        "right_shoulder": ["right_elbow", "right_shoulder", "right_hip"],
        "left_hip": ["left_shoulder", "left_hip", "left_knee"],
        "right_hip": ["right_shoulder", "right_hip", "right_knee"],
        "left_knee": ["left_hip", "left_knee", "left_ankle"],
        "right_knee": ["right_hip", "right_knee", "right_ankle"]
    }

    angles = {}
    for joint, pts in joints.items():
        a = get_landmark_points(landmarks, pts[0])
        b = get_landmark_points(landmarks, pts[1])
        c = get_landmark_points(landmarks, pts[2])
        angles[joint] = float(calculate_angle(a, b, c))

    return angles

