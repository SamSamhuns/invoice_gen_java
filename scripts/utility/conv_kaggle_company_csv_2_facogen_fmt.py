import csv
import tqdm
import random
from collections import defaultdict

from faker import Faker

Faker.seed(42)
random.seed(42)
faker_gen = Faker()

"""
0  id x
1  name
2  domain
3  year founded x
4  industry
5  size range x
6  locality x
7  country
8  linkedin url x
9  current employee estimate x
10 total employee estimate x

New added
11 address1
12 address2
"""


csv_path = "../companies_sorted.csv"
data_rows = []
country_count = defaultdict(int)

with open(csv_path, newline='') as csvfile:
    csv_reader = csv.reader(csvfile, delimiter=',')
    header = next(csv_reader)
    [header.pop(i) for i in [10, 9, 8, 6, 5, 3, 0]]
    header.extend(["address1", "address2", "postcode"])

    for row in tqdm.tqdm(csv_reader):
        id, name, domain, year, industry, size_range, locality, country, linkedin, cur_emp_est, total_emp_est = row
        country_count[country] += 1
        if country == '':
            continue
        if country in {"united arab emirates"}:
            if country_count[country] < 1000:
                [row.pop(i) for i in [10, 9, 8, 6, 5, 3, 0]]
                if locality:
                    address1, address2 = locality.split(',')[:2]
                    row.extend([address1, address2, faker_gen.postcode()])
                    data_rows.append(row)

    data_rows.append(header)
    data_rows = data_rows[::-1]


with open('companies_ae_en.csv', 'w', newline='') as csvfile:
    csv_writer = csv.writer(csvfile, delimiter=';', quotechar='"', quoting=csv.QUOTE_ALL)
    for row in tqdm.tqdm(data_rows):
        csv_writer.writerow(row)

