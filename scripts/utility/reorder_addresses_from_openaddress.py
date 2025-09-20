import csv
import tqdm
import random

random.seed(42)

sample_csv = "dubai-en.csv"
add1_set = set()

with open(sample_csv, newline="") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=",")
    header = next(csv_reader)
    for row in tqdm.tqdm(csv_reader):
        _, _, _, STREET, _, _, DISTRICT, _, _, _, _ = row
        add1_val = ""
        if STREET:
            add1_val = STREET + " " + DISTRICT
        elif DISTRICT:
            add1_val = DISTRICT

        if add1_val:
            add1_val = (
                add1_val.replace("FIRST", "1st")
                .replace("SECOND", "2nd")
                .replace("THIRD", "3rd")
                .replace("FOURTH", "4th")
                .replace("FIFTH", "5th")
            )
            add1_val = add1_val.replace("Street", "St")
            add1_set.add(add1_val)

csv_path = "src/main/resources/common/company/companies_ae_en.csv"
data_rows = []

with open(csv_path, newline="") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=";")
    _ = next(csv_reader)
    header = [
        "name",
        "domain",
        "industry",
        "country",
        "city",
        "address1",
        "address2",
        "postcode",
    ]
    data_rows.append(header)

    for row in tqdm.tqdm(csv_reader):
        name, domain, industry, country, address1, address2, postcode = row
        city = address2
        address1 = ""
        row = [name, domain, industry, country, city, address1, address2, postcode]
        row = [r.strip() for r in row]
        data_rows.append(row)

new_add1s = random.sample(add1_set, len(data_rows) - 1)
for i in range(1, len(data_rows)):
    data_rows[i][5] = new_add1s[i - 1]

with open("companies_ae_en.csv", "w", newline="") as csvfile:
    csv_writer = csv.writer(
        csvfile, delimiter=";", quotechar='"', quoting=csv.QUOTE_ALL
    )
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)
