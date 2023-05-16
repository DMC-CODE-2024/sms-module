#!/bin/bash

csv_file="input.csv"
database="your_database_name"
table="system_configuration_db"
host="your_cloud_host"
port="your_cloud_port"
username="your_database_username"
password="your_database_password"

# Read the CSV file, skipping the header line
tail -n +2 "$csv_file" | while IFS=',' read -r tag description value; do
    query="INSERT INTO $table (tag, description, value) VALUES ('$tag', '$description', '$value');"

    echo "Executing query: $query"
    mysql -h $host -P $port -u $username -p$password $database -e "$query"
done
