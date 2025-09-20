import csv
import tqdm
import random

random.seed(42)

"""
0  LON 55.4876323
1  LAT 24.9625302
2  NUMBER 47330 61649
3  STREET
4  UNIT
5  CITY
6  DISTRICT  AL MARMOOM
7  REGION
8  POSTCODE
9  ID
10 HASH b0aa70f7d0301027
"""

csv_path = "dubai-en.csv"
data_rows = []
street_map = set()

with open(csv_path, newline="") as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=",")
    header = next(csv_reader)
    [header.pop(i) for i in [10, 9, 7, 6, 4, 1, 0]]
    conv = 0
    for row in tqdm.tqdm(csv_reader):
        LON, LAT, NUMBER, STREET, UNIT, CITY, DISTRICT, REGION, POSTCODE, ID, HASH = row
        if NUMBER and STREET:
            DISTRICT = " ".join(map(str.capitalize, DISTRICT.split()))
            STREET = " ".join(map(str.capitalize, STREET.split()))

            DISTRICT = (
                DISTRICT.replace("FIRST", "1st")
                .replace("SECOND", "2nd")
                .replace("THIRD", "3rd")
                .replace("FOURTH", "4th")
                .replace("Street", "St")
            )
            STREET = (
                STREET.replace("FIRST", "1st")
                .replace("SECOND", "2nd")
                .replace("THIRD", "3rd")
                .replace("FOURTH", "4th")
                .replace("Street", "St")
            )

            street = DISTRICT + " " + " ".join(STREET.split()[1:])
            if street in street_map:
                continue
            street_map.add(street)
            number, postcode = NUMBER.split()
            [row.pop(i) for i in [10, 9, 7, 6, 4, 1, 0]]
            city = "Dubai"

            row[0] = number
            row[1] = street
            row[2] = city
            row[3] = postcode
            data_rows.append(row)
            conv += 1

    data_rows.append(header)
    data_rows = data_rows[::-1]
print(conv, "rows converted")

with open("ae_dubai_en.csv", "w", newline="") as csvfile:
    csv_writer = csv.writer(csvfile, delimiter=",", quotechar='"')
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)
