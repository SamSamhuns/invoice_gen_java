import csv
import tqdm


csv_path = "invoice_gen_java/src/main/resources/common/company/companies_us.csv"
data_rows = []

with open(csv_path, newline='') as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=';')
    _ = next(csv_reader)
    data_rows.append(["name", "domain", "industry", "country", "city", "address1", "address2", "postcode"])

    for row in tqdm.tqdm(csv_reader):
        row = [r.strip() for r in row]
        name, domain, industry, country, address1, address2, postcode = row
        city = address1
        data_rows.append([name, domain, industry, country, city, address1, address2, postcode])


with open('companies_us.csv', 'w', newline='') as csvfile:
    csv_writer = csv.writer(csvfile, delimiter=';', quotechar='"', quoting=csv.QUOTE_ALL)
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)
