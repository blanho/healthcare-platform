#!/bin/bash
# =============================================
# LocalStack Initialization Script
# Creates S3 buckets for medical attachments
# =============================================

echo "Creating S3 buckets..."

awslocal s3 mb s3://healthcare-attachments
awslocal s3 mb s3://healthcare-documents
awslocal s3 mb s3://healthcare-backups

echo "Configuring bucket policies..."

# Set CORS for attachments bucket
awslocal s3api put-bucket-cors --bucket healthcare-attachments --cors-configuration '{
  "CORSRules": [
    {
      "AllowedOrigins": ["http://localhost:3000"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedHeaders": ["*"],
      "MaxAgeSeconds": 3000
    }
  ]
}'

echo "LocalStack initialization complete!"
