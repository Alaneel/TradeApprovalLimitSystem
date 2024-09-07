import streamlit as st
import requests
import pandas as pd
from datetime import datetime
import time
import json

# Set the base URL for your Spring Boot backend
BASE_URL = "http://localhost:8080/api/trader"

def verify_instrument(instrument_verification_request):
    try:
        response = requests.post(f"{BASE_URL}/verify-instrument", json=instrument_verification_request)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"error": str(e)}

def get_available_instruments():
    try:
        response = requests.get(f"{BASE_URL}/instruments")
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return []

def create_approval_request(instrument_verification_request):
    try:
        response = requests.post(f"{BASE_URL}/approval-request", json=instrument_verification_request)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"error": str(e)}

def get_available_limit(counterparty, instrument_group):
    try:
        response = requests.get(f"{BASE_URL}/limit/{counterparty}/{instrument_group}")
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"error": f"Request failed: {str(e)}"}
    except json.JSONDecodeError:
        return {"error": "Invalid JSON response from server"}
    except Exception as e:
        return {"error": f"Unknown error: {str(e)}"}

def execute_trade(trade_request):
    try:
        response = requests.post(f"{BASE_URL}/trade", json=trade_request)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        return {"error": f"Request failed: {str(e)}"}

st.title("Trading System Dashboard")

# Instrument Verification
st.subheader("Instrument Verification")
col1, col2 = st.columns(2)
with col1:
    instrument_group = st.text_input("Instrument Group")
    instrument = st.text_input("Instrument")
    settlement_currency = st.text_input("Settlement Currency")
    trade_currency = st.text_input("Trade Currency")
with col2:
    country = st.text_input("Country")
    exchange = st.text_input("Exchange")
    department = st.text_input("Department")

if st.button("Verify Instrument"):
    instrument_verification_request = {
        "instrumentGroup": instrument_group,
        "settlementCurrency": settlement_currency,
        "tradeCurrency": trade_currency,
        "country": country,
        "exchange": exchange,
        "department": department
    }
    result = verify_instrument(instrument_verification_request)
    if "error" not in result:
        st.json(result)
        if not result["valid"]:
            if st.button("Submit Approval Request"):
                approval_result = create_approval_request(instrument_verification_request)
                if "error" not in approval_result:
                    st.json(approval_result)
                else:
                    st.error(f"Error submitting approval request: {approval_result['error']}")
    else:
        st.error(f"Error: {result['error']}")

# Available Limit
st.subheader("Available Limit")
limit_counterparty = st.text_input("Enter Counterparty")
limit_instrument_group = st.text_input("Enter Instrument Group")
if st.button("Get Available Limit"):
    limit = get_available_limit(limit_counterparty, limit_instrument_group)
    if "error" not in limit:
        st.write(f"Available Limit: {limit['availableLimit']}")
    else:
        st.error(f"Error fetching limit: {limit['error']}")

# Trade Execution Section
st.subheader("Execute Trade")

trade_instrument_group = st.text_input("Instrument Group for Trade")
trade_counterparty = st.text_input("Counterparty for Trade")
trade_amount = st.number_input("Trade Amount", min_value=0.0)

if st.button("Execute Trade"):
    with st.spinner("Executing trade..."):
        trade_request = {
            "instrumentVerificationRequest": {
                "instrumentGroup": trade_instrument_group,
                "settlementCurrency": settlement_currency,
                "tradeCurrency": trade_currency,
                "country": country,
                "exchange": exchange,
                "department": department
            },
            "counterparty": trade_counterparty,
            "amount": trade_amount
        }
        trade_result = execute_trade(trade_request)
        if "error" in trade_result:
            st.error(f"Trade execution failed: {trade_result['error']}")
        else:
            st.success(f"Trade result: {trade_result['status']} - {trade_result['message']}")

# Display Trade History
st.subheader("Trade History")
if st.button("Fetch Trade History"):
    try:
        response = requests.get(f"{BASE_URL}/trades")
        response.raise_for_status()
        trades = response.json()
        if trades:
            df = pd.DataFrame(trades)
            st.dataframe(df)
        else:
            st.info("No trade history available.")
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching trade history: {str(e)}")