# original data
country_count = {
    'united states': 2278866, 'india': 144444, 'ireland': 20426, 'united kingdom': 511969, 'germany': 118575, 'finland': 17193,
    'france': 114706, 'sweden': 34942, '': 2349207, 'netherlands': 136809, 'switzerland': 32742, 'china': 36548, 'canada': 186621,
    'spain': 143941, 'brazil': 108382, 'saudi arabia': 7137, 'australia': 117133, 'turkey': 35350, 'czechia': 12684,
    'united arab emirates': 21639, 'south korea': 2153, 'singapore': 17842, 'south africa': 26631, 'mexico': 33701,
    'taiwan': 3463, 'japan': 8911, 'belgium': 47581, 'italy': 109818, 'colombia': 17448, 'algeria': 1292,
    'norway': 25936, 'qatar': 2323, 'venezuela': 3869, 'indonesia': 14938, 'malaysia': 11088, 'denmark': 29213,
    'luxembourg': 3685, 'hong kong': 8282, 'philippines': 8968, 'austria': 10300, 'chile': 21030, 'new zealand': 19544,
    'kuwait': 1758, 'argentina': 24483, 'peru': 10285, 'greece': 10937, 'liechtenstein': 260, 'oman': 1338, 'russia': 11878,
    'pakistan': 10266, 'lebanon': 3209, 'uruguay': 3036, 'bermuda': 746, 'iran': 7114, 'nigeria': 7584, 'egypt': 8818,
    'poland': 21954, 'thailand': 5675, 'jamaica': 772, 'morocco': 4011, 'romania': 12976, 'bangladesh': 5245, 'israel': 10097,
    'jordan': 2912, 'kenya': 5006, 'srilanka': 3022, 'puerto rico': 1350, 'kazakhstan': 944, 'estonia': 3286, 'costa rica': 3127,
    'portugal': 21586, 'ukraine': 6481, 'ethiopia': 426, 'panama': 2239, 'vietnam': 4933, 'cyprus': 3239, 'tunisia': 2541,
    'hungary': 7064, 'latvia': 2699, 'uganda': 1144, 'serbia': 4705, 'croatia': 4663, 'cayman islands': 369, 'fiji': 1318,
    'slovenia': 3283, 'iceland': 1017, 'palestine': 439, 'zimbabwe': 2858, 'bahrain': 1301, 'ghana': 2016,
    'papua new guinea': 243, 'trinidad and tobago': 479, 'dominican republic': 1882, 'jersey': 583, 'angola': 721,
    'slovakia': 4186, 'bulgaria': 6110, 'guatemala': 1588, 'saint lucia': 318, 'senegal': 688, 'georgia': 1294,
    'ecuador': 3583, 'lithuania': 4256, 'madagascar': 282, 'mauritius': 1009, 'honduras': 528, 'macau': 149,
    'syria': 438, 'cameroon': 1149, 'nepal': 1613, 'iraq': 768, 'malta': 1855, 'zambia': 480, 'tanzania': 1054,
    'monaco': 518, 'azerbaijan': 1205, 'isle of man': 587, 'brunei': 100, 'namibia': 386, 'grenada': 10,
    'botswana': 300, 'bhutan': 58, 'el salvador': 779, 'mozambique': 514, 'french polynesia': 76, 'equatorial guinea': 48,
    'gibraltar': 277, 'cuba': 123, 'belarus': 1049, 'armenia': 1022, 'afghanistan': 376, 'micronesia': 5, 'dominica': 26,
    'sudan': 311, 'côte d’ivoire': 486, 'myanmar': 801, 'bahamas': 301, 'mongolia': 459, 'albania': 775, 'macedonia': 1181,
    'bolivia': 976, 'curaçao': 249, 'suriname': 176, 'paraguay': 754, 'guernsey': 325, 'martinique': 147, 'moldova': 553,
    'bosnia and herzegovina': 997, 'cambodia': 1123, 'new caledonia': 165, 'guam': 54, 'réunion': 73, 'malawi': 168,
    'nicaragua': 545, 'rwanda': 289, 'greenland': 100, 'andorra': 245, 'libya': 346, 'benin': 280, 'burkina faso': 102,
    'maldives': 302, 'democratic republic of the congo': 329, 'uzbekistan': 214, 'belize': 218, 'somalia': 77, 'swaziland': 62,
    'british virgin islands': 280, 'guadeloupe': 180, 'lesotho': 42, 'gambia': 40, 'guinea': 95, 'montenegro': 337,
    'sierra leone': 75, 'french guiana': 62, 'guyana': 49, 'south sudan': 38, 'netherlands antilles': 114, 'niger': 29,
    'american samoa': 6, 'Åland islands': 48, 'kyrgyzstan': 109, 'mali': 75, 'laos': 116, 'yemen': 115, 'liberia': 63,
    'tajikistan': 49, 'aruba': 130, 'saint vincent and the grenadines': 51, 'marshall islands': 18, 'san marino': 120,
    'togo': 167, 'djibouti': 26, 'montserrat': 3, 'faroe islands': 96, 'saint kitts and nevis': 28, 'anguilla': 21, 'kosovo': 85,
    'kiribati': 63, 'cape verde': 70, 'gabon': 117, 'vanuatu': 40, 'mauritania': 38, 'turkmenistan': 27, 'cook islands': 26,
    'seychelles': 132, 'barbados': 165, 'mayotte': 18, 'guinea-bissau': 14, 'tuvalu': 25, 'svalbard and jan mayen': 14,
    'eritrea': 4, 'haiti': 79, 'turks and caicos islands': 60, 'burundi': 35, 'solomon islands': 15,
    'palau': 7, 'northern mariana islands': 14, 'samoa': 24, 'tonga': 6, 'central african republic': 4,
    'são tomé and príncipe': 14, 'western sahara': 3, 'chad': 12, 'caribbean netherlands': 31,
    'saint helena': 1, 'sint maarten': 7, 'timor-leste': 14, 'north korea': 6, 'comoros': 6,
    'antigua and barbuda': 14, 'republic of the congo': 4, 'niue': 1, 'saint martin': 7, 'u.s. virgin islands': 1,
    'norfolk island': 1, 'saint pierre and miquelon': 1, 'saint barthélemy': 1}
