import csv
import tqdm

"""
name
domain
industry
country
address1
address2
postcode
"""

csv_path = "src/main/resources/common/company/companies_us.csv"
data_rows = []

with open(csv_path, newline='') as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=';')
    header = next(csv_reader)
    data_rows.append(header)

    for row in tqdm.tqdm(csv_reader):
        name, domain, industry, country, address1, address2, postcode = row
        name = ' '.join([string.capitalize() for string in name.split()])
        data_rows.append([name, domain, industry, country, address1, address2, postcode])

with open('companies_us.csv', 'w', newline='') as csvfile:
    csv_writer = csv.writer(csvfile, delimiter=';', quotechar='"', quoting=csv.QUOTE_ALL)
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)
