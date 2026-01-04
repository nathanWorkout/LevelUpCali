# To analysis dynamic

import numpy as np
from scipy.signal import find_peaks, savgol_filter

def extract_cycles_generic(signal, timestamps, signal_inverted=False):
    if len(signal) < 10:
        return []

    window = min(11, len(signal)//2*2+1)
    smoothed = savgol_filter(signal, window, 3)

    if signal_inverted:
        peaks, _ = find_peaks(smoothed)
        valleys, _ = find_peaks(-smoothed)
    else:
        valleys, _ = find_peaks(-smoothed)
        peaks, _ = find_peaks(smoothed)

    cycles = []
    for i in range(min(len(peaks), len(valleys))):
        start, end = peaks[i], valleys[i]
        if start < end:
            cycles.append({
                "min_value": float(min(smoothed[start:end])),
                "max_value": float(max(smoothed[start:end])),
                "amplitude": float(max(smoothed[start:end]) - min(smoothed[start:end])),
                "duration": float(timestamps[end] - timestamps[start])
            })
    return cycles
