# ============================================
# ML MODEL SERVICE - DOCKERFILE
# ============================================

FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements first for layer caching
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY app.py .
COPY model.py .
COPY train_model.py .

# Create model directory
RUN mkdir -p model

# Train the model during build (optional - can also do at runtime)
RUN python train_model.py || echo "Model training skipped"

# Create non-root user
RUN useradd -m -u 1001 appuser && \
    chown -R appuser:appuser /app
USER appuser

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8000/health || exit 1

# Environment variables
ENV MODEL_PATH=/app/model/fraud_model.pkl
ENV HOST=0.0.0.0
ENV PORT=8000
ENV ENVIRONMENT=production

# Run the application
CMD ["python", "app.py"]
