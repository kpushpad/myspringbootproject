import csv
import requests
import time

CSV_FILE = 'kvstore_test_data.csv'
ENDPOINT = 'http://localhost:8080/kvstore'

# Optional: delay between requests (in seconds)
DELAY_BETWEEN_REQUESTS = 0.1  # 100ms

HEADERS = {
    "Content-Type": "application/json",
    "Authorization" : "Basic dXNlcjpwYXNzd29yZA==",
    "Accept": "*/*"
}

def send_post_request(row):
    payload = {
        "key": row['key'],
        "value": row['value'],
        "ttl": row['ttl']
    }
    try:
        response = requests.post(ENDPOINT, json=payload,headers=HEADERS)
        print(f"Sent: {payload} => Status: {response.status_code}")
    except requests.exceptions.RequestException as e:
        print(f"Request failed for {payload['key']}: {e}")

def run():
    with open(CSV_FILE, mode='r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            send_post_request(row)
            time.sleep(DELAY_BETWEEN_REQUESTS)

if __name__ == '__main__':
    run()
