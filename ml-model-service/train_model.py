"""
Train Fraud Detection Model

Script to train and save a fraud detection model.
Uses synthetic data for demonstration purposes.
"""

import os
import pickle
import logging
from datetime import datetime

import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def generate_synthetic_data(n_samples: int = 10000) -> pd.DataFrame:
    """
    Generate synthetic transaction data for training.
    
    In production, this would be replaced with real historical data.
    """
    np.random.seed(42)
    
    # Generate features
    data = {
        "amount": np.abs(np.random.exponential(500, n_samples)),
        "hour_of_day": np.random.randint(0, 24, n_samples),
        "day_of_week": np.random.randint(1, 8, n_samples),
        "is_weekend": np.random.binomial(1, 0.3, n_samples),
        "is_night_time": np.random.binomial(1, 0.2, n_samples),
        "transaction_count_last_24h": np.random.poisson(3, n_samples),
        "total_amount_last_24h": np.abs(np.random.exponential(1000, n_samples)),
        "is_new_device": np.random.binomial(1, 0.15, n_samples),
        "is_new_location": np.random.binomial(1, 0.1, n_samples),
        "is_new_merchant": np.random.binomial(1, 0.3, n_samples),
    }
    
    df = pd.DataFrame(data)
    
    # Generate labels with realistic fraud patterns
    # Fraud is more likely with: high amount, new device, new location, night time
    fraud_score = (
        (df["amount"] > 10000).astype(int) * 0.3 +
        df["is_night_time"] * 0.15 +
        df["is_new_device"] * 0.2 +
        df["is_new_location"] * 0.25 +
        (df["transaction_count_last_24h"] > 10).astype(int) * 0.15 +
        np.random.uniform(0, 0.3, n_samples)
    )
    
    # Add some random noise
    fraud_score = np.clip(fraud_score + np.random.normal(0, 0.1, n_samples), 0, 1)
    
    # Convert to binary labels
    df["is_fraud"] = (fraud_score > 0.5).astype(int)
    
    # Ensure we have a reasonable fraud rate (around 5-10%)
    if df["is_fraud"].mean() > 0.15:
        # Reduce fraud rate
        fraud_indices = df[df["is_fraud"] == 1].index
        keep_ratio = 0.1 / df["is_fraud"].mean()
        drop_indices = np.random.choice(
            fraud_indices, 
            size=int(len(fraud_indices) * (1 - keep_ratio)), 
            replace=False
        )
        df.loc[drop_indices, "is_fraud"] = 0
    
    logger.info(f"Generated {n_samples} samples with {df['is_fraud'].mean()*100:.1f}% fraud rate")
    
    return df


def train_model(df: pd.DataFrame, output_path: str = "model/fraud_model.pkl"):
    """
    Train a Random Forest model for fraud detection.
    """
    logger.info("Training fraud detection model...")
    
    # Prepare features and target
    feature_columns = [
        "amount", "hour_of_day", "day_of_week", "is_weekend", "is_night_time",
        "transaction_count_last_24h", "total_amount_last_24h",
        "is_new_device", "is_new_location", "is_new_merchant"
    ]
    
    X = df[feature_columns]
    y = df["is_fraud"]
    
    # Split data
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    # Scale features
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)
    
    # Train Random Forest
    model = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        min_samples_split=5,
        min_samples_leaf=2,
        random_state=42,
        n_jobs=-1
    )
    
    model.fit(X_train_scaled, y_train)
    
    # Evaluate
    y_pred = model.predict(X_test_scaled)
    y_proba = model.predict_proba(X_test_scaled)[:, 1]
    
    metrics = {
        "accuracy": accuracy_score(y_test, y_pred),
        "precision": precision_score(y_test, y_pred),
        "recall": recall_score(y_test, y_pred),
        "f1": f1_score(y_test, y_pred),
        "auc_roc": roc_auc_score(y_test, y_proba)
    }
    
    logger.info("Model Performance:")
    for metric, value in metrics.items():
        logger.info(f"  {metric}: {value:.4f}")
    
    # Save model
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    model_data = {
        "model": model,
        "scaler": scaler,
        "threshold": 0.5,
        "version": "1.0.0",
        "feature_names": feature_columns,
        "metrics": metrics,
        "trained_at": datetime.utcnow().isoformat()
    }
    
    with open(output_path, "wb") as f:
        pickle.dump(model_data, f)
    
    logger.info(f"Model saved to {output_path}")
    
    return model, scaler, metrics


if __name__ == "__main__":
    # Generate synthetic data
    df = generate_synthetic_data(n_samples=50000)
    
    # Train and save model
    train_model(df)
    
    logger.info("Training complete!")
