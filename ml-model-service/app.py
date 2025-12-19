"""
ML Model Service - FastAPI Server for Fraud Detection Scoring

This service provides a REST API for scoring transactions using
a machine learning model trained for fraud detection.
"""

import os
import logging
from datetime import datetime
from typing import Optional, List
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from model import FraudDetectionModel

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# Global model instance
fraud_model: Optional[FraudDetectionModel] = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan context manager for startup/shutdown events."""
    global fraud_model
    
    # Startup
    logger.info("Starting ML Model Service...")
    model_path = os.environ.get("MODEL_PATH", "model/fraud_model.pkl")
    fraud_model = FraudDetectionModel(model_path)
    fraud_model.load_model()
    logger.info("ML Model Service started successfully")
    
    yield
    
    # Shutdown
    logger.info("Shutting down ML Model Service...")


# Create FastAPI application
app = FastAPI(
    title="Fraud Detection ML Service",
    description="Machine Learning service for real-time fraud detection scoring",
    version="1.0.0",
    lifespan=lifespan
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ============================================
# REQUEST/RESPONSE MODELS
# ============================================

class FeatureImportance(BaseModel):
    feature_name: str = Field(..., alias="featureName")
    importance: float
    value: Optional[float] = None

    class Config:
        populate_by_name = True


class PredictionRequest(BaseModel):
    transaction_id: str = Field(..., alias="transactionId")
    amount: float
    user_id: Optional[str] = Field(None, alias="userId")
    merchant_id: Optional[str] = Field(None, alias="merchantId")
    merchant_category: Optional[str] = Field(None, alias="merchantCategory")
    transaction_type: Optional[str] = Field(None, alias="transactionType")
    channel: Optional[str] = None
    hour_of_day: Optional[int] = Field(None, alias="hourOfDay", ge=0, le=23)
    day_of_week: Optional[int] = Field(None, alias="dayOfWeek", ge=1, le=7)
    is_weekend: Optional[bool] = Field(None, alias="isWeekend")
    is_night_time: Optional[bool] = Field(None, alias="isNightTime")
    latitude: Optional[float] = None
    longitude: Optional[float] = None
    distance_from_last_transaction: Optional[float] = Field(None, alias="distanceFromLastTransaction")
    time_since_last_transaction: Optional[int] = Field(None, alias="timeSinceLastTransaction")
    transaction_count_last_24h: Optional[int] = Field(None, alias="transactionCountLast24h")
    total_amount_last_24h: Optional[float] = Field(None, alias="totalAmountLast24h")
    average_transaction_amount: Optional[float] = Field(None, alias="averageTransactionAmount")
    amount_deviation: Optional[float] = Field(None, alias="amountDeviation")
    is_new_device: Optional[bool] = Field(None, alias="isNewDevice")
    is_new_location: Optional[bool] = Field(None, alias="isNewLocation")
    is_new_merchant: Optional[bool] = Field(None, alias="isNewMerchant")

    class Config:
        populate_by_name = True


class PredictionResponse(BaseModel):
    transaction_id: str = Field(..., alias="transactionId")
    fraud_probability: float = Field(..., alias="fraudProbability")
    prediction: str
    is_fraud: bool = Field(..., alias="isFraud")
    confidence: float
    model_name: str = Field(..., alias="modelName")
    model_version: str = Field(..., alias="modelVersion")
    processing_time_ms: int = Field(..., alias="processingTimeMs")
    threshold: float
    top_features: Optional[List[FeatureImportance]] = Field(None, alias="topFeatures")

    class Config:
        populate_by_name = True


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool = Field(..., alias="modelLoaded")
    model_name: str = Field(..., alias="modelName")
    model_version: str = Field(..., alias="modelVersion")
    timestamp: str

    class Config:
        populate_by_name = True


# ============================================
# API ENDPOINTS
# ============================================

@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Health check endpoint."""
    return HealthResponse(
        status="UP",
        model_loaded=fraud_model is not None and fraud_model.is_loaded(),
        model_name=fraud_model.model_name if fraud_model else "unknown",
        model_version=fraud_model.model_version if fraud_model else "unknown",
        timestamp=datetime.utcnow().isoformat() + "Z"
    )


@app.post("/predict", response_model=PredictionResponse)
async def predict(request: PredictionRequest):
    """
    Get fraud prediction for a transaction.
    
    Returns the fraud probability, prediction, and confidence.
    """
    if fraud_model is None or not fraud_model.is_loaded():
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Model not loaded"
        )
    
    try:
        start_time = datetime.utcnow()
        
        # Convert request to feature dict
        features = {
            "amount": request.amount,
            "hour_of_day": request.hour_of_day or 12,
            "day_of_week": request.day_of_week or 3,
            "is_weekend": 1 if request.is_weekend else 0,
            "is_night_time": 1 if request.is_night_time else 0,
            "transaction_count_last_24h": request.transaction_count_last_24h or 0,
            "total_amount_last_24h": request.total_amount_last_24h or 0,
            "is_new_device": 1 if request.is_new_device else 0,
            "is_new_location": 1 if request.is_new_location else 0,
            "is_new_merchant": 1 if request.is_new_merchant else 0,
        }
        
        # Get prediction
        result = fraud_model.predict(features)
        
        processing_time = int((datetime.utcnow() - start_time).total_seconds() * 1000)
        
        # Build response
        return PredictionResponse(
            transaction_id=request.transaction_id,
            fraud_probability=round(result["probability"], 4),
            prediction=result["prediction"],
            is_fraud=result["is_fraud"],
            confidence=round(result["confidence"], 4),
            model_name=fraud_model.model_name,
            model_version=fraud_model.model_version,
            processing_time_ms=processing_time,
            threshold=fraud_model.threshold,
            top_features=result.get("top_features")
        )
        
    except Exception as e:
        logger.error(f"Prediction error for transaction {request.transaction_id}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Prediction failed: {str(e)}"
        )


@app.get("/model/info")
async def model_info():
    """Get information about the loaded model."""
    if fraud_model is None:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Model not loaded"
        )
    
    return {
        "model_name": fraud_model.model_name,
        "model_version": fraud_model.model_version,
        "threshold": fraud_model.threshold,
        "feature_names": fraud_model.feature_names,
        "loaded": fraud_model.is_loaded()
    }


# ============================================
# MAIN ENTRY POINT
# ============================================

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8000))
    host = os.environ.get("HOST", "0.0.0.0")
    
    uvicorn.run(
        "app:app",
        host=host,
        port=port,
        reload=os.environ.get("ENVIRONMENT", "development") == "development",
        log_level="info"
    )
