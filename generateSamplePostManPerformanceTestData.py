import csv
import json

# Number of test records to generate
num_records = 100

# CSV file generation
csv_filename = 'kvstore_test_data.csv'
with open(csv_filename, mode='w', newline='') as csv_file:
    fieldnames = ['key', 'value', 'ttl']
    writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
    
    writer.writeheader()
    for i in range(1, num_records + 1):
        writer.writerow({
            'key': f'test{i}',
            'value': f'value{i}',
            'ttl': str(100 + i * 10)
        })

print(f"✅ CSV file '{csv_filename}' generated successfully.")

# JSON file generation
json_filename = 'kvstore_test_data.json'
json_data = [
    {
        'key': f'test{i}',
        'value': f'value{i}',
        'ttl': str(100 + i * 10)
    }
    for i in range(1, num_records + 1)
]

with open(json_filename, 'w') as json_file:
    json.dump(json_data, json_file, indent=4)

print(f"✅ JSON file '{json_filename}' generated successfully.")

