# Function to calculate angle core
import numpy as np
import logging

logger = logging.getLogger(__name__)

def calculate_angle(a, b, c):
    try:
        a, b, c = np.array(a), np.array(b), np.array(c)
        radians = np.arctan2(c[1]-b[1], c[0]-b[0]) - np.arctan2(a[1]-b[1], a[0]-b[0])
        angle = abs(radians * 180.0 / np.pi)
        return 360-angle if angle > 180 else angle
    except Exception as e:
        logger.error(f"Erreur calcul angle: {e}")
        return 0.0

