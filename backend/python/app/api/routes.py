from flask import Blueprint, request, jsonify

from services.static_image_service import analyze_static_image_service
from services.video_dynamic_service import analyze_video_dynamic_service
from services.video_static_service import analyze_video_static_service

api = Blueprint("api", __name__)


@api.route("/", methods=["GET"])
def home():
    return jsonify({
        "status": "ok",
        "message": "API d'analyse de mouvement complète",
        "version": "7.0 - Analyse unifiée statique",
        "endpoints": {
            "static": "/analyze_static",
            "video_dynamic": "/analyze_video_dynamic",
            "video_static": "/analyze_video_static"
        }
    })


@api.route("/analyze_static", methods=["POST"])
def analyze_static():
    return jsonify(analyze_static_image_service(request))


@api.route("/analyze_video_dynamic", methods=["POST"])
def analyze_video_dynamic():
    return jsonify(analyze_video_dynamic_service(request))


@api.route("/analyze_video_static", methods=["POST"])
def analyze_video_static():
    return jsonify(analyze_video_static_service(request))
