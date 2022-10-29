import csv
import tqdm
import random
from collections import defaultdict

from faker import Faker

Faker.seed(42)
random.seed(42)
faker_gen = Faker()

"""
name
country
address1
address2
postcode
"""


csv_path = "src/main/resources/common/company/companies_fr.csv"
data_rows = []
country_count = defaultdict(int)

with open(csv_path, newline='', encoding="mac_roman") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=';')
    row = next(csv_reader)
    print(row[36], row[8], row[28], row[5], row[6], row[20])
    header = ["name", "country", "town", "address1", "address2", "postcode"]
    data_rows.append(header)

    for row in tqdm.tqdm(csv_reader):
        name, country, town = row[36], row[8], row[28]
        address1, address2, postcode = row[5], row[6], row[20]
        data_rows.append([name, country, town, address1, address2, postcode])


with open('companies_fr.csv', 'w', newline='', encoding="mac_roman") as csvfile:
    csv_writer = csv.writer(csvfile, delimiter=';', quotechar='"', quoting=csv.QUOTE_ALL)
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)
