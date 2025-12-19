"""
Fraud Detection Model - ML Model Wrapper

This module provides a wrapper around the fraud detection model
for loading, inference, and feature extraction.
"""

import os
import pickle
import logging
from typing import Dict, Any, List, Optional

import numpy as np

logger = logging.getLogger(__name__)


class FraudDetectionModel:
    """
    Wrapper class for the fraud detection ML model.
    
    Handles model loading, feature preprocessing, and prediction.
    """
    
    def __init__(self, model_path: str = "model/fraud_model.pkl"):
        self.model_path = model_path
        self.model = None
        self.scaler = None
        self.model_name = "FraudDetector"
        self.model_version = "1.0.0"
        self.threshold = 0.5
        self.feature_names = [
            "amount",
            "hour_of_day",
            "day_of_week",
            "is_weekend",
            "is_night_time",
            "transaction_count_last_24h",
            "total_amount_last_24h",
            "is_new_device",
            "is_new_location",
            "is_new_merchant"
        ]
        self._loaded = False
    
    def is_loaded(self) -> bool:
        """Check if model is loaded."""
        return self._loaded
    
    def load_model(self) -> bool:
        """
        Load the model from disk.
        
        If the model file doesn't exist, creates a simple rule-based model.
        """
        try:
            if os.path.exists(self.model_path):
                logger.info(f"Loading model from {self.model_path}")
                with open(self.model_path, "rb") as f:
                    saved_data = pickle.load(f)
                    self.model = saved_data.get("model")
                    self.scaler = saved_data.get("scaler")
                    self.threshold = saved_data.get("threshold", 0.5)
                    self.model_version = saved_data.get("version", "1.0.0")
                self._loaded = True
                logger.info("Model loaded successfully")
            else:
                logger.warning(f"Model file not found at {self.model_path}, using rule-based fallback")
                self._loaded = True  # Use rule-based model
            
            return True
            
        except Exception as e:
            logger.error(f"Failed to load model: {e}")
            self._loaded = True  # Use rule-based fallback
            return False
    
    def predict(self, features: Dict[str, Any]) -> Dict[str, Any]:
        """
        Make a fraud prediction for the given features.
        
        Args:
            features: Dictionary of feature values
            
        Returns:
            Dictionary with prediction results
        """
        if self.model is not None:
            return self._predict_with_model(features)
        else:
            return self._predict_rule_based(features)
    
    def _predict_with_model(self, features: Dict[str, Any]) -> Dict[str, Any]:
        """Make prediction using the loaded ML model."""
        try:
            # Prepare feature vector
            X = self._prepare_features(features)
            
            # Scale if scaler exists
            if self.scaler is not None:
                X = self.scaler.transform(X)
            
            # Get prediction probability
            if hasattr(self.model, 'predict_proba'):
                proba = self.model.predict_proba(X)[0]
                fraud_probability = proba[1] if len(proba) > 1 else proba[0]
            else:
                fraud_probability = float(self.model.predict(X)[0])
            
            # Make decision
            is_fraud = fraud_probability >= self.threshold
            prediction = "FRAUD" if is_fraud else "LEGITIMATE"
            
            # Calculate confidence
            confidence = abs(fraud_probability - 0.5) * 2
            
            return {
                "probability": fraud_probability,
                "prediction": prediction,
                "is_fraud": is_fraud,
                "confidence": confidence,
                "top_features": self._get_feature_importance(features)
            }
            
        except Exception as e:
            logger.error(f"Model prediction failed: {e}, falling back to rules")
            return self._predict_rule_based(features)
    
    def _predict_rule_based(self, features: Dict[str, Any]) -> Dict[str, Any]:
        """
        Make prediction using rule-based logic.
        
        This is a fallback when no ML model is available.
        """
        score = 0.0
        
        # Amount-based scoring
        amount = features.get("amount", 0)
        if amount > 50000:
            score += 0.4
        elif amount > 10000:
            score += 0.25
        elif amount > 5000:
            score += 0.1
        
        # Time-based scoring
        if features.get("is_night_time"):
            score += 0.15
        
        if features.get("is_weekend"):
            score += 0.05
        
        # Velocity-based scoring
        tx_count = features.get("transaction_count_last_24h", 0)
        if tx_count > 10:
            score += 0.2
        elif tx_count > 5:
            score += 0.1
        
        # Device/Location scoring
        if features.get("is_new_device"):
            score += 0.15
        
        if features.get("is_new_location"):
            score += 0.15
        
        if features.get("is_new_merchant"):
            score += 0.05
        
        # Cap at 1.0
        probability = min(score, 1.0)
        is_fraud = probability >= self.threshold
        prediction = "FRAUD" if is_fraud else "LEGITIMATE"
        confidence = abs(probability - 0.5) * 2
        
        return {
            "probability": probability,
            "prediction": prediction,
            "is_fraud": is_fraud,
            "confidence": confidence,
            "top_features": self._get_rule_based_features(features, score)
        }
    
    def _prepare_features(self, features: Dict[str, Any]) -> np.ndarray:
        """Prepare feature vector for model input."""
        feature_vector = []
        
        for name in self.feature_names:
            value = features.get(name, 0)
            if isinstance(value, bool):
                value = 1 if value else 0
            feature_vector.append(float(value) if value is not None else 0.0)
        
        return np.array([feature_vector])
    
    def _get_feature_importance(self, features: Dict[str, Any]) -> List[Dict]:
        """Get feature importance for the prediction."""
        # Simplified feature importance based on feature values
        importances = []
        
        if features.get("amount", 0) > 10000:
            importances.append({
                "featureName": "amount",
                "importance": 0.3,
                "value": features.get("amount")
            })
        
        if features.get("is_new_location"):
            importances.append({
                "featureName": "is_new_location",
                "importance": 0.2,
                "value": 1
            })
        
        if features.get("is_night_time"):
            importances.append({
                "featureName": "is_night_time",
                "importance": 0.15,
                "value": 1
            })
        
        return importances[:5]  # Return top 5
    
    def _get_rule_based_features(self, features: Dict[str, Any], score: float) -> List[Dict]:
        """Get contributing features for rule-based prediction."""
        contributors = []
        
        amount = features.get("amount", 0)
        if amount > 10000:
            contributors.append({
                "featureName": "amount",
                "importance": 0.25 if amount > 50000 else 0.15,
                "value": amount
            })
        
        if features.get("is_night_time"):
            contributors.append({
                "featureName": "is_night_time",
                "importance": 0.15,
                "value": 1
            })
        
        if features.get("is_new_device"):
            contributors.append({
                "featureName": "is_new_device",
                "importance": 0.15,
                "value": 1
            })
        
        if features.get("is_new_location"):
            contributors.append({
                "featureName": "is_new_location",
                "importance": 0.15,
                "value": 1
            })
        
        tx_count = features.get("transaction_count_last_24h", 0)
        if tx_count > 5:
            contributors.append({
                "featureName": "transaction_count_last_24h",
                "importance": 0.1 if tx_count > 10 else 0.05,
                "value": tx_count
            })
        
        return sorted(contributors, key=lambda x: x["importance"], reverse=True)[:5]
