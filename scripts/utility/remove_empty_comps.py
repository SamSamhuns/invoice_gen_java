import csv
import tqdm

"""
"name, country, town, address1, address2, postcode"
"""

csv_path = "src/main/resources/common/company/companies_fr.csv"
data_rows = []

with open(csv_path, newline='', encoding="mac_roman") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=';')
    header = next(csv_reader)
    header = ["name", "domain", "industry", "country", "town", "address1", "address2", "postcode"]
    data_rows.append(header)

    for row in tqdm.tqdm(csv_reader):
        name, country, town, address1, address2, postcode = row
        domain = ""
        industry = ""
        data_rows.append([name, domain, industry, country, town, address1, address2, postcode])

with open('companies_fr.csv', 'w', newline='', encoding="mac_roman") as csvfile:
    csv_writer = csv.writer(csvfile, delimiter=';', quotechar='"', quoting=csv.QUOTE_ALL)
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)
